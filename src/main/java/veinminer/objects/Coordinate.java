package veinminer.objects;

import necesse.level.gameObject.GameObject;
import org.json.simple.JSONObject;
import java.util.LinkedList;
import java.util.List;

public class Coordinate {
    private final int x;
    private final int y;
    private int id;
    private boolean visited;
    private List<Coordinate> neighbors;
    private GameObject gameObject;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.neighbors = new LinkedList<>();
    }

    public String getJSON() {
        JSONObject coordJSON = new JSONObject();
        coordJSON.put("x", this.getX());
        coordJSON.put("y", this.getY());
        coordJSON.put("id", this.getGameObjectID());
        return coordJSON.toJSONString();
    }

    public int getGameObjectID() {
        if (this.gameObject != null) {
            return this.gameObject.getID();
        } else {
            return -1;
        }
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public List<Coordinate> getNeighbors() {
        return this.neighbors;
    }

    public void setNeighbors(List<Coordinate> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Coordinate neighbor) {
        this.neighbors.add(neighbor);
    }

}
