package veinminer.objects;

import java.io.File;
import necesse.engine.GlobalData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Config {
    String ores[] = {
        "ironorerock", 
        "copperorerock", 
        "goldorerock", 
        "tungstenorerock", 
        "lifequartzrock",
        "ironoresnow", 
        "copperoresnow", 
        "goldoresnow", 
        "frostshardsnow",
        "ironoresandstone", 
        "copperoresandstone", 
        "goldoresandstone", 
        "quartzsandstone",
        "ironoreswamp", 
        "copperoreswamp", 
        "goldoreswamp", 
        "ivyoreswamp",
        "ironoredeeprock", 
        "copperoredeeprock",
        "goldoredeeprock", 
        "tungstenoredeeprock", 
        "lifequartzdeeprock",
        "ironoredeepsnowrock", 
        "copperoredeepsnowrock", 
        "goldoredeepsnowrock", 
        "tungstenoredeepsnowrock", 
        "lifequartzdeepsnowrock", 
        "glacialoredeepsnowrock",
        "ironoredeepsandstonerock", 
        "copperoredeepsandstonerock", 
        "goldoredeepsandstonerock", 
        "ancientfossiloredeepsnowrock", 
        "lifequartzdeepsandstonerock",
        "clayrock"
    };
    int radius = 10;
    Character miningKey = null;
    final String defaultMiningKey = "z";
    private static final Config OBJ = new Config();

    Config() {
        File file = new File(GlobalData.cfgPath() + "anotherveinminer.cfg");
        if (!file.exists()) {
            SaveData saveFile = new SaveData("CONFIG");
            saveFile.addStringArray("ores", this.ores);
            saveFile.addInt("radius", this.radius);
            saveFile.addSafeString("mining_key", this.defaultMiningKey);
            saveFile.saveScript(file);
            return;
        }
    
        LoadData save = new LoadData(file);
        this.ores = save.getStringArray("ores", this.ores, false);
        this.radius = save.getInt("radius", this.radius);
        String key = save.getSafeString("mining_key", null);
        if (key != null)
            this.miningKey = key.charAt(0); 
    }
    
    public static Config getInstance() {
        return OBJ;
    }

    public static Character getMiningKey() {
        return Config.getInstance().miningKey;
    }

    public static int getRadius() {
        return Config.getInstance().radius;
    }

    public static String[] getOres() {
        return Config.getInstance().ores;
    }
}
