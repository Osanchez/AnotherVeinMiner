package veinminer.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import veinminer.objects.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static veinminer.objects.Constants.*;

public class ConfigParser {
    private final JsonFactory jsonReader;


    public ConfigParser() {
        this.jsonReader = initializeReader();
        this.initializeModConfig();
    }

    public JsonFactory initializeReader() {
        JsonFactory factory = new JsonFactory();
        factory.enable(Feature.ALLOW_COMMENTS);
        return factory;
    }

    public void initializeModConfig() {
        try {
            // check if directory exists, otherwise create the directory
            File directory = new File(CONFIG_DIRECTORY_NAME);
            if(!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    System.out.println("Failed to create AnotherVeinMiner config directory");
                    return;
                }
            }

            //check if the config file exists, otherwise create the config file
            File configFile = new File(CONFIG_FILE_PATH);
            if(!configFile.exists()) {
                createConfigFile();
            }
        } catch (Exception e) {
            System.out.println("Failed to create config directory for AnotherVeinMiner");
        }
    }

    public boolean deleteConfigFile() {
        File configFile = new File(CONFIG_FILE_PATH);
        if(configFile.exists()) {
            return configFile.delete();
        }
        return false;
    }

    public void createConfigFile() throws IOException {
        JSONObject jsonObject = new JSONObject();

        //create the default ores list
        JSONArray oresList = new JSONArray();
        oresList.addAll(DEFAULT_ORE_IDS);

        //set the keys
        jsonObject.put(MINING_KEY, DEFAULT_MINING_KEY);
        jsonObject.put(RADIUS_KEY, DEFAULT_RADIUS);
        jsonObject.put(ORES_KEY, oresList);

        //write the file
        FileWriter file = new FileWriter(CONFIG_FILE_PATH);
        file.write(jsonObject.toJSONString());
        file.close();
    }

    public Config parseConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        JsonParser parser = this.jsonReader.createParser(configFile);
        if(parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        Config theConfig = new Config();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            switch (fieldName) {
                case RADIUS_KEY:
                    theConfig.set_radius(parser.getIntValue());
                    break;
                case ORES_KEY:
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayNode node = mapper.readTree(parser);
                    Iterator<JsonNode> iterator = node.elements();
                    HashSet<String> ores = new HashSet<>();
                    for (int i = 0; i < node.size(); i++) {
                        if (iterator.hasNext()) {
                            ores.add(iterator.next().asText());
                        }
                    }
                    theConfig.setOreIDs(ores);
                    break;
                case MINING_KEY:
                    theConfig.set_mining_key(parser.getTextCharacters()[0]);
                    break;
                default:
                    System.out.printf("unrecognized key '%s' in config file", fieldName);
            }
        }
        parser.close();
        return theConfig;
    }

}
