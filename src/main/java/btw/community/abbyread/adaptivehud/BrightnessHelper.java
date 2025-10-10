package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;

public class BrightnessHelper {
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) return 1.0f;
        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        // 0.0f to 1.0f
        return player.worldObj.getLightBrightness(x, y, z);
    }
}

