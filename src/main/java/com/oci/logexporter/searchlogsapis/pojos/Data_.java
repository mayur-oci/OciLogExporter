package com.oci.logexporter.searchlogsapis.pojos;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "additionalDetails",
        "availabilityDomain",
        "compartmentId",
        "compartmentName",
        "definedTags",
        "eventGroupingId",
        "eventName",
        "freeformTags",
        "identity",
        "message",
        "request",
        "resourceId",
        "response",
        "stateChange"
})
public class Data_ {

    @JsonProperty("additionalDetails")
    private Object additionalDetails;
    @JsonProperty("availabilityDomain")
    private String availabilityDomain;
    @JsonProperty("compartmentId")
    private Object compartmentId;
    @JsonProperty("compartmentName")
    private Object compartmentName;
    @JsonProperty("definedTags")
    private Object definedTags;
    @JsonProperty("eventGroupingId")
    private String eventGroupingId;
    @JsonProperty("eventName")
    private String eventName;
    @JsonProperty("freeformTags")
    private Object freeformTags;
    @JsonProperty("identity")
    private Identity identity;
    @JsonProperty("message")
    private String message;
    @JsonProperty("request")
    private Request request;
    @JsonProperty("resourceId")
    private Object resourceId;
    @JsonProperty("response")
    private Response response;
    @JsonProperty("stateChange")
    private StateChange stateChange;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("additionalDetails")
    public Object getAdditionalDetails() {
        return additionalDetails;
    }

    @JsonProperty("additionalDetails")
    public void setAdditionalDetails(Object additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    @JsonProperty("availabilityDomain")
    public String getAvailabilityDomain() {
        return availabilityDomain;
    }

    @JsonProperty("availabilityDomain")
    public void setAvailabilityDomain(String availabilityDomain) {
        this.availabilityDomain = availabilityDomain;
    }

    @JsonProperty("compartmentId")
    public Object getCompartmentId() {
        return compartmentId;
    }

    @JsonProperty("compartmentId")
    public void setCompartmentId(Object compartmentId) {
        this.compartmentId = compartmentId;
    }

    @JsonProperty("compartmentName")
    public Object getCompartmentName() {
        return compartmentName;
    }

    @JsonProperty("compartmentName")
    public void setCompartmentName(Object compartmentName) {
        this.compartmentName = compartmentName;
    }

    @JsonProperty("definedTags")
    public Object getDefinedTags() {
        return definedTags;
    }

    @JsonProperty("definedTags")
    public void setDefinedTags(Object definedTags) {
        this.definedTags = definedTags;
    }

    @JsonProperty("eventGroupingId")
    public String getEventGroupingId() {
        return eventGroupingId;
    }

    @JsonProperty("eventGroupingId")
    public void setEventGroupingId(String eventGroupingId) {
        this.eventGroupingId = eventGroupingId;
    }

    @JsonProperty("eventName")
    public String getEventName() {
        return eventName;
    }

    @JsonProperty("eventName")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @JsonProperty("freeformTags")
    public Object getFreeformTags() {
        return freeformTags;
    }

    @JsonProperty("freeformTags")
    public void setFreeformTags(Object freeformTags) {
        this.freeformTags = freeformTags;
    }

    @JsonProperty("identity")
    public Identity getIdentity() {
        return identity;
    }

    @JsonProperty("identity")
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("request")
    public Request getRequest() {
        return request;
    }

    @JsonProperty("request")
    public void setRequest(Request request) {
        this.request = request;
    }

    @JsonProperty("resourceId")
    public Object getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Object resourceId) {
        this.resourceId = resourceId;
    }

    @JsonProperty("response")
    public Response getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(Response response) {
        this.response = response;
    }

    @JsonProperty("stateChange")
    public StateChange getStateChange() {
        return stateChange;
    }

    @JsonProperty("stateChange")
    public void setStateChange(StateChange stateChange) {
        this.stateChange = stateChange;
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
