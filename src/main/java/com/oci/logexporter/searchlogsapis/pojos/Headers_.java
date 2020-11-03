package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Content-Length",
        "Content-Type",
        "Date",
        "opc-request-id"
})
public class Headers_ {

    @JsonProperty("Content-Length")
    private List<String> contentLength = null;
    @JsonProperty("Content-Type")
    private List<String> contentType = null;
    @JsonProperty("Date")
    private List<String> date = null;
    @JsonProperty("opc-request-id")
    private List<String> opcRequestId = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Content-Length")
    public List<String> getContentLength() {
        return contentLength;
    }

    @JsonProperty("Content-Length")
    public void setContentLength(List<String> contentLength) {
        this.contentLength = contentLength;
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

    @JsonProperty("opc-request-id")
    public List<String> getOpcRequestId() {
        return opcRequestId;
    }

    @JsonProperty("opc-request-id")
    public void setOpcRequestId(List<String> opcRequestId) {
        this.opcRequestId = opcRequestId;
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
