package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

/**
 * Computes an adaptive HUD brightness value based on local light and global sky factors.
 * </p>
 * Adds support for moon-phase brightness, so the HUD is slightly brighter on full-moon nights.
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

        // --- Normalize to [0–1] range ---
        float sampled = Math.max(adjustedSkyLight, blockLight) / 15.0F;

        // --- Blend in global lighting factors (sun + moon) ---
        float sunFactor = world.getSunBrightness(1.0F);         // 0 at midnight, 1 at noon
        float moonFactor = world.getCurrentMoonPhaseFactor();   // 0 new moon → 1 full moon

        // Weighting:
        //   - Local sampled light dominates (70%)
        //   - Sun and moon add ambient context (20% + 10%)
        sampled = (sampled * 0.7F) + ((sunFactor * 0.2F) + (moonFactor * 0.1F));

        // --- Clamp for readability ---
        final float MIN = 0.2F;
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
