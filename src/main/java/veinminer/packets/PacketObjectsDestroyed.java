package veinminer.packets;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import necesse.engine.GameEvents;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import veinminer.AnotherVeinMiner;
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

    //copied from GameObject.java, avoid invocation of patched method
    public void onDestroyed(GameObject gameObject, Level level, int x, int y, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (itemsDropped != null) {
            ArrayList<InventoryItem> drops = gameObject.getDroppedItems(level, x, y);
            ObjectLootTableDropsEvent dropsEvent;
            GameEvents.triggerEvent(dropsEvent = new ObjectLootTableDropsEvent(new LevelObject(level, x, y), new Point(x * 32 + 16, y * 32 + 16), drops));
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                Iterator var8 = dropsEvent.drops.iterator();

                while(var8.hasNext()) {
                    InventoryItem item = (InventoryItem)var8.next();
                    ItemPickupEntity droppedItem = item.getPickupEntity(level, (float)dropsEvent.dropPos.x, (float)dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(droppedItem);
                    itemsDropped.add(droppedItem);
                }
            }
        }

        if (client != null) {
            client.newStats.objects_mined.increment(1);
        }

        if (!level.isServerLevel()) {
            gameObject.spawnDestroyedParticles(level, x, y);
        }

        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        level.setObject(x, y, 0);
        if (objectEntity != null) {
            objectEntity.remove();
        }

    }


    public void processServer(NetworkPacket packet, Server server, ServerClient serverClient) {
        ArrayList<Coordinate> destroyOnClient = new ArrayList<>();
        for(Coordinate coordinate : this.tiles) {
            int tileX = coordinate.getX();
            int tileY = coordinate.getY();
            int tileID = coordinate.getID();
            if(serverClient != null) {
                if (serverClient.getLevel() != null) {
                    LevelObject serverLevelObject = serverClient.getLevel().getLevelObject(tileX, tileY);
                    if (serverLevelObject.object.getID() == tileID && AnotherVeinMiner.oreIDs.contains(serverLevelObject.object.getStringID())) {
                        // remove level object from server
                        onDestroyed(serverLevelObject.object, serverLevelObject.level, tileX, tileY, serverClient, new ArrayList<>());
                        destroyOnClient.add(coordinate);
                    }
                }
            }
        }
        if(destroyOnClient.size() > 0) {
            server.network.sendToClientsAt(new PacketObjectsDestroyed(destroyOnClient), serverClient.getLevel());
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
        for(Coordinate coordinate : this.tiles) {
            int tileX = coordinate.getX();
            int tileY = coordinate.getY();
            int tileID = coordinate.getID();

            if (client.getLevel() != null) {
                LevelObject lo = client.getLevel().getLevelObject(tileX, tileY);
                if (lo.object.getID() == tileID) {
                    onDestroyed(lo.object, lo.level, tileX, tileY, null, null);
                } else {
                    client.network.sendPacket(new PacketRequestObjectChange(tileX, tileY));
                }
            }
        }
    }
}