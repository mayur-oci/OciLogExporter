package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "resultCount",
        "fieldCount"
})
public class Summary {

    @JsonProperty("resultCount")
    private Integer resultCount;
    @JsonProperty("fieldCount")
    private Object fieldCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("resultCount")
    public Integer getResultCount() {
        return resultCount;
    }

    @JsonProperty("resultCount")
    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    @JsonProperty("fieldCount")
    public Object getFieldCount() {
        return fieldCount;
    }

    @JsonProperty("fieldCount")
    public void setFieldCount(Object fieldCount) {
        this.fieldCount = fieldCount;
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
