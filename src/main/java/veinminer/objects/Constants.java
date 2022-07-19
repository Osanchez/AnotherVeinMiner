package veinminer.objects;

import java.util.ArrayList;
import java.util.Arrays;

public final class Constants {
    public static final String CONFIG_DIRECTORY_NAME = "AnotherVeinMiner";
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY_NAME + "\\" + CONFIG_FILE_NAME;
    public static final int DEFAULT_RADIUS = 10;
    public static final ArrayList<String> DEFAULT_ORE_IDS = new ArrayList<>(Arrays.asList(
            "ironorerock", "copperorerock", "goldorerock", "tungstenorerock", "lifequartzrock",
            "ironoresnow", "copperoresnow", "goldoresnow", "frostshardsnow",
            "copperoresandstone", "goldoresandstone", "quartzsandstone",
            "ironoreswamp", "copperoreswamp", "goldoreswamp", "ivyoreswamp",
            "ironoredeeprock", "copperoredeeprock","goldoredeeprock", "tungstenoredeeprock", "lifequartzdeeprock",
            "ironoredeepsnowrock", "copperoredeepsnowrock", "goldoredeepsnowrock", "tungstenoredeepsnowrock", "lifequartzdeepsnowrock", "glacialoredeepsnowrock",
            "ironoredeepsandstonerock", "copperoredeepsandstonerock", "goldoredeepsandstonerock", "ancientfossiloredeepsnowrock", "lifequartzdeepsandstonerock",
            "clayrock"
    ));
    public static final String RADIUS_KEY = "radius";
    public static final String ORES_KEY = "ores";
    public static final String MINING_KEY = "key";
    public static final String DEFAULT_MINING_KEY = "~";
    public static final String X_KEY = "x";
    public static final String Y_KEY = "y";
    public static final String ID_KEY = "id";

    // restrict instantiation
    private Constants() {}
}
