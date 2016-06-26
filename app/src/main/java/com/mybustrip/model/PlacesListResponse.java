package com.mybustrip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonRootName("")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacesListResponse {

    private List<Place> results;

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }
}