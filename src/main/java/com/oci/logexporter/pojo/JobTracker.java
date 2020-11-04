package com.oci.logexporter.pojo;

import java.util.Date;

public class JobTracker {
    LogExportReq logExportReq;
    Integer jobId;
    Date givenPrettyStartTime;
    Date givenPrettyEndTime;
    String finalStatus;
}
