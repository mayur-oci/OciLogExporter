package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "logContent",
        "datetime",
        "oracle.tenantid",
        "oracle.compartmentid",
        "oracle.loggroupid",
        "specversion",
        "type",
        "source",
        "subject",
        "id",
        "time"
})
public class Data {

    @JsonProperty("logContent")
    private LogContent logContent;
    @JsonProperty("datetime")
    private Integer datetime;
    @JsonProperty("oracle.tenantid")
    private String oracleTenantid;
    @JsonProperty("oracle.compartmentid")
    private String oracleCompartmentid;
    @JsonProperty("oracle.loggroupid")
    private String oracleLoggroupid;
    @JsonProperty("specversion")
    private String specversion;
    @JsonProperty("type")
    private String type;
    @JsonProperty("source")
    private String source;
    @JsonProperty("subject")
    private Object subject;
    @JsonProperty("id")
    private String id;
    @JsonProperty("time")
    private String time;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("logContent")
    public LogContent getLogContent() {
        return logContent;
    }

    @JsonProperty("logContent")
    public void setLogContent(LogContent logContent) {
        this.logContent = logContent;
    }

    @JsonProperty("datetime")
    public Integer getDatetime() {
        return datetime;
    }

    @JsonProperty("datetime")
    public void setDatetime(Integer datetime) {
        this.datetime = datetime;
    }

    @JsonProperty("oracle.tenantid")
    public String getOracleTenantid() {
        return oracleTenantid;
    }

    @JsonProperty("oracle.tenantid")
    public void setOracleTenantid(String oracleTenantid) {
        this.oracleTenantid = oracleTenantid;
    }

    @JsonProperty("oracle.compartmentid")
    public String getOracleCompartmentid() {
        return oracleCompartmentid;
    }

    @JsonProperty("oracle.compartmentid")
    public void setOracleCompartmentid(String oracleCompartmentid) {
        this.oracleCompartmentid = oracleCompartmentid;
    }

    @JsonProperty("oracle.loggroupid")
    public String getOracleLoggroupid() {
        return oracleLoggroupid;
    }

    @JsonProperty("oracle.loggroupid")
    public void setOracleLoggroupid(String oracleLoggroupid) {
        this.oracleLoggroupid = oracleLoggroupid;
    }

    @JsonProperty("specversion")
    public String getSpecversion() {
        return specversion;
    }

    @JsonProperty("specversion")
    public void setSpecversion(String specversion) {
        this.specversion = specversion;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("subject")
    public Object getSubject() {
        return subject;
    }

    @JsonProperty("subject")
    public void setSubject(Object subject) {
        this.subject = subject;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
