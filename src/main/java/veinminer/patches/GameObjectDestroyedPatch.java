package veinminer.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;
import veinminer.AnotherVeinMiner;
import veinminer.packets.PacketObjectsDestroyed;
import veinminer.utils.BFS;
import veinminer.objects.Coordinate;
import java.util.ArrayList;


@ModMethodPatch(target = GameObject.class, name = "onDestroyed", arguments = {Level.class, int.class, int.class, ServerClient.class, ArrayList.class})
public class GameObjectDestroyedPatch {

    public static ArrayList<Coordinate> getNeighboringOres(Level level, String objectID, int originX, int originY) {
        BFS graph = new BFS(originX, originY);
        graph.generateGraph(AnotherVeinMiner.radius);
        return graph.getRelated(level, objectID);
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This GameObject gameObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) ServerClient client) {
        if(level.isClientLevel()) {
            if(AnotherVeinMiner.SPEED_MINE.isDown()) {
                String objectID = gameObject.getStringID();
                if(AnotherVeinMiner.oreIDs.contains(objectID)) {
                    ArrayList<Coordinate> neighboringOres = getNeighboringOres(level, objectID, x, y);
                    level.getClient().network.sendPacket(new PacketObjectsDestroyed(neighboringOres));
                }
            }
       }
    }
}
