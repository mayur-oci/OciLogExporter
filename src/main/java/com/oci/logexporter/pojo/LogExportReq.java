package com.oci.logexporter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.loggingsearch.LogSearchClient;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Future;

public class LogExportReq {

    @JsonIgnore
    public final int logFetchApiLimit = 999; // limit for each searchLogs api call
    // This input determines time window for each searchLogs api call.
    // For more info on API go to https://docs.cloud.oracle.com/en-us/iaas/api/#/en/logging-search/20190909/SearchResult/SearchLogs
    // We cover entire time range from startDateInMillisSinceEpoch to endDateInMillisSinceEpoch
    // by moving time window for each successive searchLogs api call
    // keep less than 5*60 .. 5 minutes
    public Long timeWindowIncrementInSeconds = 120l; // 2 minutes
    // Upload Size In Number Of LogRecords in each. This is approximate
    // uploadLogFileSize <= Actual number of records <= (uploadLogFileSize + logFetchApiLimit)
    // keep it less than 8000
    // needs to be more than 999
    public int numberOfLogRecordsForEachFile = 3000;
    @JsonIgnore
    public Region ociRegion = null;
    @JsonIgnore
    public String endpointForLogSearchOci = null;
    @JsonIgnore
    public Date startDate = null;
    @JsonIgnore
    public Date endDate = null;
    @JsonIgnore
    public Integer jobId = null; // please see hashCode for this pojo, its unique to your search query, time window and region, set for given request
    @JsonIgnore
    public BasicAuthenticationDetailsProvider ociAuthProvider = null;
    @JsonIgnore
    public File requestOutputFile = null;
    @JsonIgnore
    public File requestLogDirectory = null;
    @JsonIgnore
    public LogSearchClient ociLogClient = null;
    @JsonIgnore
    public ObjectStorage ociOsClient = null;
    @JsonIgnore
    public UploadManager objUploadManager = null;
    @JsonIgnore
    public String timeOfStartOfRequestInSecondsFromEpoch = null;
    @JsonIgnore
    public String objectStoragePrefixForUploadedFilesForThisRequest;
    @JsonIgnore
    public volatile boolean doesJobNeedsToBeAborted = false;
    @JsonIgnore
    public volatile Future<?> future = null;
    @JsonIgnore
    public Date startOfJobTS;

    // Log Search criteria...determines logs to be exported
    // preferably run your code in the same region from where your reading logs
    // and also have destination object storage bucket in the same region from latency point of view.
    // Allowed values are
    // AP-CHIYODA-1, AP-CHUNCHEON-1, AP-HYDERABAD-1, AP-MELBOURNE-1, AP-MUMBAI-1, AP-OSAKA-1, AP-SEOUL-1, AP-SYDNEY-1,
    // AP-TOKYO-1, CA-MONTREAL-1, CA-TORONTO-1, EU-AMSTERDAM-1, EU-FRANKFURT-1, EU-ZURICH-1, ME-DUBAI-1,
    // ME-JEDDAH-1, SA-SAOPAULO-1, UK-CARDIFF-1, UK-GOV-CARDIFF-1, UK-GOV-LONDON-1, UK-LONDON-1,
    // US-ASHBURN-1, US-GOV-ASHBURN-1, US-GOV-CHICAGO-1, US-GOV-PHOENIX-1, US-LANGLEY-1, US-LUKE-1, US-PHOENIX-1, US-SANJOSE-1
    String region = null;
    // Query MUST use sorting by datetime
    String ociLogSearchQuery = null;
    Long startDateInMillisSinceEpoch = null;
    Long endDateInMillisSinceEpoch = null;
    // destination object storage for your exported logs
    // as said earlier ..
    // preferably run your code in the same region from where your reading logs
    // and also this code 'assumes' destination object storage bucket in the same region. This helps from latency point of view.
    String ociObjectStorageNamespace = null; // usually same as your OCI tenancy name
    String destinationBucketName = null;
    // needed only if you are running code locally or where you have oci cli setup...
    // else use dynamic group if running on OCI cloud as explained above
    String ociConfigFilePath = null;
    String ociProfileName = null;

    public long getTimeWindowIncrementInSeconds() {
        return timeWindowIncrementInSeconds;
    }

    public void setTimeWindowIncrementInSeconds(long timeWindowIncrementInSeconds) {
        this.timeWindowIncrementInSeconds = timeWindowIncrementInSeconds;
    }

    public int getNumberOfLogRecordsForEachFile() {
        return numberOfLogRecordsForEachFile;
    }

    public void setNumberOfLogRecordsForEachFile(int numberOfLogRecordsForEachFile) {
        this.numberOfLogRecordsForEachFile = numberOfLogRecordsForEachFile;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOciLogSearchQuery() {
        return ociLogSearchQuery;
    }

    public void setOciLogSearchQuery(String searchQuery) {
        this.ociLogSearchQuery = searchQuery;
    }

    public Long getStartDateInMillisSinceEpoch() {
        return startDateInMillisSinceEpoch;
    }

    public void setStartDateInMillisSinceEpoch(Long startDateInMillisSinceEpoch) {
        this.startDateInMillisSinceEpoch = startDateInMillisSinceEpoch;
    }

    public Long getEndDateInMillisSinceEpoch() {
        return endDateInMillisSinceEpoch;
    }

    public void setEndDateInMillisSinceEpoch(Long endDate) {
        this.endDateInMillisSinceEpoch = endDate;
    }

    public String getOciObjectStorageNamespace() {
        return ociObjectStorageNamespace;
    }

    public void setOciObjectStorageNamespace(String ociObjectStorageNamespace) {
        this.ociObjectStorageNamespace = ociObjectStorageNamespace;
    }

    public String getDestinationBucketName() {
        return destinationBucketName;
    }

    public String getOciConfigFilePath() {
        return ociConfigFilePath;
    }

    public void setOciConfigFilePath(String ociConfigFilePath) {
        this.ociConfigFilePath = ociConfigFilePath;
    }

    public String getOciProfileName() {
        return ociProfileName;
    }

    public void setOciProfileName(String ociProfileName) {
        this.ociProfileName = ociProfileName;
    }

    public Region getOciRegion() {
        return ociRegion;
    }

    public String getEndpointForLogSearchOci() {
        return endpointForLogSearchOci;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogExportReq that = (LogExportReq) o;
        return region.equals(that.region) &&
                ociLogSearchQuery.equals(that.ociLogSearchQuery) &&
                startDateInMillisSinceEpoch.equals(that.startDateInMillisSinceEpoch) &&
                endDateInMillisSinceEpoch.equals(that.endDateInMillisSinceEpoch) &&
                ociObjectStorageNamespace.equals(that.ociObjectStorageNamespace) &&
                destinationBucketName.equals(that.destinationBucketName) &&
                timeWindowIncrementInSeconds == that.timeWindowIncrementInSeconds;
    }

    @Override
    public int hashCode() {
        return Math.abs(Objects.hash(region, ociLogSearchQuery, startDateInMillisSinceEpoch, endDateInMillisSinceEpoch,
                ociObjectStorageNamespace, destinationBucketName, timeWindowIncrementInSeconds));
    }

    @Override
    public String toString() {
        return "Your request info :  " +
                "\n                  " +
                "\n                  jobId=" + jobId + " ,Generated after hashing " + "(region, ociLogSearchQuery, startDateInMillisSinceEpoch, endDateInMillisSinceEpoch,\n" +
                "                    ociObjectStorageNamespace, destinationBucketName, timeWindowIncrementInSeconds) hence unique to each search query" +
                "\n                  region='" + region + '\'' +
                "\n                  ociLogSearchQuery='" + ociLogSearchQuery + '\'' +
                "\n                  startDateInMillisSinceEpoch=" + startDateInMillisSinceEpoch +
                "\n                  endDateInMillisSinceEpoch=" + endDateInMillisSinceEpoch +
                "\n                  startDate in date format=" + startDate +
                "\n                  endDate in date format=" + endDate +
                "\n                  timeWindowIncrementInSeconds=" + timeWindowIncrementInSeconds +
                "\n                  uploadLogFileSize=" + numberOfLogRecordsForEachFile +
                "\n                  logFetchApiLimit=" + logFetchApiLimit +
                "\n                  ociObjectStorageNamespace='" + ociObjectStorageNamespace + '\'' +
                "\n                  destinationBucketName='" + destinationBucketName + '\'' +
                "\n                  ociConfigFilePath='" + ociConfigFilePath + '\'' +
                "\n                  ociProfileName='" + ociProfileName + '\'' +
                "\n                  ociRegion=" + ociRegion +
                "\n                  endpointForLogSearchOci='" + endpointForLogSearchOci + '\'' +
                "\n                  local requestOutputFile=" + requestOutputFile +
                "\n                  local requestLogDirectory=" + requestLogDirectory +
                "\n                  Object Storage prefix for uploaded files for this request=" + objectStoragePrefixForUploadedFilesForThisRequest.replace("_", "/") +
                "\n\n\n\n   ";
    }

    public String initialization() {
        try {
            if (StringUtils.isEmpty(ociLogSearchQuery) || !this.ociLogSearchQuery.endsWith(" | sort by datetime asc")) {
                return ("ERROR: Input parameter ociLogSearchQuery cant be null and must end with -  \"| sort by datetime asc\"");
            }

            if(StringUtils.isEmpty(destinationBucketName)){
                return "Bucket name cant be empty";
            }

            if(StringUtils.isEmpty(ociObjectStorageNamespace)){
                return "Object Storage namespace cant be empty";
            }

            if(timeWindowIncrementInSeconds > 180){
                return "Keep timeWindowIncrementInSeconds no more than 180 seconds";
            }

            this.jobId = this.hashCode();
            this.startOfJobTS = new Date();
            this.objectStoragePrefixForUploadedFilesForThisRequest = "JobId(unique to search query)" + this.jobId + "_" + "Time Of JobRun for this log collection" + this.startOfJobTS.toString() + "_";
            File file = new File(System.getProperty("user.home"));
            this.requestLogDirectory = new File(file, "logDir_" + this.objectStoragePrefixForUploadedFilesForThisRequest);
            if (requestLogDirectory.exists()) {
                FileUtils.deleteDirectory(this.requestLogDirectory);
            }
            this.requestLogDirectory.mkdir();
            this.requestOutputFile = new File(this.requestLogDirectory, this.objectStoragePrefixForUploadedFilesForThisRequest + "requestOutput" + ".out");
            this.requestOutputFile.createNewFile();

            ociRegion = Region.fromRegionCodeOrId(region);
            endpointForLogSearchOci = "https://logging." + ociRegion.getRegionId() + ".oci.oraclecloud.com";

            setOciAuthProvider();
            setOciClients();

            if (endDateInMillisSinceEpoch < startDateInMillisSinceEpoch) {
                return ("ERROR: You cant have endDateInMillisSinceEpoch < startDateInMillisSinceEpoch and they cant be empty");
            } else {
                this.endDate = new Date(this.endDateInMillisSinceEpoch);
                this.startDate = new Date(this.startDateInMillisSinceEpoch);
            }

        } catch (Exception e) {
            return ("ERROR: Failed in request initialization " + e);
        }
        this.log("Started Job \n" + this.toString());
        return ("Started Job " + this.toString());
    }

    private void setOciClients() {
        this.ociLogClient = this.getLogSearchClient();
        this.ociOsClient = ObjectStorageClient.builder().region(this.ociRegion).build(this.ociAuthProvider);
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(false)
                        .allowParallelUploads(false)
                        .build();
        this.objUploadManager = new UploadManager(ociOsClient, uploadConfiguration);
    }

    synchronized public void log(String logLine) {
        try {
            System.out.println(logLine);
            byte[] logLineBytes = (logLine + "\n").getBytes();
            Files.write(this.requestOutputFile.toPath(), logLineBytes, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOciAuthProvider() throws IOException {
        if (StringUtils.isNotEmpty(this.ociConfigFilePath)) {
            File file = new File(this.ociConfigFilePath);
            file.setReadOnly();
            final ConfigFileReader.ConfigFile configFile;
            configFile = ConfigFileReader.parse(file.getAbsolutePath(), StringUtils.isEmpty(this.ociProfileName) ? "DEFAULT" : this.ociProfileName);
            this.ociAuthProvider = (BasicAuthenticationDetailsProvider)
                    new ConfigFileAuthenticationDetailsProvider(configFile);
        } else {
            this.ociAuthProvider = (BasicAuthenticationDetailsProvider) InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        }
    }

    private LogSearchClient getLogSearchClient() {
        LogSearchClient logSearchClient = LogSearchClient.builder().
                endpoint(this.endpointForLogSearchOci).
                build(this.ociAuthProvider);
        return logSearchClient;
    }

}
