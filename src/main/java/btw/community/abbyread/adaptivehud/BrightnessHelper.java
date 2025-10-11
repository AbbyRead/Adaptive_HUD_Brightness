package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

public class BrightnessHelper {

    // Smoothed brightness value used by HUD rendering (0.0 - 1.0 range, clamped to readable minimum)
    private static float lastBrightness = 1.0F;

    /**
     * Returns a smoothed brightness value for the HUD.
     * 0.0 = completely dark, 1.0 = full brightness.
     * <p>
     * Samples the player's current eye-position light (sky and block) and applies smoothing
     * to avoid flicker. Clamps minimum so HUD remains readable in total darkness.
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) return 1.0F;

        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        World world = player.worldObj;

        int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
        int blockLight = world.getBlockLightValue(x, y, z); // block light (0-15)

        float sampled = Math.max(skyLight, blockLight) / 15.0F;

        // Clamp minimum/maximum so HUD stays readable
        final float MIN = 0.2F;
        final float MAX = 1.0F;
        if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
        if (sampled > MAX) sampled = MAX;

        // Smooth interpolation (exponential moving average)
        final float ALPHA = 0.2F; // how quickly it moves toward the sampled value
        float next = lastBrightness * (1.0F - ALPHA) + sampled * ALPHA;
        System.out.println("[AdaptiveHUD] Sky: " + skyLight + ", Block: " + blockLight + ", sampled: " + sampled + ", lastBrightness: " + lastBrightness + ", next: " + next);
        lastBrightness = next;

        return next;
    }

    /**
     * Allow external code to nudge the internal brightness toward a target.
     * EntityRendererMixin uses this to set a frame's target brightness.
     * <p>
     * Moves lastBrightness a little toward the provided target using the same smoothing factor.
     */
    public static void setTargetBrightness(float target) {
        final float MIN = 0.2F;
        final float MAX = 1.0F;
        if (Float.isNaN(target) || target < MIN) target = MIN;
        if (target > MAX) target = MAX;

        final float ALPHA = 0.2F;
        lastBrightness = lastBrightness * (1.0F - ALPHA) + target * ALPHA;
    }
}
