package com.mybustrip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.dsl.Table;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeRoute {

    private String duetime;

    private String route;

    private String direction;

    public String getDuetime() {
        return duetime;
    }

    public void setDuetime(String duetime) {
        this.duetime = duetime;
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
}
