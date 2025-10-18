package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

/**
 * Computes an adaptive HUD brightness value based on local light and global sky factors.
 * <p>
 * Adds support for moon-phase brightness, with new moons causing near-complete darkness
 * and full moons providing the most nighttime brightness.
 */
public class BrightnessHelper {

    private static float lastBrightness = 1.0F;
    private static long lastUpdateTime = System.currentTimeMillis();

    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null) {
            return 0.8F;
        }

        World world = player.worldObj;

        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        // --- Sample local light levels ---
        int skyLightRaw = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
        int blockLight = world.getBlockLightValue(x, y, z);

        // --- Apply global darkening (night / weather) ---
        int adjustedSkyLight = skyLightRaw - world.skylightSubtracted;
        if (adjustedSkyLight < 0) adjustedSkyLight = 0;

        // --- Normalize local block light ---
        float localBrightness = blockLight / 15.0F;

        // --- Compute global sky contribution only if player can see the sky ---
        float globalBrightness = 0.0F;
        if (adjustedSkyLight > 0) {
            float sunFactor = world.getSunBrightness(1.0F);

            // Get moon phase (0-7): 0 = full moon, 4 = new moon
            int moonPhase = world.provider.getMoonPhase(world.getWorldTime());

            // Calculate moon brightness based on distance from new moon (phase 4)
            // Full moon (phase 0): moonBrightness = 1.0
            // New moon (phase 4): moonBrightness = 0.0
            float moonBrightness = Math.abs(4 - moonPhase) / 4.0F;

            // Calculate night brightness based on moon phase
            // Full moon: 0.3 brightness
            // New moon: 0.1 brightness
            float nightBrightnessBase = 0.1F + 0.2F * moonBrightness;

            // nightBlend: 1.0 at midnight, 0.0 at noon
            float nightBlend = 1.0F - sunFactor;
            globalBrightness = nightBrightnessBase * nightBlend;

            // Scale local brightness by sunlight (reduces at night)
            localBrightness *= sunFactor;
        }

        // --- Combine local and global contributions ---
        float sampled = Math.max(localBrightness, adjustedSkyLight / 15.0F) + globalBrightness;

        // --- Clamp for readability ---
        final float MIN = 0.05F;  // Lowered to allow darker nights
        final float MAX = 1.0F;
        if (Float.isNaN(sampled) || sampled < MIN) sampled = MIN;
        if (sampled > MAX) sampled = MAX;

        // --- Temporal smoothing (adaptive adjustment) ---
        long now = System.currentTimeMillis();
        float deltaSeconds = Math.min((now - lastUpdateTime) / 1000.0F, 0.25F);
        lastUpdateTime = now;

        float diff = sampled - lastBrightness;
        float alpha = diff > 0.0F
                ? 1.0F - (float) Math.pow(0.25F, deltaSeconds * 8.0F)  // brighten fast
                : 1.0F - (float) Math.pow(0.25F, deltaSeconds * 3.0F); // darken slow

        lastBrightness += diff * alpha;
        return lastBrightness;
    }

    /** Optional manual override */
    @SuppressWarnings("unused")
    public static void setTargetBrightness(float target) {
        final float MIN = 0.2F;
        final float MAX = 1.0F;
        if (Float.isNaN(target) || target < MIN) target = MIN;
        if (target > MAX) target = MAX;

        float diff = target - lastBrightness;
        float alpha = diff > 0 ? 0.3F : 0.15F;
        lastBrightness += diff * alpha;
    }
}