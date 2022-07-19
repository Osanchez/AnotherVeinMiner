package veinminer.objects;

import java.util.HashSet;

public class Config {
    private int _radius;
    private Character _mining_key;
    private HashSet<String> oreIDs;

    public Config(){}

    public void set_radius(int _radius) {
        this._radius = _radius;
    }
    public void setOreIDs(HashSet<String> oreIDs) {
        this.oreIDs = oreIDs;
    }

    public int get_radius() {
        return _radius;
    }
    public HashSet<String> getOreIDs() {
        return oreIDs;
    }

    public Character get_mining_key() {
        return _mining_key;
    }
    public void set_mining_key(Character mining_key) {
        this._mining_key = mining_key;
    }

}
