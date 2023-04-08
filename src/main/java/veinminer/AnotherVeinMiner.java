package veinminer;
import necesse.engine.control.Control;
import necesse.engine.control.Control.ControlGroup;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.PacketRegistry;
import veinminer.objects.Config;
import veinminer.packets.PacketObjectsDestroyed;
import necesse.engine.localization.message.LocalMessage;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@ModEntry
public class AnotherVeinMiner {

    public static Control SPEED_MINE;
    public static int RADIUS;
    public AnotherVeinMiner() {
        //get the mining key to use
        AnotherVeinMiner.RADIUS = Config.getRadius();
        Character miningChar = Config.getMiningKey();
        if (miningChar == null) {
            SPEED_MINE = null;
            return;
        }
        int miningCharKeyCode = KeyEvent.getExtendedKeyCodeForChar(miningChar);
        try {
            Class<Control> classObj = Control.class;

            //create an instance of control class
            Constructor<? extends Control> controlConstructor = classObj.getDeclaredConstructor(Integer.TYPE, String.class);
            controlConstructor.setAccessible(true);
            //get the add control method
            Method addControlMethod = classObj.getDeclaredMethod("addControl", Control.class, ControlGroup.class);
            addControlMethod.setAccessible(true);
            SPEED_MINE = (Control) addControlMethod.invoke(null, controlConstructor.newInstance(miningCharKeyCode, "speedmine"), new ControlGroup(1, new LocalMessage("controls", "groupgeneral")));
        } catch (Exception e) {
            System.out.println("Could not create fast mine key bind");
        }
    }

    public void init() {
        PacketRegistry.registerPacket(PacketObjectsDestroyed.class);
    }

}
