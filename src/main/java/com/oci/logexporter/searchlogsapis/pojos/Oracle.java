package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "compartmentid",
        "ingestedtime",
        "loggroupid",
        "tenantid"
})
public class Oracle {

    @JsonProperty("compartmentid")
    private String compartmentid;
    @JsonProperty("ingestedtime")
    private String ingestedtime;
    @JsonProperty("loggroupid")
    private String loggroupid;
    @JsonProperty("tenantid")
    private String tenantid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("compartmentid")
    public String getCompartmentid() {
        return compartmentid;
    }

    @JsonProperty("compartmentid")
    public void setCompartmentid(String compartmentid) {
        this.compartmentid = compartmentid;
    }

    @JsonProperty("ingestedtime")
    public String getIngestedtime() {
        return ingestedtime;
    }

    @JsonProperty("ingestedtime")
    public void setIngestedtime(String ingestedtime) {
        this.ingestedtime = ingestedtime;
    }

    @JsonProperty("loggroupid")
    public String getLoggroupid() {
        return loggroupid;
    }

    @JsonProperty("loggroupid")
    public void setLoggroupid(String loggroupid) {
        this.loggroupid = loggroupid;
    }

    @JsonProperty("tenantid")
    public String getTenantid() {
        return tenantid;
    }

    @JsonProperty("tenantid")
    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
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
