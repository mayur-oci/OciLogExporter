package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Accept",
        "Accept-Encoding",
        "Connection",
        "Content-Type",
        "Date",
        "Opc-Client-Info",
        "Opc-Request-Id",
        "User-Agent",
        "X-Forwarded-For",
        "X-Real-IP",
        "X-Real-Port",
        "oci-original-url",
        "opc-principal"
})
public class Headers {

    @JsonProperty("Accept")
    private List<String> accept = null;
    @JsonProperty("Accept-Encoding")
    private List<String> acceptEncoding = null;
    @JsonProperty("Connection")
    private List<String> connection = null;
    @JsonProperty("Content-Type")
    private List<String> contentType = null;
    @JsonProperty("Date")
    private List<String> date = null;
    @JsonProperty("Opc-Client-Info")
    private List<String> opcClientInfo = null;
    @JsonProperty("Opc-Request-Id")
    private List<String> opcRequestId = null;
    @JsonProperty("User-Agent")
    private List<String> userAgent = null;
    @JsonProperty("X-Forwarded-For")
    private List<String> xForwardedFor = null;
    @JsonProperty("X-Real-IP")
    private List<String> xRealIP = null;
    @JsonProperty("X-Real-Port")
    private List<String> xRealPort = null;
    @JsonProperty("oci-original-url")
    private List<String> ociOriginalUrl = null;
    @JsonProperty("opc-principal")
    private List<String> opcPrincipal = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Accept")
    public List<String> getAccept() {
        return accept;
    }

    @JsonProperty("Accept")
    public void setAccept(List<String> accept) {
        this.accept = accept;
    }

    @JsonProperty("Accept-Encoding")
    public List<String> getAcceptEncoding() {
        return acceptEncoding;
    }

    @JsonProperty("Accept-Encoding")
    public void setAcceptEncoding(List<String> acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    @JsonProperty("Connection")
    public List<String> getConnection() {
        return connection;
    }

    @JsonProperty("Connection")
    public void setConnection(List<String> connection) {
        this.connection = connection;
    }

    @JsonProperty("Content-Type")
    public List<String> getContentType() {
        return contentType;
    }

    @JsonProperty("Content-Type")
    public void setContentType(List<String> contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("Date")
    public List<String> getDate() {
        return date;
    }

    @JsonProperty("Date")
    public void setDate(List<String> date) {
        this.date = date;
    }

    @JsonProperty("Opc-Client-Info")
    public List<String> getOpcClientInfo() {
        return opcClientInfo;
    }

    @JsonProperty("Opc-Client-Info")
    public void setOpcClientInfo(List<String> opcClientInfo) {
        this.opcClientInfo = opcClientInfo;
    }

    @JsonProperty("Opc-Request-Id")
    public List<String> getOpcRequestId() {
        return opcRequestId;
    }

    @JsonProperty("Opc-Request-Id")
    public void setOpcRequestId(List<String> opcRequestId) {
        this.opcRequestId = opcRequestId;
    }

    @JsonProperty("User-Agent")
    public List<String> getUserAgent() {
        return userAgent;
    }

    @JsonProperty("User-Agent")
    public void setUserAgent(List<String> userAgent) {
        this.userAgent = userAgent;
    }

    @JsonProperty("X-Forwarded-For")
    public List<String> getXForwardedFor() {
        return xForwardedFor;
    }

    @JsonProperty("X-Forwarded-For")
    public void setXForwardedFor(List<String> xForwardedFor) {
        this.xForwardedFor = xForwardedFor;
    }

    @JsonProperty("X-Real-IP")
    public List<String> getXRealIP() {
        return xRealIP;
    }

    @JsonProperty("X-Real-IP")
    public void setXRealIP(List<String> xRealIP) {
        this.xRealIP = xRealIP;
    }

    @JsonProperty("X-Real-Port")
    public List<String> getXRealPort() {
        return xRealPort;
    }

    @JsonProperty("X-Real-Port")
    public void setXRealPort(List<String> xRealPort) {
        this.xRealPort = xRealPort;
    }

    @JsonProperty("oci-original-url")
    public List<String> getOciOriginalUrl() {
        return ociOriginalUrl;
    }

    @JsonProperty("oci-original-url")
    public void setOciOriginalUrl(List<String> ociOriginalUrl) {
        this.ociOriginalUrl = ociOriginalUrl;
    }

    @JsonProperty("opc-principal")
    public List<String> getOpcPrincipal() {
        return opcPrincipal;
    }

    @JsonProperty("opc-principal")
    public void setOpcPrincipal(List<String> opcPrincipal) {
        this.opcPrincipal = opcPrincipal;
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
