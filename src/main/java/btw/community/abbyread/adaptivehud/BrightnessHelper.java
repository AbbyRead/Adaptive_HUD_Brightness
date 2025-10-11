package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

public class BrightnessHelper {

    private static float lastBrightness = 1.0F;

    public static float getCurrentHUDLight(EntityPlayer player) {
        float brightness;

        if (player == null || player.worldObj == null) {
            // No player (menu screen), use default dimmed value
            brightness = 0.8F;
            // System.out.println("[AdaptiveHUD] getCurrentHUDLight: player is null, using default brightness " + brightness);
        } else {
            int x = (int) Math.floor(player.posX);
            int y = (int) Math.floor(player.posY + player.getEyeHeight());
            int z = (int) Math.floor(player.posZ);

            World world = player.worldObj;

            int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
            int blockLight = world.getBlockLightValue(x, y, z);

            float sampled = Math.max(skyLight, blockLight) / 15.0F;

            final float MIN = 0.2F;
            final float MAX = 1.0F;
            if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
            if (sampled > MAX) sampled = MAX;

            final float ALPHA = 0.2F;
            float next = lastBrightness * (1.0F - ALPHA) + sampled * ALPHA;
            lastBrightness = next;

            brightness = next;

            // System.out.println("[AdaptiveHUD] getCurrentHUDLight: player present, brightness = " + brightness);
        }

        return brightness;
    }

    @SuppressWarnings("unused")
    public static void setTargetBrightness(float target) {
        final float MIN = 0.2F;
        final float MAX = 1.0F;
        if (Float.isNaN(target) || target < MIN) target = MIN;
        if (target > MAX) target = MAX;

        final float ALPHA = 0.2F;
        lastBrightness = lastBrightness * (1.0F - ALPHA) + target * ALPHA;
    }
}
