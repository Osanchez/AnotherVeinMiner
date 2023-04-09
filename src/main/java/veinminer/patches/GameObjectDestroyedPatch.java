package veinminer.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;
import veinminer.objects.Coordinate;
import veinminer.AnotherVeinMiner;
import veinminer.packets.PacketObjectsDestroyed;
import java.util.HashSet;
import java.util.ArrayList;


@ModMethodPatch(target = GameObject.class, name = "onDestroyed", arguments = {Level.class, int.class, int.class, ServerClient.class, ArrayList.class})
public class GameObjectDestroyedPatch {

    public static ArrayList<Coordinate> DFSNodeGathering(Level level, Coordinate coord, int depth, int maxRadius, HashSet<Coordinate> visited) {
        ArrayList<Coordinate> result = new ArrayList<>();
        if(coord.x < 0 
        || coord.y < 0 
        || coord.x > level.width
        || coord.y > level.height
        || depth > maxRadius)
            return result;

        GameObject gameObject = level.getObject(coord.x, coord.y);
        int objID = gameObject.getID();
        Coordinate current = new Coordinate(coord.x, coord.y, objID);
        if (visited.contains(current)) 
            return result;
            
        if (gameObject.isOre || gameObject.isRock)
            result.add(current);

        visited.add(current);
        depth++;
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x + 1, coord.y, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x - 1, coord.y, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x, coord.y - 1, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x, coord.y + 1, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x - 1 , coord.y - 1, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x + 1, coord.y + 1, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x - 1 , coord.y + 1, objID), depth, maxRadius, visited));
        result.addAll(DFSNodeGathering(level, new Coordinate(coord.x + 1, coord.y - 1, objID), depth, maxRadius, visited));
        return result;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This GameObject gameObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) ServerClient client) {
        if (!level.isClientLevel() || (AnotherVeinMiner.SPEED_MINE != null && !AnotherVeinMiner.SPEED_MINE.isDown()))
            return;

        level.getClient().network.sendPacket(new PacketObjectsDestroyed(DFSNodeGathering(level, new Coordinate(x, y, gameObject.getID()), 0, AnotherVeinMiner.RADIUS, new HashSet<Coordinate>())));
    }
}
