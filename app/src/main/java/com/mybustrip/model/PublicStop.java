package com.mybustrip.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bengthammarlund on 03/05/16.
 */
@JsonDeserialize(using = PublicStopDeserializer.class)
public class PublicStop extends Place implements Identifiable, Parcelable {

    private Long id;

    private String stopId;

    private String[] routes;

    private List<RealtimeRoute> realtimeRoutes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicStop)) return false;

        PublicStop that = (PublicStop) o;

        return getStopId() != null ? getStopId().equals(that.getStopId()) : that.getStopId() == null;

    }

    @Override
    public int hashCode() {
        return getStopId() != null ? getStopId().hashCode() : 0;
    }

    /*
        Parcelable stuff
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(getId()));
        dest.writeString(getStopId());
        dest.writeStringArray(getRoutes());
        dest.writeString(getName());
        dest.writeDouble(getLatitude());
        dest.writeDouble(getLongitude());
    }

    public static final Parcelable.Creator<PublicStop> CREATOR
            = new Parcelable.Creator<PublicStop>() {

        public PublicStop createFromParcel(Parcel in) {
            final PublicStop publicStop = new PublicStop();
            final String id = in.readString();
            if (id != null) {
                publicStop.setId(Long.parseLong(id));
            }
            publicStop.setStopId(in.readString());
            publicStop.setRoutes(in.createStringArray());
            publicStop.setName(in.readString());
            publicStop.setLatitude(in.readDouble());
            publicStop.setLongitude(in.readDouble());
            return publicStop;
        }

        public PublicStop[] newArray(int size) {
            return new PublicStop[size];
        }
    };



}
