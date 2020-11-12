#!/bin/bash

# This script needs root privileges
#   sudo su

# Disable firewall of OCI linux node for HTTP and other communication to node.
# This is needed in addition to security lists changes for the subnet for this node
    sestatus
    setenforce 0
    search='SELINUX=enforcing'
    replace='SELINUX=disabled'
    sed -i "s/${search}/${replace}/g" /etc/selinux/config
    systemctl stop firewalld
    systemctl disable firewalld

# Upgrade yum package manager
    yum upgrade -y -q

# Install git for fetching the code
    yum install -y git

# Install java-11
    yum install -y java-11-openjdk-devel

# Set Java 11 as default since Oracle linux has Java8 as default in-case OCI linux node has multiple JDK versions
    JAVA_11=$(alternatives --display java | grep 'family java-11-openjdk' | cut -d' ' -f1)
    alternatives --set java $JAVA_11
    java --version
    javac -version

# Fetch and run LogExporter. Please note since LogExporter is Spring Boot application, the maven is prepackaged with its code repo
    git clone https://github.com/mayur-oci/OciLogExporter.git
    cd OciLogExporter
    # Arguments '-Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000' are optional needed in-case you want to develop and debug the code
    ./mvnw spring-boot:run -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000



