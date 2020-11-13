# OciLogExporter

## Introduction
   This is Spring-Boot based Java web app which helps you export your OCI logs from OCI Logging Service to Oracle Cloud Object Storage.
   You submit jobs for exporting the logs using rest based api. Once you launch job with the rest api, it starts a single thread which does the job of exporting logs to object storage.
   The thread fetches logs [OCI SearchLogs API](https://docs.cloud.oracle.com/en-us/iaas/api/#/en/logging-search/latest/SearchResult/SearchLogs), which is part OCI Java SDK. Similarly, for putting exported logs into object storage, it uses object storage apis from OCI java SDK.
    
   You can also track jobs, check their status and even kill jobs using other apis.
## FAQs
### Which OCI logs are exported?   
    This depends on your search query submitted as a part of the job.
    Please read the api section for details.

### How region for logs and object storage are configured? Do they need to be same?
    This depends on your region submitted as a part of the job request. 
    Yes they need to be same.
    Please read the api section for details.
    
## APIs
   We will explain apis with examples
### API to submit job for log export

#### Request Curl   
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
{}
```   

### API to track already submitted job

#### Request Curl   
```
curl --location --request GET '<HostIP>/export/jobstatus?jobId=<JobId_Unsigned_Integer>'
```
Query parameter JobId_Unsigned_Integer is the same job id you get when you submitted the job with the above api.
#### Response
```

```
Response is the entire log file for this job till the moment of the call. Each time you call. Note this is the log file of this job run, and it is not to be confused with the logs being exported, as result of this job.   

### API to kill the already submitted job

#### Request Curl   
```
curl --location --request GET '<HostIP>/export/killjob?jobId=<JobId_Unsigned_Integer>'
```
Query parameter JobId_Unsigned_Integer is the same job id you get when you submitted the job with the above api.
The job is killed almost instantly after this api call. The logs exported so far to the object storage will not be deleted.
The job cant be tracked after killing it.
#### Response
```

```
Response is the entire log file for this job till the moment of the call. Each time you call. Note this is the log file of this job run, and it is not to be confused with the logs being exported, as result of this job.   

## Application Deployment
   
   You can run the application either on your dev box or on OCI compute-instance.
   OCI compute-instance is recommended since to be in same region
   For authenticating the code to use OCI api for reading logs and writing to bucket, you have 2 options.
   1. In case you are using OCI compute-instance for running this application, 
      1. Create dynamic group and associated IAM policy.
      2. Create compute-instance which is part of this dynamic group.
   2. Select **OCI region** from where you want your logs to be fetched and shown in the panel
   
## Automation scripts setting up the OciLogExporter on for OCI compute-instance

   
   