package veinminer.utils;

import necesse.level.gameObject.GameObject;

import java.util.LinkedList;
import java.util.List;

public class Coordinate {
    private final int x;
    private final int y;
    private boolean visited;
    private List<Coordinate> neighbors;
    private GameObject gameObject;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.neighbors = new LinkedList<>();
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
