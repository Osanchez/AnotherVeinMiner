package veinminer.patches;

import necesse.engine.control.Control;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import net.bytebuddy.asm.Advice;


@ModMethodPatch(target = PlayerMob.class, name = "tickControls", arguments = {MainGame.class, boolean.class, GameCamera.class})
public class ControlTickPatch {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This PlayerMob playerMob, @Advice.Argument(0) MainGame mainGame, @Advice.Argument(1) boolean isGameTick, @Advice.Argument(2) GameCamera camera) {
        if (Control.MOVE_UP.isDown()) {
            //System.out.println("'W' key has been pressed");
        }
        return false;
    }
}
