package veinminer.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import veinminer.objects.Coordinate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static veinminer.objects.Constants.*;

public class CoordinateParser {
    private final JsonFactory jsonReader;

    public CoordinateParser() {
        this.jsonReader = initializeReader();
    }

    public JsonFactory initializeReader() {
        JsonFactory factory = new JsonFactory();
        factory.enable(Feature.ALLOW_COMMENTS);
        return factory;
    }

    public ArrayList<Coordinate> parseCoordinates(String rawJSON) throws IOException {
        JsonParser parser = this.jsonReader.createParser(rawJSON);

        if(parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            if(fieldName.equals("tiles")) {
                ObjectMapper mapper = new ObjectMapper();
                ArrayNode node = mapper.readTree(parser);
                Iterator<JsonNode> iterator = node.elements();
                for (int i = 0; i < node.size(); i++) {
                    if (iterator.hasNext()) {
                        String CoordinateJSONString = iterator.next().asText();
                        JsonNode coordinate = mapper.readTree(CoordinateJSONString);
                        Coordinate currentCoord = new Coordinate(coordinate.get(X_KEY).asInt(), coordinate.get(Y_KEY).asInt());
                        currentCoord.setID(coordinate.get(ID_KEY).asInt());
                        coordinates.add(currentCoord);
                    }
                }
            }
        }
        parser.close();
        return coordinates;
    }

}
