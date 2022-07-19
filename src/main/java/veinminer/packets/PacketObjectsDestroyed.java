package veinminer.packets;

import java.io.IOException;
import java.util.ArrayList;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.LevelObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import veinminer.objects.Coordinate;
import veinminer.utils.CoordinateParser;

public class PacketObjectsDestroyed extends Packet {
    public final ArrayList<Coordinate> tiles;

    public PacketObjectsDestroyed(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        String rawJson = reader.getNextString();
        CoordinateParser parser = new CoordinateParser();
        ArrayList<Coordinate> tiles;
        try {
            tiles = parser.parseCoordinates(rawJson);
        } catch (IOException e) {
            System.out.printf("Failed to parse PacketObjectsDestroyed packet, Error: %s", e.getMessage());
            tiles = new ArrayList<>();
        }
        this.tiles = tiles;
    }

    public PacketObjectsDestroyed(ArrayList<Coordinate> all_tiles) {
        this.tiles = all_tiles;
        PacketWriter writer = new PacketWriter(this);
        //convert coordinates of tiles into string json
        JSONObject tilesJSON = new JSONObject();
        JSONArray tiles = new JSONArray();
        for(Coordinate coordinate : all_tiles) {
            tiles.add(coordinate.getJSON());
        }
        tilesJSON.put("tiles", tiles);
        writer.putNextString(tilesJSON.toJSONString());
    }

    public void processClient(NetworkPacket packet, Client client) {
        for(Coordinate coordinate : this.tiles) {
            int tileX = coordinate.getX();
            int tileY = coordinate.getY();
            int tileID = coordinate.getID();

            if (client.getLevel() != null) {
                LevelObject lo = client.getLevel().getLevelObject(tileX, tileY);
                if (lo.object.getID() == tileID) {
                    lo.onObjectDestroyed((ServerClient)null, (ArrayList)null);
                } else {
                    client.network.sendPacket(new PacketRequestObjectChange(tileX, tileY));
                }
            }
        }


    }
}