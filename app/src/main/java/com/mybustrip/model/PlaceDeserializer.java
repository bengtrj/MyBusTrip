package com.mybustrip.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

/**
 * Created by bengthammarlund on 04/05/16.
 */
public class PlaceDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Place> {
    @Override
    public Place deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        final Place place = new Place();

        final JsonNode placeNode = jsonParser.getCodec().readTree(jsonParser);
        place.setIcon(placeNode.get("icon").asText());
        place.setName(placeNode.get("name").asText());

        JsonNode locationNode = placeNode.get("geometry").get("location");
        place.setLatitude(locationNode.findValue("lat").asDouble());
        place.setLongitude(locationNode.findValue("lng").asDouble());

        return place;
    }

    /*
    {
   "html_attributions" : [],
   "results" : [
    {
         "geometry" : {
            "location" : {
               "lat" : 53.38733329999999,
               "lng" : -6.245862799999999
            }
         },
         "icon" : "https://maps.gstatic.com/mapfiles/place_api/icons/bus-71.png",
         "id" : "3d295114bc7255efded893a5fa3ff74d1dcb702f",
         "name" : "Glentow Estate",
         "place_id" : "ChIJWSJx_DwOZ0gRTehbiPoS-NU",
         "reference" : "CnRiAAAAGGWf2PaZgMHn57RfZO5DLkJLZwNis7MoA3bamRONlZqylxMkyOixFxtH5OvTfzoeZS2IMLMcmo2eqYpQ6kZ_WfPc04HTu0zLAGhL6UlDJQVqx4nqer1qsSYUiAD1IFMo6wJ3lD71hHSsQWXDL2kquBIQ1OrDLlD2GrZvFHr93mcbzBoUYRfGSEgz_BjTw0lGDW1YWKnZffg",
         "scope" : "GOOGLE",
         "types" : [
            "bus_station",
            "transit_station",
            "point_of_interest",
            "establishment"
         ],
         "vicinity" : "Ireland"
      },
     */
}
