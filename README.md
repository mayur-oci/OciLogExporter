# OciLogExporter

## Introduction
   This is Spring-Boot based Java web app which helps you export your OCI logs from OCI Logging Service to Oracle Cloud Object Storage.
   
   You submit jobs for exporting the logs using rest based api. Once you launch job with this rest api, it starts a single thread which does the job of exporting logs to object storage.
   The thread fetches logs [OCI SearchLogs API](https://docs.cloud.oracle.com/en-us/iaas/api/#/en/logging-search/latest/SearchResult/SearchLogs), which is part OCI Java SDK. Similarly, for putting exported logs into object storage, it uses object storage apis from OCI java SDK.
   
   You can also track jobs, check their status and even kill jobs using other apis.

## APIs
   We will explain apis with examples. 
### API to submit job for log export
#### Request    
```
curl --location --request POST '<HostIP>/export/' \
--header 'Content-Type: application/json' \
--data-raw '{
  "region" : "US_PHOENIX_1",
  "ociLogSearchQuery" : "search \"ocid1.tenancy.oc1..XXXX\"  | sort by datetime asc",
  "startDateInMillisSinceEpoch" : 1603153200000,
  "endDateInMillisSinceEpoch" : 1604008851760,
  "timeWindowIncrementInSeconds" : 180,
  "uploadLogFileSize" : 2000,
  "ociObjectStorageNamespace" : "intrandallbarnes",
  "destinationBucketName" : "LogExport_",
  "ociConfigFilePath" : "/Users/mraleras/.oci/config",
  "ociProfileName" : "DEFAULT"
}'
```
All parameters are part of JSON Body to this REST POST call
##### Parameters determining which logs will be exported
   * region - this parameter configures both region from where logs will be imported from and region for the object storage bucket.
   * ociLogSearchQuery - OCI log [searchquery](https://docs.cloud.oracle.com/en-us/iaas/Content/Logging/Reference/query_language_specification.htm). Must be ending with "| sort by datetime asc"    
   * startDateInMillisSinceEpoch & endDateInMillisSinceEpoch - these 2 parameters configure the time interval for which logs will be exported as per query and region selected. 
   
##### Parameters for Object storage destination
   * ociObjectStorageNamespace - your tenancy name which also your OCI object storage namespace
   * destinationBucketName - name of the already existing bucket in the same region 
   
##### Other Parameters 
   * timeWindowIncrementInSeconds - this configures time duration for each log search api call in seconds. The job proceeds successively from timeWindowIncrementInSeconds to timeWindowIncrementInSeconds in these time duration increments. Keep it below 200 seconds.
   * uploadLogFileSize - this approximate number of log records in each log file exported. The max number of records in each log file uploaded will be uploadLogFileSize+999 .Keep it no more than 5000.  
   * ociConfigFilePath, ociProfileName - needed if you are running code locally, not otherwise.
   
#### Response 
You get a JobId after job submission. JobId is generated after hashing key input parameter namely region, ociLogSearchQuery, startDateInMillisSinceEpoch, endDateInMillisSinceEpoch,ociObjectStorageNamespace, destinationBucketName  & timeWindowIncrementInSeconds.
Hence, JobId is unique to each search query and other factors used for hashing process.
```
Your request info :  
                  
                  jobId=916912835 ,Generated after hashing (region, ociLogSearchQuery, startDateInMillisSinceEpoch, endDateInMillisSinceEpoch,
                    ociObjectStorageNamespace, destinationBucketName, timeWindowIncrementInSeconds) hence unique to each search query
                  region='US_PHOENIX_1'
                  ociLogSearchQuery='search "ocid1.tenancy.oc1..aaaaaaaaopbu45aomik7sswe4nzzll3f6ii6pipd5ttw4ayoozez37qqmh3a"  | sort by datetime asc'
                  startDateInMillisSinceEpoch=1603153210000
                  endDateInMillisSinceEpoch=1604007851770
                  startDate in date format=Tue Oct 20 00:20:10 GMT 2020
                  endDate in date format=Thu Oct 29 21:44:11 GMT 2020
                  timeWindowIncrementInSeconds=180
                  uploadLogFileSize=3000
                  logFetchApiLimit=999
                  ociObjectStorageNamespace='intrandallbarnes'
                  destinationBucketName='LogExport_'
                  ociConfigFilePath='null'
                  ociProfileName='null'
                  ociRegion=US_PHOENIX_1
                  endpointForLogSearchOci='https://logging.us-phoenix-1.oci.oraclecloud.com'
                  local requestOutputFile=/root/logDir_JobId->916912835_Time Of JobRun->Mon Nov 16 21:32:17 GMT 2020 _/JobId->916912835_Time Of JobRun->Mon Nov 16 21:32:17 GMT 2020 _requestOutput.out
                  local requestLogDirectory=/root/logDir_JobId->916912835_Time Of JobRun->Mon Nov 16 21:32:17 GMT 2020 _
                  Object Storage prefix for uploaded files for this request=JobId->916912835/Time Of JobRun->Mon Nov 16 21:32:17 GMT 2020 /



   Please take note of jobId:916912835 to track job and see its log with HTTP GET request on <hostname>/export/jobstatus?jobId=916912835 Log/status file will also be uploaded to same bucket at the end of the job
```   

### API to track already submitted job

#### Request    
```
curl --location --request GET '<HostIP>/export/jobstatus?jobId=<JobId_Unsigned_Integer>'
```
Query parameter JobId_Unsigned_Integer is the same job id you get when you submitted the job with the above [job submit api](#API-to-submit-job-for-log-export).
#### Response
```

```
Response is the entire log file for this job till the moment of the call. Each time you call. Note this is the log file of this job run, and it is not to be confused with the logs being exported, as result of this job.   

### API to kill the already submitted job

#### Request    
```
curl --location --request GET '<HostIP>/export/killjob?jobId=<JobId_Unsigned_Integer>'
```
Query parameter JobId_Unsigned_Integer is the same job id you get when you submitted the job with the above [job submit api](#API-to-submit-job-for-log-export).
The job is killed almost instantly after this api call. The logs exported so far to the object storage will not be deleted.
The job cant be tracked after killing it.
#### Response
```

```
Response is the entire log file for this job till the moment of the call. Each time you call. Note this is the log file of this job run, and it is not to be confused with the logs being exported, as result of this job.

   
_Feel free to import these curls into postman, for exploring them with GUI._
## Application Deployment
   
You can run the application either on your dev box or on OCI compute-instance.
You will need Java-11 SDK installed on the host. Maven is prepackaged with Spring-boot, and you can use [mvnw utility which is part of this repo ](mvnw) for compiling and running application with simple command as follows,
```
./mvnw spring-boot:run
```
OCI compute-instance is recommended to be in same region of your logs(you want to export) and destination object storage. This can help with reducing the end to end latency for the job run.
We use OCI Java SDK apis for reading logs and writing to bucket. The application needs to OCI authentication for using these apis.

For this authentication, you have 2 options.
* In case you are using OCI compute-instance for running this application, 
  1. Create dynamic group named say *dg_for_log_exporter* and associated IAM policy. 
     The policy needs to have following two statements.
     ```
     Allow dynamic-group dg_for_log_exporter to use log-content in tenancy 
     Allow dynamic-group dg_for_log_exporter to manage objects in compartment <CompartmentName> where any {request.permission='OBJECT_CREATE', request.permission='OBJECT_READ', request.permission='OBJECT_INSPECT'}
     ```
     The policy needs to be in root compartment since it asks for privileges to read logs at tenancy level.
  2. Create compute-instance which is part of this dynamic group and deploy the code on the same.
* Make sure you have OCI CLI config files and private key file are on the host/compute-instance. You also need to give their local paths as input parameters when you submit jobs using [job submit api](#API-to-submit-job-for-log-export).

### Helpful *[Automation Scripts](AutomationScripts)* setting up the OciLogExporter
* The bash script [CreateOCIComputeInstance.sh](AutomationScripts/CreateOciComputeInstance.sh) uses OCI CLI to create OCI linux compute-instance and its required pre-requisite resources like VCN, subnets and internet gateway etc.
 It is fully configurable.
* After creation of compute-instance, you will have bash prompt open on the same compute-instance.
 You can then run the next [SetupOciInstanceForLogExporter.sh](AutomationScripts/SetupOciInstanceForLogExporter.sh) bash script. This will setup the application on the compute-instance, listening on port 80.
 
_Needless to say, usage of these scripts is optional._   
   