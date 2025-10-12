package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

public class BrightnessHelper {

    private static float lastBrightness = 1.0F;
    private static long lastUpdateTime = System.currentTimeMillis();

    public static float getCurrentHUDLight(EntityPlayer player) {
        float brightness;

        if (player == null || player.worldObj == null) {
            // No player (e.g. menus)
            brightness = 0.8F;
        } else {
            int x = (int) Math.floor(player.posX);
            int y = (int) Math.floor(player.posY + player.getEyeHeight());
            int z = (int) Math.floor(player.posZ);

            World world = player.worldObj;
            int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
            int blockLight = world.getBlockLightValue(x, y, z);

            float sampled = Math.max(skyLight, blockLight) / 15.0F;

            // Clamp to readable range
            final float MIN = 0.2F;
            final float MAX = 1.0F;
            if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
            if (sampled > MAX) sampled = MAX;

            // Compute delta time
            long now = System.currentTimeMillis();
            float deltaSeconds = Math.min((now - lastUpdateTime) / 1000.0F, 0.25F);
            lastUpdateTime = now;

            // Adaptive response: fast brighten, slow darken
            float diff = sampled - lastBrightness;
            float alpha;

            if (diff > 0.0F) {
                // Brighten quickly — approach 70–90% of target per second
                alpha = 1.0F - (float)Math.pow(0.25F, deltaSeconds * 8.0F);
            } else {
                // Darken more gradually — approach ~40–50% of target per second
                alpha = 1.0F - (float)Math.pow(0.25F, deltaSeconds * 3.0F);
            }

            lastBrightness += diff * alpha;
            brightness = lastBrightness;
        }

        return brightness;
    }

    @SuppressWarnings("unused")
    public static void setTargetBrightness(float target) {
        final float MIN = 0.2F;
        final float MAX = 1.0F;
        if (Float.isNaN(target) || target < MIN) target = MIN;
        if (target > MAX) target = MAX;

        float diff = target - lastBrightness;
        float alpha = (diff > 0) ? 0.3F : 0.15F;
        lastBrightness += diff * alpha;
    }
}
