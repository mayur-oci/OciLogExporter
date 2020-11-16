package com.oci.logexporter;

import com.oci.logexporter.pojo.LogExportReq;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(path = "/export")
public class RestController {

    static ConcurrentMap<Integer, LogExportReq> map = new ConcurrentHashMap<Integer, LogExportReq>();
    static ExecutorService executorService = Executors.newFixedThreadPool(5);

    private static String txtToHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                // We need Tab support here, because we print StackTraces as HTML
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    builder.append(c);

            }
        }
        String converted = builder.toString();
        String str = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
        Pattern patt = Pattern.compile(str);
        Matcher matcher = patt.matcher(converted);
        converted = matcher.replaceAll("<a href=\"$1\">$1</a>");
        return converted;
    }

    @PostMapping(path = "/", consumes = "application/json", produces = "application/text")
    public String exportFromLogsToOs(@RequestBody LogExportReq logExportReq)
            throws Exception {
        if (map.size() > 5) {
            return " Do not submit more than 5 log export jobs...either wait for any existing job to finish " +
                    "Or kill any job with HTTP GET request on <hostname>:8080/export/killjob?jobId=<jobId>";
        }
        if (!map.containsKey(logExportReq.hashCode())) {
            String success = logExportReq.initialization();
            if (!success.startsWith("Started Job ")) {
                return success;
            }
            LogExporter logExporter = new LogExporter(logExportReq);
            Future<?> future = executorService.submit(logExporter);
            logExportReq.future = future;
            map.put(logExportReq.jobId, logExportReq);
            return logExportReq.toString() + "Please take note of jobId:" +
                    logExportReq.jobId + " to track job and see its log with HTTP GET request on <hostname>:8080/export/jobstatus?jobId=" + logExportReq.jobId +
                    " Log/status file will also be uploaded to same bucket at the end of the job";
        } else {
            return "\n\n Same job already exists. Please kill it first with HTTP GET request on <hostname>:8080/export/killjob?jobId=" + logExportReq.hashCode();
        }
    }


    @GetMapping(path = "/jobstatus")
    public String getJobStatus(@RequestParam(name = "jobId") String jobId) {
        try {
            LogExportReq logReq = (LogExportReq) map.get(Integer.parseInt(jobId));
            if (logReq != null) {
                return "<html><head><title>" +
                        logReq.objectStoragePrefixForUploadedFilesForThisRequest +
                        "</title></head><body>" +
                        txtToHtml(Files.readString(logReq.requestOutputFile.toPath())) +
                        "</body></html>";
            } else {
                return "<html><head><title>" +
                        jobId +
                        "</title></head><body>" +
                        txtToHtml("No job found with jobId: " + jobId) +
                        "</body></html>";
            }
        } catch (Exception e) {
            return " Could not fetch status jobId is integer.Exception " + e;
        }
    }

    @GetMapping(path = "/killjob")
    public String killJob(@RequestParam(name = "jobId") String jobId) {

        LogExportReq logReq = (LogExportReq) map.get(Integer.parseInt(jobId));
        if (logReq != null) {
            logReq.log("Attempting to abort the job " + jobId);
            logReq.doesJobNeedsToBeAborted = true;
            while (logReq.future.isDone()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    continue;
                }
            }
            map.remove(logReq.jobId);
            return "<html><head><title>" +
                    jobId +
                    "</title></head><body>" +
                    txtToHtml("Job with jobId: " + jobId + " aborted!!!\n") +
                    "</body></html>";
        } else {
            return "<html><head><title>" +
                    jobId +
                    "</title></head><body>" +
                    txtToHtml("No job with jobId: " + jobId) +
                    "</body></html>";
        }
    }
}


