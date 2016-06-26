package com.mybustrip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeRoute implements Identifiable, Comparable<RealtimeRoute> {

    @JsonIgnore
    private Long id;

    @JsonProperty("duetime")
    private String dueTime;

    private String route;

    private String stopId;

    private String direction;

    private String origin;

    private String destination;

    private String operator;

    private Date lastUpdated;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public int compareTo(RealtimeRoute another) {
        int result = this.route.compareToIgnoreCase(another.route);
        if (result == 0) {
            result = compare(getDue(), another.getDue());
        }
        return result;
    }

    private int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    private int getDue() {
        return this.dueTime.equalsIgnoreCase("due") ? -1 : Integer.valueOf(this.dueTime);
    }

}
