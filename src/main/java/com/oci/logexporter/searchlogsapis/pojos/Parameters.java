package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fluentType",
        "fluentVersion"
})
public class Parameters {

    @JsonProperty("fluentType")
    private List<String> fluentType = null;
    @JsonProperty("fluentVersion")
    private List<String> fluentVersion = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fluentType")
    public List<String> getFluentType() {
        return fluentType;
    }

    @JsonProperty("fluentType")
    public void setFluentType(List<String> fluentType) {
        this.fluentType = fluentType;
    }

    @JsonProperty("fluentVersion")
    public List<String> getFluentVersion() {
        return fluentVersion;
    }

    @JsonProperty("fluentVersion")
    public void setFluentVersion(List<String> fluentVersion) {
        this.fluentVersion = fluentVersion;
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
