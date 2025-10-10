package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;

public class BrightnessHelper {

    // Last brightness value for smoothing
    private static float lastBrightness = 1.0f;

    /**
     * Returns a smoothed brightness value for HUD rendering (0.2 - 1.0)
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) return lastBrightness;

        float sampled = player.worldObj.getLightBrightness(
                (int) player.posX,
                (int) (player.posY + player.getEyeHeight()),
                (int) player.posZ
        );

        // Clamp sampled to 0.2 - 1.0
        sampled = 0.2f + 0.8f * sampled;

        // Lightweight exponential smoothing
        final float ALPHA = 0.2f;
        lastBrightness = lastBrightness * (1.0f - ALPHA) + sampled * ALPHA;

        return lastBrightness;
    }

    /**
     * Optional: reset lastBrightness (useful on world change)
     */
    public static void reset() {
        lastBrightness = 1.0f;
    }
}
