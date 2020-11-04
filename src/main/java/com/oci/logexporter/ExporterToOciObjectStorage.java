package com.oci.logexporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oracle.bmc.loggingsearch.LogSearchClient;
import com.oracle.bmc.loggingsearch.model.SearchLogsDetails;
import com.oracle.bmc.loggingsearch.model.SearchResult;
import com.oracle.bmc.loggingsearch.requests.SearchLogsRequest;
import com.oracle.bmc.loggingsearch.responses.SearchLogsResponse;
import com.oracle.bmc.model.BmcException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ExporterToOciObjectStorage {
    static LogSearchClient logSearchClient = getLogSearchClient();
    static ObjectMapper objectMapper = null;
    static List<SearchResult> consolidatedSearchResultList = new ArrayList<>();

    static {
        objectMapper = new ObjectMapper();
        SimpleBeanPropertyFilter firstFilter = SimpleBeanPropertyFilter.serializeAllExcept("explicitlySetFilter");
        FilterProvider filters = new SimpleFilterProvider().addFilter("explicitlySetFilter", firstFilter);
        objectMapper.setFilterProvider(filters);
    }

    private static String objToJson(List<SearchResult> searchResultList) {
        try {
            String objJackson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(searchResultList);
            return objJackson;
        } catch (JsonProcessingException e) {
            System.out.println("Error: failed conversion :object to Json" + e);
        }
        return null;
    }

    public static void searchLogs() throws InterruptedException {
        int sizeOfResultSet = 0;
        Date startDate = ConfigHolder.startDate;
        Date endDate = new Date(startDate.getTime() + ConfigHolder.timeWindowIncrement);
        Set<String> prevChunkSetOfLogIds = new HashSet<>();
        int apiCallAttempt = 1;
        do {
            SearchLogsDetails searchLogsDetails = SearchLogsDetails.builder().
                    searchQuery(ConfigHolder.searchQuery).
                    timeStart(startDate).
                    timeEnd(endDate).
                    isReturnFieldInfo(false).
                    build();
//            DelayStrategy delayStrategy = new DelayStrategy() {
//                @Override
//                public long nextDelay(WaiterConfiguration.WaitContext waitContext) {
//                    return 0;
//                }
//            };
//            RetryConfiguration retryConfig = RetryConfiguration.builder().delayStrategy(delayStrategy);
            SearchLogsRequest searchLogsRequest = SearchLogsRequest.builder().searchLogsDetails(searchLogsDetails).
                    limit(ConfigHolder.logFetchApiLimit).build();
            SearchLogsResponse searchLogsResponse = null;
            List<SearchResult> resultSet = null;
            try {
                searchLogsResponse = logSearchClient.searchLogs(searchLogsRequest);
                sizeOfResultSet = searchLogsResponse.getSearchResponse().getSummary().getResultCount();
                if (sizeOfResultSet > 0) {
                    resultSet = searchLogsResponse.getSearchResponse().getResults();
                    apiCallAttempt = 1;
                    Set<String> thisChunkSetOfLogIds = new HashSet<>();

                    for (SearchResult searchLogEntry : resultSet) {
                        Map mapForLogEntry = (Map) searchLogEntry.getData();

                        String logEntryId = (String) mapForLogEntry.get("id");
                        if (prevChunkSetOfLogIds.contains(logEntryId)) {
                            System.out.println("WARN: Duplicate Log Entry " + logEntryId);
                            System.exit(1);
                        }
                        thisChunkSetOfLogIds.add(logEntryId);
                    }
                    prevChunkSetOfLogIds = thisChunkSetOfLogIds;
                    assert thisChunkSetOfLogIds.size() == sizeOfResultSet;

                    consolidatedSearchResultList.addAll(resultSet);

                    if (sizeOfResultSet == 999)
                        System.out.println("WARN: This log fetch may lose some logs, Start Date : "
                                + startDate + " , End Date : " + endDate);

                    if (consolidatedSearchResultList.size() > ConfigHolder.chunkSize) {
                        System.out.println("Writing new file for Start Date " + startDate + " , End Date : " + endDate);
                        createObjectInOciOs();
                        consolidatedSearchResultList = new ArrayList<>();
                    }
                    Map mapForLastLogEntry = (Map) resultSet.get(sizeOfResultSet - 1).getData();
                    startDate = new Date((long) mapForLastLogEntry.get("datetime") + 1);
                } else {
                    System.out.println("no chunk for this time window, Start Date " + startDate + " , End Date : " + endDate);
                    startDate = new Date(endDate.getTime() + 1);
                }
                sizeOfResultSet = 0;
                searchLogsResponse = null;
                endDate = new Date(startDate.getTime() + ConfigHolder.timeWindowIncrement);
            } catch (BmcException e) {
                System.out.println("Error: Could not get logs ...Due to exception " + e);
                Thread.sleep(100);
                if (apiCallAttempt > 12) {
                    System.out.println("Exiting since consistent errors for " + apiCallAttempt + " times");
                    System.exit(1);
                }
                apiCallAttempt++;
                if (e.isTimeout()) {
                    continue;
                }

            } catch (Exception e) {
                System.out.println("Error: Could not get logs ...Due to exception " + e + "\n");
                System.exit(1);
            }

        } while (endDate.getTime() <= (ConfigHolder.endDate.getTime() + ConfigHolder.timeWindowIncrement));

        try {
            if (consolidatedSearchResultList.size() > 0) {
                System.out.println("Processing last chunk");
                createObjectInOciOs();
                consolidatedSearchResultList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void createObjectInOciOs() throws IOException {
        System.out.println("Size of consolidatedSearchResultList is " + consolidatedSearchResultList.size());
        String jsonPrettyLogs = objToJson(consolidatedSearchResultList);
        String fileName = Math.abs(jsonPrettyLogs.hashCode()) + ".log";
        System.out.println("New file name is " + fileName);
        File jsonLogs = new File(ConfigHolder.newLogDir, fileName);

        Files.write(jsonLogs.toPath(), jsonPrettyLogs.getBytes(), StandardOpenOption.CREATE);

        ObjectStorageHandler.putLogObject(jsonLogs.getName(), jsonLogs);

    }

    private static LogSearchClient getLogSearchClient() {
        LogSearchClient logSearchClient = LogSearchClient.builder().
                endpoint(ConfigHolder.endpointForLogSearchOci).
                build(ConfigHolder.ociAuthProvider);
        return logSearchClient;
    }

}
