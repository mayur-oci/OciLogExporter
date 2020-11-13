# This script creates OCI linux compute instance and its required pre-requisite resource like VCN, subnets and internet gateway etc
# Oci region for these resources is based on your current profile of oci cli config
# Please setup following parameter as per your tenancy and credentials
    export COMPARTMENT_NAME='TestCmpt_0'
    export COMPUTE_NAME='ComputeForLogExporter'
    export COMPUTE_SHAPE='VM.Standard1.4'
    export OCI_HOME_REGION='us-ashburn-1'
    export OCI_TENANCY_OCID='ocid1.tenancy.oc1..aaaaaaaaopbu45aomik7sswe4nzzll3f6ii6pipd5ttw4ayoozez37qqmh3a'
    export USER_HOME=$(eval echo ~)

    # Use your ssh public key file location here. Required for SSH connection to the compute instance.
    export SSH_PUBLIC_KEY_LOCATION="/Users/mraleras/sshkeypair1.key.pub"
    export SSH_PRIVATE_KEY_LOCATION="/Users/mraleras/sshkeypair1.key.pvt"


    # Image decides the operating system for the compute instance.
    # We need Oracle Linux since the scripts are tested on this OS.
    # Image ocid depends on region get image ocid from https://docs.cloud.oracle.com/en-us/iaas/images/image/96068886-76e5-4a48-af0a-fa7ed8466a25/
    export IMAGE_OCID='ocid1.image.oc1.phx.aaaaaaaaym3vkgeag7mn3csoxxvk6gdirryocsubuv2xvgefhi2wrwytp2gq'

# Compartment OCID
    export COMPARTMENT_ID=$(oci iam compartment list --query "data[?name=='${COMPARTMENT_NAME}'].id | [0]" --raw-output)

# Availability Domain
    export AVAILABILITY_DOMAIN=$(oci iam availability-domain list --query "(data[?ends_with(name, '-3')] | [0].name) || data[0].name" --raw-output)
    echo AVAILABILITY_DOMAIN chosen $AVAILABILITY_DOMAIN

# Create a new VCN
    export VCN_ID=$(oci network vcn create -c ${COMPARTMENT_ID} --cidr-block "10.0.0.0/16" --query "data.id" --raw-output)

# Add a Subnet to the VCN
    export SUBNET_ID=$(oci network subnet create --vcn-id ${VCN_ID} -c ${COMPARTMENT_ID} --cidr-block "10.0.0.0/24" --query "data.id" --raw-output)

# Add an Internet Gateway
    export IG_ID=$(oci network internet-gateway create -c ${COMPARTMENT_ID} --is-enabled true --vcn-id ${VCN_ID} --wait-for-state AVAILABLE --query "data.id" --raw-output)

# Add a Route Table
    export RT_ID=$(oci network route-table list -c ${COMPARTMENT_ID} --vcn-id ${VCN_ID} --query "data[0].id" --raw-output)

# Add a Route Rule for the Internet Gateway
    oci network route-table update --rt-id ${RT_ID} --route-rules '[{"cidrBlock":"0.0.0.0/0","networkEntityId":"'${IG_ID}'"}]' --force

# Update security list for allowing traffic on port 8080
    SECURITY_LIST_ID=$(oci network vcn get --vcn-id ${VCN_ID} --query 'data."default-security-list-id"' --raw-output)

    curl -O https://gist.githubusercontent.com/mayur-oci/ba3c76f50ca60445c9807effbf695706/raw/86cb986ee9fd5afd134f81fd0481550212223071/ingress.json
    curl -O https://gist.githubusercontent.com/mayur-oci/2529ba1a3a3fed582631407435accff6/raw/ee07bc4984f0956972dd2379fc7d4491a527c29e/egress.json

    OCI_SECURITY_LIST_UPDATE=$(oci network security-list update --security-list-id ${SECURITY_LIST_ID} \
                            --ingress-security-rules file://`pwd`/ingress.json  \
                            --egress-security-rules file://`pwd`/egress.json --force)
    rm -rf ingress.json
    rm -rf egress.json

# Create the Compute Instance
    export COMPUTE_OCID=$(oci compute instance launch \
                            -c ${COMPARTMENT_ID} \
                            --shape "${COMPUTE_SHAPE}" \
                            --display-name "${COMPUTE_NAME}" \
                            --image-id ${IMAGE_OCID} \
                            --ssh-authorized-keys-file "${SSH_PUBLIC_KEY_LOCATION}" \
                            --subnet-id ${SUBNET_ID} \
                            --availability-domain "${AVAILABILITY_DOMAIN}" \
                            --wait-for-state RUNNING \
                            --query "data.id" \
                            --raw-output)


# Get the public IP for the compute instance
    export COMPUTE_IP=$(oci compute instance list-vnics \
    --instance-id "${COMPUTE_OCID}" \
    --query 'data[0]."public-ip"' \
    --raw-output)
    echo 'The OCI Oracle Linux Compute Instance IP is' $COMPUTE_IP

# Create Dynamic group and policy for your above instance to
#   1- read logs from OCI logging service. This enables code running on this instance to make for SearchLogs api calls.
#   2- write to your bucket in the same above compartment
    MATCHING_RULE_FOR_DG="ANY {instance.id = '${COMPUTE_OCID}'}"
    DG_NAME='dg_for_log_exporter'_$(date "+DATE_%Y_%m_%d_TIME_%H_%M_%S")
    DG_ID=$(oci --region $OCI_HOME_REGION iam dynamic-group create --description 'dg_for_log_exporter' --name 'dg_for_log_exporter' --matching-rule "$MATCHING_RULE_FOR_DG" --wait-for-state ACTIVE --query "data.id" --raw-output)

    DG_POLICY="[\"Allow dynamic-group dg_for_log_exporter to use log-content in tenancy \", \"Allow dynamic-group dg_for_log_exporter to manage objects in compartment $COMPARTMENT_NAME where any {request.permission='OBJECT_CREATE', request.permission='OBJECT_READ', request.permission='OBJECT_INSPECT'} \"]"
    echo $DG_POLICY > statements.json
    DG_POLICY_ID=$(oci iam policy create -c $OCI_TENANCY_OCID --name "DG_POLICY_$COMPARTMENT_NAME" --description "A policy for instance" --statements file://`pwd`/statements.json --region ${OCI_HOME_REGION} --raw-output --query "data.id" --wait-for-state ACTIVE)
    echo Created policy ${DG_POLICY_ID}.  Use the command: \'oci iam policy get --policy-id "${DG_POLICY_ID}"\' if you want to view the policy.
    rm -rf statements.json

# SSH into the node, set it up JDK 11, configure firewall and run the exporter
    export GIT_SETUP_EXPORTER="https://raw.githubusercontent.com/mayur-oci/OciLogExporter/master/AutomationScripts/SetupOciInstanceForLogExporter.sh"
    ssh -i $SSH_PRIVATE_KEY_LOCATION opc@$COMPUTE_IP -o ServerAliveInterval=60 -o "StrictHostKeyChecking no" \
           "curl -O $GIT_SETUP_EXPORTER; chmod 777 SetupOciInstanceForLogExporter.sh"
    echo;echo;echo "Run the Script for setup after with root privileges aka 'sudo ./SetupOciInstanceForLogExporter.sh' on the instance"

    ssh -i $SSH_PRIVATE_KEY_LOCATION opc@$COMPUTE_IP -o ServerAliveInterval=60 -o "StrictHostKeyChecking no"






