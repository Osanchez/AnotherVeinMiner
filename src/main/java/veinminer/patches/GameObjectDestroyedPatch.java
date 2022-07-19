package veinminer.patches;

import necesse.engine.GameEvents;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import net.bytebuddy.asm.Advice;
import veinminer.AnotherVeinMiner;
import veinminer.packets.PacketObjectsDestroyed;
import veinminer.utils.BFS;
import veinminer.objects.Coordinate;

import java.awt.*;
import java.util.*;



@ModMethodPatch(target = GameObject.class, name = "onDestroyed", arguments = {Level.class, int.class, int.class, ServerClient.class, ArrayList.class})
public class GameObjectDestroyedPatch {

    public static ArrayList<Coordinate> getNeighboringOres(Level level, String objectID, int originX, int originY) {
        BFS graph = new BFS(originX, originY);
        graph.generateGraph(AnotherVeinMiner.radius);
        return graph.getRelated(level, objectID);
    }


    public static void destroyGameObjects(ServerClient client, Level level, ArrayList<Coordinate> oresToDestroy) {

        //send blocks to destroy to the clients
        level.getServer().network.sendToClientsAt(new PacketObjectsDestroyed(oresToDestroy), level);

        //increment stats
        client.newStats.objects_mined.increment(oresToDestroy.size());

        //update other stats on server, should automatically be sent to the clients
        for (Coordinate coordinate : oresToDestroy) {
            int oreX = coordinate.getX();
            int oreY = coordinate.getY();

            GameObject currentGameObject = level.getObject(oreX, oreY);

            //handle item drops - GameObject.class - onDestroyed
            ArrayList<InventoryItem> drops = currentGameObject.getDroppedItems(level, oreX, oreY);
            ObjectLootTableDropsEvent dropsEvent;
            GameEvents.triggerEvent(dropsEvent = new ObjectLootTableDropsEvent(new LevelObject(level, oreX, oreY), new Point(oreX * 32 + 16, oreY * 32 + 16), drops));
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                Iterator var8 = dropsEvent.drops.iterator();

                while(var8.hasNext()) {
                    InventoryItem item = (InventoryItem)var8.next();
                    ItemPickupEntity droppedItem = item.getPickupEntity(level, (float)dropsEvent.dropPos.x, (float)dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(droppedItem);
                }
            }

            //remove entity
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(oreX, oreY);
            level.setObject(oreX, oreY, 0);
            if (objectEntity != null) {
                objectEntity.remove();
            }

        }
    }

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GameObject gameObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) ServerClient client, @Advice.Argument(4) ArrayList<ItemPickupEntity> itemsDropped) {
        boolean isServer = level.isServerLevel();
        boolean isClient = level.isClientLevel();
        System.out.printf("isClient: %s | isServer: %s \n", isClient, isServer);

        if(client != null) {
           String objectID = gameObject.getStringID();
           if(AnotherVeinMiner.oreIDs.contains(objectID)) {
               ArrayList<Coordinate> neighboringOres = getNeighboringOres(level, objectID, x, y);
               destroyGameObjects(client, level, neighboringOres);
               return true; // skip the original method
           }
       }
       return false; // run the original method
    }


}
