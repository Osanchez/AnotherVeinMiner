package veinminer.patches;

import necesse.engine.GameEvents;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.packet.PacketTileDestroyed;
import necesse.engine.network.server.ServerClient;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.TileDamageType;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import net.bytebuddy.asm.Advice;
import veinminer.utils.BFS;
import veinminer.utils.Coordinate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


@ModMethodPatch(target = GameObject.class, name = "onDestroyed", arguments = {Level.class, int.class, int.class, ServerClient.class, ArrayList.class})
public class GameObjectDestroyedPatch {

    public static final HashSet<String> oreIDs = new HashSet<>(Arrays.asList(
            "ironorerock", "copperorerock", "goldorerock", "tungstenorerock", "lifequartzrock",
            "ironoresnow", "copperoresnow", "goldoresnow", "frostshardsnow",
            "copperoresandstone", "goldoresandstone", "quartzsandstone",
            "ironoreswamp", "copperoreswamp", "goldoreswamp", "ivyoreswamp",
            "ironoredeeprock", "copperoredeeprock","goldoredeeprock", "tungstenoredeeprock", "lifequartzdeeprock",
            "ironoredeepsnowrock", "copperoredeepsnowrock", "goldoredeepsnowrock", "tungstenoredeepsnowrock", "lifequartzdeepsnowrock", "glacialoredeepsnowrock",
            "ironoredeepsandstonerock", "copperoredeepsandstonerock", "goldoredeepsandstonerock", "ancientfossiloredeepsnowrock", "lifequartzdeepsandstonerock"
    ));

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GameObject gameObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) ServerClient client, @Advice.Argument(4) ArrayList<ItemPickupEntity> itemsDropped) {
       //todo investigate if limiting this to server only is an issue, perhaps client is overwriting the server
        if(client != null) {
           String objectID = gameObject.getStringID();
           if(oreIDs.contains(objectID)) {
               ArrayList<Coordinate> neighboringOres = getNeighboringOres(level, objectID, x, y);
               //destroy the ore, and the neighboring ores
               for(Coordinate ore : neighboringOres) {
                   GameObject currentOre = ore.getGameObject();
                   int oreX = ore.getX();
                   int oreY = ore.getY();

                   DamagedObjectEntity blockToDestroy = level.entityManager.getOrCreateDamagedObjectEntity(oreX, oreY);

                   //destroy the blocks on server - DamagedObjectEntity.class - checkObjectDamage
                   if (blockToDestroy.getLevel().isServerLevel()) {
                       blockToDestroy.getLevel().getServer().network.sendToClientsAt(new PacketTileDestroyed(oreX, oreY, currentOre.getID(), false), blockToDestroy.getLevel());
                   }

                   blockToDestroy.objectDamage = 0;
                   blockToDestroy.getLevel().getLevelTile(oreX, oreY).checkAround();
                   blockToDestroy.getLevel().getLevelObject(oreX, oreY).checkAround();
                   if (blockToDestroy.tileDamage == 0 && blockToDestroy.objectDamage == 0) {
                       blockToDestroy.remove();
                   }

                   //handle item drops - GameObject.class - onDestroyed
                   if (itemsDropped != null) {
                       ArrayList<InventoryItem> drops = currentOre.getDroppedItems(level, oreX, oreY);
                       ObjectLootTableDropsEvent dropsEvent;
                       GameEvents.triggerEvent(dropsEvent = new ObjectLootTableDropsEvent(new LevelObject(level, oreX, oreY), new Point(oreX * 32 + 16, oreY * 32 + 16), drops));
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

                   //increment stats
                   client.newStats.objects_mined.increment(1);

                   //play destroyed particles
                   if (!level.isServerLevel()) {
                       currentOre.spawnDestroyedParticles(level, oreX, oreY);
                   }

                   //remove entity
                   ObjectEntity objectEntity = level.entityManager.getObjectEntity(oreX, oreY);
                   level.setObject(oreX, oreY, 0);
                   if (objectEntity != null) {
                       objectEntity.remove();
                   }

               }
               //skip the original method
               return true;
           }
       }
       //run the original method
       return false;
    }


    public static ArrayList<Coordinate> getNeighboringOres(Level level, String objectID, int originX, int originY) {
        BFS graph = new BFS(originX, originY);
        graph.generateGraph(10);
        return graph.getRelated(level, objectID);
    }

}
