package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;

public class BrightnessHelper {

    public static float getCurrentHUDLight(EntityPlayer player) {
        float light = player.worldObj.getLightBrightness(
                (int) player.posX,
                (int) (player.posY + player.getEyeHeight()),
                (int) player.posZ
        );
        return 0.2f + 0.8f * light; // clamp to 0.2 - 1.0
    }
}
