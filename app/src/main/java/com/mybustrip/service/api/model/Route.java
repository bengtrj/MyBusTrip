package com.mybustrip.service.api.model;

import java.util.List;

/**
 * Created by bengthammarlund on 12/05/16.
 */
public class Route {

    public List<Leg> legs;

    public Polyline polyline;

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

}
