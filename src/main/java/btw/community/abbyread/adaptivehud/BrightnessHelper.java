package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

/**
 * Computes adaptive HUD brightness based on player light exposure,
 * accounting for moon phase and day/night cycles.
 */
public class BrightnessHelper {

    private static float lastBrightness = 1.0F;
    private static long lastUpdateTime = System.currentTimeMillis();

    // Brightness bounds
    private static final float MIN_BRIGHTNESS = 0.1F;
    private static final float MAX_BRIGHTNESS = 1.0F;

    /**
     * Computes the current adaptive HUD brightness for the given player.
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null)
            return 0.8F;

        World world = player.worldObj;
        int x = (int) Math.floor(player.posX);
        int feetY = (int) Math.floor(player.posY);
        int headY = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        int blockLightFeet = world.getBlockLightValue(x, feetY, z);
        int blockLightHead = world.getBlockLightValue(x, headY, z);
        int blockLight = Math.max(blockLightFeet, blockLightHead);

        float localBrightness = blockLight / 15.0F;
        float globalBrightness = computeGlobalBrightness(world);

        float target = localBrightness + globalBrightness;

        lastBrightness = smooth(lastBrightness, target);
        lastBrightness = clamp(lastBrightness);

        return lastBrightness;
    }

    /**
     * Computes the contribution of sky and moonlight to overall brightness.
     */
    private static float computeGlobalBrightness(World world) {
        float sunFactor = world.getSunBrightness(1.0F);

        int moonPhase = world.provider.getMoonPhase(world.getWorldTime());
        float moonBrightness = Math.abs(4 - moonPhase) / 4.0F;

        float nightBase = 0.25F * moonBrightness;
        float nightBlend = 1.0F - sunFactor;
        float nightScale = sunFactor < 0.5F ? 0.5F : 1.0F;

        return nightBase * nightBlend * nightScale;
    }

    /**
     * Smoothly interpolates current brightness toward a target brightness.
     */
    private static float smooth(float current, float target) {
        long now = System.currentTimeMillis();
        float deltaSeconds = Math.min((now - lastUpdateTime) / 1000.0F, 0.25F);
        lastUpdateTime = now;

        float diff = target - current;
        float speed = diff > 0.0F ? 8.0F : 3.0F; // brighten fast, darken slow
        float alpha = 1.0F - (float) Math.pow(0.25F, deltaSeconds * speed);

        return current + diff * alpha;
    }

    /**
     * Clamps brightness to the configured min/max range.
     */
    private static float clamp(float value) {
        if (Float.isNaN(value)) return MIN_BRIGHTNESS;
        return Math.max(MIN_BRIGHTNESS, Math.min(value, MAX_BRIGHTNESS));
    }
}
