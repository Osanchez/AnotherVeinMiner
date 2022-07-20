package veinminer.objects;

import necesse.engine.GlobalData;
import java.util.ArrayList;
import java.util.Arrays;

public final class Constants {
    public static final double MAJOR_VERSION = 1;
    public static final double MINOR_VERSION = .20;
    public static final String MOD_VERSION = Double.toString(MAJOR_VERSION + MINOR_VERSION);
    public static final String CONFIG_DIRECTORY_NAME = GlobalData.appDataPath() + "\\AnotherVeinMiner";
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY_NAME + "\\" + CONFIG_FILE_NAME;
    public static final int DEFAULT_RADIUS = 10;
    public static final ArrayList<String> DEFAULT_ORE_IDS = new ArrayList<>(Arrays.asList(
            "ironorerock", "copperorerock", "goldorerock", "tungstenorerock", "lifequartzrock",
            "ironoresnow", "copperoresnow", "goldoresnow", "frostshardsnow",
            "ironoresandstone", "copperoresandstone", "goldoresandstone", "quartzsandstone",
            "ironoreswamp", "copperoreswamp", "goldoreswamp", "ivyoreswamp",
            "ironoredeeprock", "copperoredeeprock","goldoredeeprock", "tungstenoredeeprock", "lifequartzdeeprock",
            "ironoredeepsnowrock", "copperoredeepsnowrock", "goldoredeepsnowrock", "tungstenoredeepsnowrock", "lifequartzdeepsnowrock", "glacialoredeepsnowrock",
            "ironoredeepsandstonerock", "copperoredeepsandstonerock", "goldoredeepsandstonerock", "ancientfossiloredeepsnowrock", "lifequartzdeepsandstonerock",
            "clayrock"
    ));
    public static final String RADIUS_KEY = "radius";
    public static final String ORES_KEY = "ores";
    public static final String MINING_KEY = "mining_key";
    public static final String DEFAULT_MINING_KEY = "z";
    public static final String X_KEY = "x";
    public static final String Y_KEY = "y";
    public static final String ID_KEY = "id";

    // restrict instantiation
    private Constants() {}
}
