package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "headers",
        "message",
        "payload",
        "responseTime",
        "status"
})
public class Response {

    @JsonProperty("headers")
    private Headers_ headers;
    @JsonProperty("message")
    private Object message;
    @JsonProperty("payload")
    private Object payload;
    @JsonProperty("responseTime")
    private String responseTime;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("headers")
    public Headers_ getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(Headers_ headers) {
        this.headers = headers;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
        this.message = message;
    }

    @JsonProperty("payload")
    public Object getPayload() {
        return payload;
    }

    @JsonProperty("payload")
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @JsonProperty("responseTime")
    public String getResponseTime() {
        return responseTime;
    }

    @JsonProperty("responseTime")
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
