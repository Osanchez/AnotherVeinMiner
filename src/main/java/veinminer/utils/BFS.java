package veinminer.utils;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import veinminer.objects.Coordinate;
import veinminer.objects.Key;

import java.util.*;

public class BFS {
    private final int originX;
    private final int originY;
    private Map<Key, Coordinate> allEdges;

    public BFS(int originX, int originY) {
        this.originX = originX;
        this.originY = originY;
    }

    //dynamically generate a graph with origin at starting coordinate
    //parameter will be the distance from origin where we should search
    public void generateGraph(int perimeter) {
        this.allEdges = new HashMap<>();

        int lowerX = this.originX - perimeter;
        int higherX = this.originX + perimeter;

        int lowerY = this.originY - perimeter;
        int higherY = this.originY + perimeter;

        //create all edges in 2 dimensional array, to make it easier to set the neighbors
        //we always create the graph with a 1 unit parameter to handle neighbors for edge on graph
        for(int x = lowerX - 1; x <= higherX + 1; x++) {
            for (int y = lowerY - 1; y <= higherY + 1; y++) {
                this.allEdges.put(new Key(x, y), new Coordinate(x, y));
            }
        }

        //set the neighbors for all edges
        for(int x = lowerX; x <= higherX; x++) {
            for (int y = lowerY; y <= higherY; y++) {
                //row above
                addNeighborToEdge(x, y, x-1, y+1);
                addNeighborToEdge(x, y, x, y+1);
                addNeighborToEdge(x, y, x+1, y+1);

                // sides
                addNeighborToEdge(x, y, x-1, y);
                addNeighborToEdge(x, y, x+1, y);

                //row below
                addNeighborToEdge(x, y, x-1, y-1);
                addNeighborToEdge(x, y, x, y-1);
                addNeighborToEdge(x, y, x+1, y-1);
            }
        }
    }

    public void addNeighborToEdge(int edgeX, int edgeY, int neighborX, int neighborY) {
        try {
            this.allEdges.get(new Key(edgeX, edgeY)).addNeighbor(this.allEdges.get(new Key(neighborX, neighborY)));
        } catch (Exception e) {
            System.out.printf("neighbor (%s, %s) does not exist, skipping\n", neighborX, neighborY);
        }
    }

    //search for related nodes
    public ArrayList<Coordinate> getRelated(Level level, String matchID) {
        ArrayList<Coordinate> relatedObjs = new ArrayList<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(allEdges.get(new Key(this.originX, this.originY)));

        while(!queue.isEmpty()) {
            Coordinate currentCord = queue.poll();
            if (!currentCord.isVisited()) {
                currentCord.setVisited(true);
                try {
                    GameObject currentGameObject = level.getObject(currentCord.getX(), currentCord.getY());
                    if(currentGameObject.getStringID().equals(matchID)) {
                        relatedObjs.add(currentCord);
                        currentCord.setGameObject(currentGameObject);
                        currentCord.setID(currentGameObject.getID());
                        queue.addAll(currentCord.getNeighbors());
                    }
                } catch (Exception e) {
                    System.out.printf("current coordinate does not exist X: %s Y: %s", currentCord.getX(), currentCord.getY());
                }
            }
        }
        return relatedObjs;
    }

}
