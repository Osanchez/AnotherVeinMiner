package veinminer;
import necesse.engine.control.Control;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.PacketRegistry;
import veinminer.objects.Config;
import veinminer.packets.PacketObjectsDestroyed;
import veinminer.utils.ConfigParser;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;

import static veinminer.utils.ModMisc.getModVersion;

@ModEntry
public class AnotherVeinMiner {

    public static Control SPEED_MINE;
    public static Config modConfig;
    public static boolean configReadAttempted = false;
    public static HashSet<String> oreIDs;
    public static int radius;

    public AnotherVeinMiner() {
        readConfigFile();
        configureMiningControl();
    }

    public static void readConfigFile() {
        ConfigParser configReader = new ConfigParser();
        configReadAttempted = true;
        try {
            modConfig = configReader.parseConfig();
            oreIDs = modConfig.getOreIDs();
            radius = modConfig.get_radius();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void configureMiningControl() {
        try {
            //get the mining key to use
            Character miningChar = modConfig.get_mining_key();
            int miningCharKeyCode = KeyEvent.getExtendedKeyCodeForChar(miningChar);

            Class<Control> classObj = Control.class;

            //create an instance of control class
            Constructor<? extends Control> controlConstructor = classObj.getDeclaredConstructor(Integer.TYPE, String.class, Integer.TYPE);
            controlConstructor.setAccessible(true);
            //get the add control method
            Method addControlMethod = classObj.getDeclaredMethod("addControl", Control.class);
            addControlMethod.setAccessible(true);
            SPEED_MINE = (Control) addControlMethod.invoke(null, controlConstructor.newInstance(miningCharKeyCode, "speedmine", 1));

        } catch (Exception e) {
            System.out.println("Could not create fast mine key bind");
        }
    }

    public void init() {
        PacketRegistry.registerPacket(PacketObjectsDestroyed.class);
        System.out.printf("AnotherVeinMiner (version %s) by Trihardest Loaded!\n", getModVersion());
    }

}
