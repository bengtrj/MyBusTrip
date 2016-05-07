package com.mybustrip.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class PublicStopDeserializer extends JsonDeserializer<PublicStop> {

    @Override
    public PublicStop deserialize(final JsonParser jsonParser,
                                  final DeserializationContext deserializationContext) throws IOException {

        final PublicStop publicStop = new PublicStop();

        final JsonNode publicStopNode = jsonParser.getCodec().readTree(jsonParser);
        publicStop.setStopId(publicStopNode.get("stopid").asText());
        publicStop.setName(publicStopNode.get("displaystopid").asText());
        publicStop.setLatitude(publicStopNode.get("latitude").asDouble());
        publicStop.setLongitude(publicStopNode.get("longitude").asDouble());

        JsonNode routesNode = ((
                (ArrayNode) publicStopNode.get("operators")))
                .findValue("routes");
        publicStop.setRoutes(parseRoutes(routesNode));

        return publicStop;
    }

    @NonNull
    private String[] parseRoutes(JsonNode routesNode) {
        final String[] routes = new String[routesNode.size()];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = routesNode.get(i).asText();
        }
        return routes;
    }
}
