package com.oci.logexporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oci.logexporter.pojo.LogExportReq;
import com.oracle.bmc.loggingsearch.model.SearchLogsDetails;
import com.oracle.bmc.loggingsearch.model.SearchResult;
import com.oracle.bmc.loggingsearch.requests.SearchLogsRequest;
import com.oracle.bmc.loggingsearch.responses.SearchLogsResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

class LogExporter implements Runnable {
    static ObjectMapper objectMapper = null;

    static {
        objectMapper = new ObjectMapper();
        SimpleBeanPropertyFilter firstFilter = SimpleBeanPropertyFilter.serializeAllExcept("explicitlySetFilter");
        FilterProvider filters = new SimpleFilterProvider().addFilter("explicitlySetFilter", firstFilter);
        objectMapper.setFilterProvider(filters);
    }

    List<SearchResult> consolidatedSearchResultList = new ArrayList<>();
    int logFileCount;
    LogExportReq logReq;
    private Date startDateLog;
    private String exitMsgOnError = null;

    public LogExporter(LogExportReq logExportReq) {
        this.logReq = logExportReq;
        this.logFileCount = 0;
        this.startDateLog = this.logReq.startDate;
        exitMsgOnError = "\n      ----Aborting this job with jobId " + logReq.jobId +
                "\n      ----If you want delete the log files for this job, all log files uploaded for this job have prefix " + logReq.objectStoragePrefixForUploadedFilesForThisRequest.replace("_", "/")
                + "\n      ----The bucket " + logReq.getDestinationBucketName() + " contains these log files";
    }

    private String objToJson(List<SearchResult> searchResultList) {
        try {
            String objJackson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(searchResultList);
            return objJackson;
        } catch (JsonProcessingException e) {
            logReq.log("ERROR: failed conversion :object to Json" + e);
            logReq.doesJobNeedsToBeAborted = true;
        }
        return null;
    }

    @Override
    public void run() {
        try {
            searchLogs();
        } catch (Exception e) {
            logReq.log("ERROR : Thread crashed for " + logReq + " with exception" + e);
            logReq.doesJobNeedsToBeAborted = true;
        }
        if (logReq.doesJobNeedsToBeAborted) {
            logReq.log("Job was aborted either due to error or by user");
        } else {
            logReq.log("Job is completed successfully.");
        }
        logReq.log("Log file " + logReq.requestOutputFile.getName() + " for this logs export request " +
                "is uploaded to the same bucket. Please look if there are any ERROR log statements, " +
                "if there are errors please try to resolve issue and run the the request again." +
                "Time taken by job in minutes " + (System.currentTimeMillis() - logReq.startOfJobTS.getTime()) / (1000 * 60));
        putLogObject(logReq.requestOutputFile);

        RestController.map.remove(logReq.jobId);
    }

    public void searchLogs() throws InterruptedException {
        int sizeOfResultSet = 0;
        Date startDateApi = logReq.startDate;
        Date endDateApi = new Date(startDateApi.getTime() + logReq.timeWindowIncrementInSeconds * 1000);
        Set<String> prevChunkSetOfLogIds = new HashSet<>();
        int apiCallAttempt = 1;
        do {
            SearchLogsDetails searchLogsDetails = SearchLogsDetails.builder().
                    searchQuery(logReq.getOciLogSearchQuery()).
                    timeStart(startDateApi).
                    timeEnd(endDateApi).
                    isReturnFieldInfo(false).
                    build();

            SearchLogsRequest searchLogsRequest = SearchLogsRequest.builder().searchLogsDetails(searchLogsDetails).
                    limit(logReq.logFetchApiLimit).build();
            SearchLogsResponse searchLogsResponse = null;
            List<SearchResult> resultSet = null;

            try {
                searchLogsResponse = logReq.ociLogClient.searchLogs(searchLogsRequest);

                sizeOfResultSet = searchLogsResponse.getSearchResponse().getSummary().getResultCount();
                if (sizeOfResultSet > 0) {
                    resultSet = searchLogsResponse.getSearchResponse().getResults();
                    apiCallAttempt = 1;

                    List<SearchResult> dedupedResultSet = new ArrayList<>();
                    Set<String> thisChunkSetOfLogIds = getLogRecordIdsAndMakeSureNoDuplicates(prevChunkSetOfLogIds, resultSet, dedupedResultSet);
                    resultSet = dedupedResultSet;
                    sizeOfResultSet = resultSet.size();

                    prevChunkSetOfLogIds = thisChunkSetOfLogIds;
                    if (thisChunkSetOfLogIds.size() != sizeOfResultSet) {
                        logReq.log("ERROR: Number of Ids and size of result set from api do not match");
                        logReq.log("opc request id " + searchLogsResponse.getOpcRequestId());
                        logReq.log("StartTimeStamp is " + startDateApi.getTime() + " EndTimeStamp is " + endDateApi);
                        logReq.doesJobNeedsToBeAborted = true;
                        break;
                    }

                    if (logReq.doesJobNeedsToBeAborted) {
                        break;
                    }

                    consolidatedSearchResultList.addAll(resultSet);

                    if (sizeOfResultSet == 999) {
                        logReq.log("ERROR: This log fetch may lose some logs, Start Date : "
                                + startDateApi + " , End Date : " + endDateApi +
                                "\n      ----Make your search query more specific(more filtered say by compartment) and reduce input parameter timeWindowIncrementInSeconds. "
                                + "\n      ----It is recommended to abort this request and try again with these changes" + exitMsgOnError);
                        // logReq.doesJobNeedsToBeAborted = true; break;
                    }

                    if (consolidatedSearchResultList.size() > logReq.getNumberOfLogRecordsForEachFile()) {
                        createObjectInOciOs(endDateApi);
                        consolidatedSearchResultList = new ArrayList<>();
                    }
                    Map mapForLastLogEntry = (Map) resultSet.get(sizeOfResultSet - 1).getData();
                    startDateApi = new Date((long) mapForLastLogEntry.get("datetime") + 1);
                } else {
                    // logReq.log("No logs for the time window: Start Date " + startDate + " , End Date : " + endDate);
                    startDateApi = new Date(endDateApi.getTime() + 1);
                }
                endDateApi = new Date(startDateApi.getTime() + (logReq.timeWindowIncrementInSeconds * 1000));
            } catch (BmcException e) {
                logReq.log("WARN: Could not get logs ...Due to exception ... trying again in 100 millis " + e);
                Thread.sleep(150);
                if (apiCallAttempt > 12) {
                    logReq.log("ERROR: Exiting since consistent errors for " + apiCallAttempt + " times" + exitMsgOnError);
                    logReq.doesJobNeedsToBeAborted = true;
                    break;
                }
                apiCallAttempt++;
            } catch (Exception e) {
                logReq.log("ERROR: Exiting due to exception " + e + exitMsgOnError);
                logReq.doesJobNeedsToBeAborted = true;
                return;
            }

        } while (endDateApi.getTime() <= (logReq.endDate.getTime() + (logReq.timeWindowIncrementInSeconds * 1000)) && !logReq.doesJobNeedsToBeAborted);

        try {
            if (consolidatedSearchResultList.size() > 0 && !logReq.doesJobNeedsToBeAborted) {
                logReq.log("Processing last chunk for this request");
                createObjectInOciOs(endDateApi);
                logReq.log("Successfully completed job " + logReq.toString());
                consolidatedSearchResultList = new ArrayList<>();
            }
        } catch (Exception e) {
            logReq.log("ERROR: for last chunk ...Due to exception " + e);
            logReq.doesJobNeedsToBeAborted = true;
        }
    }

    private Set<String> getLogRecordIdsAndMakeSureNoDuplicates(Set<String> prevChunkSetOfLogIds, List<SearchResult> resultSet, List<SearchResult> dedupedResulSet) {
        Set<String> thisChunkSetOfLogIds = new HashSet<>();
        for (SearchResult searchLogEntry : resultSet) {
            Map mapForLogEntry = (Map) searchLogEntry.getData();
            String logEntryId = (String) mapForLogEntry.get("id");
            if (prevChunkSetOfLogIds.contains(logEntryId)) {
                logReq.log("ERROR: Duplicate Log Entry ... highly unexpected " + logEntryId + exitMsgOnError);
                logReq.doesJobNeedsToBeAborted = true;
                break;
            }
            if (thisChunkSetOfLogIds.contains(logEntryId)) {
                logReq.log("Duplicate log record entry for id found in the single call of search API results : " + logEntryId);
//                logReq.log(objToJson(resultSet));
//                logReq.doesJobNeedsToBeAborted = true;
//                break;

            } else {
                dedupedResulSet.add(searchLogEntry);
            }
            thisChunkSetOfLogIds.add(logEntryId);
        }
        return thisChunkSetOfLogIds;
    }

    private void createObjectInOciOs(Date endDateApi) throws IOException {
        String jsonPrettyLogs = objToJson(consolidatedSearchResultList);
        String fileName = logReq.objectStoragePrefixForUploadedFilesForThisRequest + " Log records with timestamps from " + startDateLog.toString() + " to " + endDateApi.toString() + ".log";

        logReq.log("Number of records to be uploaded "
                + consolidatedSearchResultList.size() + " with file " + fileName);

        File jsonLogs = new File(logReq.requestLogDirectory, fileName);
        Files.write(jsonLogs.toPath(), jsonPrettyLogs.getBytes(), StandardOpenOption.CREATE);
        putLogObject(jsonLogs);
        this.startDateLog = new Date(endDateApi.getTime());
    }

    private boolean putLogObject(File logFileObj) {

        try {
            do {
                PutObjectRequest request =
                        PutObjectRequest.builder()
                                .bucketName(logReq.getDestinationBucketName())
                                .namespaceName(logReq.getOciObjectStorageNamespace())
                                .objectName(logFileObj.getName().replace("_", "/")) // https://docs.cloud.oracle.com/en-us/iaas/Content/Object/Tasks/managingobjects.htm#nameprefix
                                .contentType(null)
                                .contentLanguage(null)
                                .contentEncoding(null)
                                .opcMeta(null)
                                .build();

                UploadManager.UploadRequest uploadDetails =
                        UploadManager.UploadRequest.builder(logFileObj).allowOverwrite(true).build(request);

                UploadManager.UploadResponse response = logReq.objUploadManager.upload(uploadDetails);

                InputStream is = Files.newInputStream(logFileObj.toPath());
                String md5 = Base64.getEncoder().encodeToString(DigestUtils.md5(is));
                if (!md5.equals(response.getContentMd5())) {
                    logReq.log("WARN: md5 checksum not matching for uploaded file " + logFileObj.getName());
                    logReq.log("WARN: Hence will upload the file again, overwriting the old one");
                    logReq.log("-md5 from oci os service is " + response.getContentMd5());
                    logReq.log("-md5 from local calculations " + md5);
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.
                            builder().namespaceName(logReq.getOciObjectStorageNamespace())
                            .bucketName(logReq.getDestinationBucketName()).
                                    objectName(logFileObj.getName()).build();

                    logReq.ociOsClient.deleteObject(deleteObjectRequest);
                    continue;
                } else {
                    logReq.log("Log file " + logFileObj.getName() + " uploaded successfully to oci object storage bucket " + logReq.getDestinationBucketName() + "\n");
                    logFileObj.delete();
                    break;
                }
            } while (true);
        } catch (Exception e) {
            logReq.log("ERROR: Failed to upload log file " + logFileObj.getName() + " to oci object storage. Exception : " + e);
            logReq.doesJobNeedsToBeAborted = true;
            return false;
        }

        return true;
    }

}
