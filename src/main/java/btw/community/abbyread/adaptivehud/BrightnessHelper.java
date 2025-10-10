package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

public class BrightnessHelper {

    // smoothed brightness value used by HUD rendering (0.0 - 1.0 range,
    // but we clamp to a readable minimum)
    private static float lastBrightness = 1.0f;

    /**
     * Returns a smoothed brightness value for the HUD.
     * 0.0 = completely dark, 1.0 = full brightness
     * <p>
     * This method samples the player's current eye-position light (sky and block)
     * and applies smoothing to avoid flicker. It also clamps the minimum so
     * HUD remains readable in total darkness.
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) return 1.0f;

        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        World world = player.worldObj;

        int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
        int blockLight = world.getBlockLightValue(x, y, z);   // block light (0–15)

        float sampled = Math.max(skyLight, blockLight) / 15.0f;

        // Clamp minimum/maximum so HUD stays readable
        final float MIN = 0.2f;
        final float MAX = 1.0f;
        if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
        if (sampled > MAX) sampled = MAX;

        // Smooth interpolation (exponential moving average)
        final float ALPHA = 0.2f; // how quickly it moves toward the sampled value
        float next = lastBrightness * (1.0f - ALPHA) + sampled * ALPHA;
        lastBrightness = next;

        return next;
    }

    /**
     * Allow external code to nudge the internal brightness toward a target.
     * EntityRendererMixin currently uses this to set a frame's target brightness.
     * <p>
     * This simply moves lastBrightness a little toward the provided target
     * using the same smoothing factor used in getCurrentHUDLight.
     */
    public static void setTargetBrightness(float target) {
        final float MIN = 0.2f;
        final float MAX = 1.0f;
        if (Float.isNaN(target) || target < MIN) target = MIN;
        if (target > MAX) target = MAX;

        final float ALPHA = 0.2f;
        lastBrightness = lastBrightness * (1.0f - ALPHA) + target * ALPHA;
    }

    public static void applyGLBrightness(EntityPlayer player) {
        float b = getCurrentHUDLight(player);
        GL11.glColor4f(b, b, b, 1.0F);
    }

}
