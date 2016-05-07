package com.mybustrip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonRootName("")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicStopListResponse {

    private String errorcode;

    private String errormessage;

    private String numberofresults;

    private String timestamp;

    private List<PublicStop> results;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public String getNumberofresults() {
        return numberofresults;
    }

    public void setNumberofresults(String numberofresults) {
        this.numberofresults = numberofresults;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<PublicStop> getResults() {
        return results;
    }

    public void setResults(List<PublicStop> results) {
        this.results = results;
    }
}
