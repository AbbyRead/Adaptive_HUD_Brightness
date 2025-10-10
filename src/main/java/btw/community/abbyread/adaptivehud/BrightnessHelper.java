package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

public class BrightnessHelper {

    // Smoothed brightness value (0.0 - 1.0)
    private static float lastBrightness = 1.0f;

    /**
     * Returns the brightness for the HUD based on the player's head position.
     * Combines sky light and block light and clamps to a readable minimum.
     * Optionally smooths transitions for nicer visuals.
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) return 1.0f;

        // Player's eye-level block coordinates
        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        World world = player.worldObj;

        // Raw light values
        int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
        int blockLight = world.getBlockLightValue(x, y, z);

        // Choose the brighter source at this block
        float sampled = Math.max(skyLight, blockLight) / 15.0f;

        // Clamp so HUD is always readable
        final float MIN = 0.2f;
        final float MAX = 1.0f;
        if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
        if (sampled > MAX) sampled = MAX;

        // Smooth brightness changes (optional, tweak ALPHA for responsiveness)
        final float ALPHA = 0.2f;
        float next = lastBrightness * (1.0f - ALPHA) + sampled * ALPHA;
        lastBrightness = next;

        // Debug prints (optional)
        // System.out.println("[AdaptiveHUD] x=" + x + " y=" + y + " z=" + z +
        //        " sky=" + skyLight + " block=" + blockLight +
        //        " sampled=" + sampled + " smoothed=" + next);

        return next;
    }

    /**
     * Apply the current HUD brightness to OpenGL
     */
    public static void applyGLBrightness(EntityPlayer player) {
        float b = getCurrentHUDLight(player);
        GL11.glColor4f(b, b, b, 1.0F);
    }

    /**
     * Reset GL color to default (white)
     */
    public static void resetGLBrightness() {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
