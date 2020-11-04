package com.oci.logexporter;

import com.oci.logexporter.pojo.LogExportReq;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/export")
public class LogExportController {

    static Map<Integer, LogExportReq> map = new HashMap<Integer, LogExportReq>();

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
        String success = logExportReq.initialization();
        if(!success.startsWith("Started Job ")){
            return success;
        }
        LogExporter logExporter = new LogExporter(logExportReq);
        new Thread(logExporter, String.valueOf(logExportReq.getJobId())).start();
        map.put(logExportReq.jobId, logExportReq);
        return logExportReq.toString();
    }

    @GetMapping(path = "/", produces = "application/json")
    public LogExportReq getSampleJson() {
        return new LogExportReq();
    }

    @GetMapping(path = "/jobstatus")
    public String getJobStatus(@RequestParam(name = "jobId") String jobId) {
        try {
            LogExportReq logReq = (LogExportReq) map.get(Integer.parseInt(jobId));
            return "<html><head><title>" +
                    jobId +
                    "</title></head><body>" +
                    txtToHtml(Files.readString(logReq.requestOutputFile.toPath())) +
                    "</body></html>";
        } catch (IOException e) {
            return " Could not fetch status Exception " + e;
        }
    }

    @GetMapping(path = "/killjob")
    public String killJob(@RequestParam(name = "jobId") String jobId) {

        LogExportReq logReq = (LogExportReq) map.get(Integer.parseInt(jobId));
        if (logReq != null) {
            logReq.log("Attempting to abort the job " + jobId);
            logReq.isJobAborted = true;
            return "<html><head><title>" +
                    jobId +
                    "</title></head><body>" +
                    txtToHtml("Job with jobId: " + jobId + " aborted") +
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


