package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "dataschema",
        "id",
        "oracle",
        "source",
        "specversion",
        "time",
        "type"
})
public class LogContent {

    @JsonProperty("data")
    private Data_ data;
    @JsonProperty("dataschema")
    private String dataschema;
    @JsonProperty("id")
    private String id;
    @JsonProperty("oracle")
    private Oracle oracle;
    @JsonProperty("source")
    private String source;
    @JsonProperty("specversion")
    private String specversion;
    @JsonProperty("time")
    private String time;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public Data_ getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data_ data) {
        this.data = data;
    }

    @JsonProperty("dataschema")
    public String getDataschema() {
        return dataschema;
    }

    @JsonProperty("dataschema")
    public void setDataschema(String dataschema) {
        this.dataschema = dataschema;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("oracle")
    public Oracle getOracle() {
        return oracle;
    }

    @JsonProperty("oracle")
    public void setOracle(Oracle oracle) {
        this.oracle = oracle;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("specversion")
    public String getSpecversion() {
        return specversion;
    }

    @JsonProperty("specversion")
    public void setSpecversion(String specversion) {
        this.specversion = specversion;
    }

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
