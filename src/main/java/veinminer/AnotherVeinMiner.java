package veinminer;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.PacketRegistry;
import veinminer.objects.Config;
import veinminer.packets.PacketObjectsDestroyed;
import veinminer.utils.ConfigParser;

import java.io.IOException;
import java.util.HashSet;

import static veinminer.objects.Constants.CONFIG_FILE_PATH;

@ModEntry
public class AnotherVeinMiner {

    public static Config modConfig;
    public static boolean configReadAttempted = false;
    public static HashSet<String> oreIDs;
    public static int radius;

    public AnotherVeinMiner() {
        readConfigFile();
    }

    public static void readConfigFile() {
        ConfigParser configReader = new ConfigParser();
        configReadAttempted = true;
        try {
            modConfig = configReader.parseConfig(CONFIG_FILE_PATH);
            oreIDs = modConfig.getOreIDs();
            radius = modConfig.get_radius();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void init() {
        System.out.println("Hello world from a mod by Trihardest!");
        PacketRegistry.registerPacket(PacketObjectsDestroyed.class);
    }

}
