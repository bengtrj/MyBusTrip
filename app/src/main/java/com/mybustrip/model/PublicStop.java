package com.mybustrip.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.orm.dsl.Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bengthammarlund on 03/05/16.
 */
@Table
@JsonDeserialize(using = PublicStopDeserializer.class)
public class PublicStop extends Place {

    private Long id;

    private String stopId;

    private String[] routes;

    private List<RealtimeRoute> realtimeRoutes;

    public List<RealtimeRoute> getRealtimeRoutes() {
        return realtimeRoutes;
    }

    public void setRealtimeRoutes(List<RealtimeRoute> realtimeRoutes) {
        this.realtimeRoutes = realtimeRoutes;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String[] getRoutes() {
        return routes;
    }

    public void setRoutes(String[] routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "PublicStop{" +
                "stopId='" + stopId + '\'' +
                ", name='" + getName() + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                ", routes=" + Arrays.toString(routes) +
                '}';
    }
}
