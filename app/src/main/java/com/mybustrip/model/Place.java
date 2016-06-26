package com.mybustrip.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by bengthammarlund on 04/05/16.
 */
@JsonDeserialize(using = PlaceDeserializer.class)
public class Place extends Location {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
