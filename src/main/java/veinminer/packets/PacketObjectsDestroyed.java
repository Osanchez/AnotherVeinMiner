package veinminer.packets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import veinminer.objects.Coordinate;
import veinminer.objects.Config;

public class PacketObjectsDestroyed extends Packet {

    public final ArrayList<Coordinate> tiles;
    public static Set<String> ores = new HashSet<>(Arrays.asList(Config.getOres()));
    public PacketObjectsDestroyed(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        ArrayList<Coordinate> tiles = new ArrayList<>();
        while (reader.hasNext()) 
            tiles.add(new Coordinate(reader.getNextInt(), reader.getNextInt(), reader.getNextInt()));

        this.tiles = tiles;
    }

    public PacketObjectsDestroyed(ArrayList<Coordinate> all_tiles) {
        this.tiles = all_tiles;
        PacketWriter writer = new PacketWriter(this);
        for (Coordinate a : all_tiles) {
            writer.putNextInt(a.x);
            writer.putNextInt(a.y);
            writer.putNextInt(a.gameID);
        }
    }

    public void onDestroyed(GameObject gameObject, Level level, int x, int y, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (itemsDropped != null) {
            ArrayList<InventoryItem> drops = gameObject.getDroppedItems(level, x, y);
            ObjectLootTableDropsEvent dropsEvent;
            GameEvents.triggerEvent(dropsEvent = new ObjectLootTableDropsEvent(new LevelObject(level, x, y), new Point(x * 32 + 16, y * 32 + 16), drops));
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                Iterator<InventoryItem> var8 = dropsEvent.drops.iterator();
                while(var8.hasNext()) {
                    InventoryItem item = var8.next();
                    ItemPickupEntity droppedItem = item.getPickupEntity(level, (float)dropsEvent.dropPos.x, (float)dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(droppedItem);
                    itemsDropped.add(droppedItem);
                }
            }
        }

        if (!level.isServerLevel())
            gameObject.spawnDestroyedParticles(level, x, y);

        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        level.setObject(x, y, 0);
        if (objectEntity != null) 
            objectEntity.remove();
    }


    public void processServer(NetworkPacket packet, Server server, ServerClient serverClient) {
        ArrayList<Coordinate> destroyOnClient = new ArrayList<>();
        for (Coordinate coordinate : this.tiles) {
            if (serverClient == null || serverClient.getLevel() == null)
                continue;

            LevelObject serverLevelObject = serverClient.getLevel().getLevelObject(coordinate.x, coordinate.y);
            if (!ores.contains(serverLevelObject.object.getStringID())) 
                continue;

            onDestroyed(serverLevelObject.object, serverLevelObject.level, coordinate.x, coordinate.y, serverClient, new ArrayList<>());
            destroyOnClient.add(coordinate);
        }
        if (destroyOnClient.size() > 0)
            server.network.sendToClientsAt(new PacketObjectsDestroyed(destroyOnClient), serverClient.getLevel());
    }

    public void processClient(NetworkPacket packet, Client client) {
        for (Coordinate coordinate : this.tiles) {
            if (client.getLevel() == null) 
                continue;

            LevelObject lo = client.getLevel().getLevelObject(coordinate.x, coordinate.y);
            if (lo.object.getID() == coordinate.gameID)
                onDestroyed(lo.object, lo.level, coordinate.x, coordinate.y, null, null);
            else
                client.network.sendPacket(new PacketRequestObjectChange(coordinate.x, coordinate.y));
        }
    }
}