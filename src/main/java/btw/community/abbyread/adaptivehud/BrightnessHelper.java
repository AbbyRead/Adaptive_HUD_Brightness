package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

/**
 * Computes an adaptive HUD brightness value based on local light and global sky factors.
 * <p>
 * Adds support for moon-phase brightness, with new moons causing near-complete darkness
 * and full moons providing the most nighttime brightness.
 */
@SuppressWarnings("FieldCanBeLocal")
public class BrightnessHelper {

    private static float lastBrightness = 1.0F;
    private static long lastUpdateTime = System.currentTimeMillis();

    // Debug flag
    public static boolean DEBUG = true;

    // Cached debug values
    private static float cachedBrightness = 1.0F;
    private static int cachedFeetBlockLight = 0;
    private static int cachedHeadBlockLight = 0;
    private static int cachedSkyLightRaw = 0;
    private static float cachedSunFactor = 0.0F;
    private static float cachedMoonFactor = 0.0F;
    private static boolean cachedCanSeeSky = false;

    // Additional cached values for detailed debug
    private static int cachedMoonPhase = 0;
    private static float cachedMoonBrightness = 0.0F;
    private static float cachedNightBrightnessBase = 0.0F;
    private static float cachedNightBlend = 0.0F;
    private static float cachedGlobalBrightness = 0.0F;
    private static float cachedLocalBrightness = 0.0F;
    private static float cachedSampled = 0.0F;
    private static int cachedAdjustedSkyLight = 0;
    private static int cachedSkylightSubtracted = 0;

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

        // Cache for debug
        cachedFeetBlockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, (int) Math.floor(player.posY), z);
        cachedHeadBlockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
        cachedSkyLightRaw = Math.max(world.getSavedLightValue(EnumSkyBlock.Sky, x, (int) Math.floor(player.posY), z),
                world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));

        // --- Apply global darkening (night / weather) ---
        int adjustedSkyLight = skyLightRaw - world.skylightSubtracted;
        if (adjustedSkyLight < 0) adjustedSkyLight = 0;

        // Cache for debug
        cachedAdjustedSkyLight = adjustedSkyLight;
        cachedSkylightSubtracted = world.skylightSubtracted;

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

            // Cache for debug
            cachedMoonPhase = moonPhase;
            cachedMoonBrightness = moonBrightness;
            cachedNightBrightnessBase = nightBrightnessBase;
            cachedNightBlend = nightBlend;
            cachedGlobalBrightness = globalBrightness;

            // During day, use adjusted sky light; at night, use moon-based brightness
            if (sunFactor > 0.5F) {
                // Daytime: scale local brightness by sunlight
                localBrightness = Math.max(localBrightness, adjustedSkyLight / 15.0F * sunFactor);
            }

            // Cache for debug
            cachedSunFactor = sunFactor;
            cachedMoonFactor = world.getCurrentMoonPhaseFactor();
            cachedCanSeeSky = world.canBlockSeeTheSky(x, y, z);
        }

        // --- Combine local and global contributions ---
        float sampled = localBrightness + globalBrightness;

        // Cache for debug
        cachedLocalBrightness = localBrightness;
        cachedSampled = sampled;

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
        cachedBrightness = lastBrightness;

        return lastBrightness;
    }

    /**
     * Renders debug information about brightness calculations.
     * Should be called after all HUD rendering is complete with color reset to white.
     */
    public static void renderDebugInfo(FontRenderer fontRenderer, boolean showDebugScreen) {
        if (!showDebugScreen || !DEBUG || fontRenderer == null) return;

        // Ensure color is reset before drawing debug text
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float localBrightness = Math.max(cachedFeetBlockLight, cachedHeadBlockLight) / 15.0F;
        float rawSkyBrightness = cachedSkyLightRaw / 15.0F;

        fontRenderer.drawStringWithShadow(String.format("HUD Brightness: %.2f", cachedBrightness), 2, 132, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("Block Light: %d (%.2f)", Math.max(cachedFeetBlockLight, cachedHeadBlockLight), localBrightness), 2, 142, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("Sky Light Raw: %d (%.2f)", cachedSkyLightRaw, rawSkyBrightness), 2, 152, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("Sun Factor: %.2f", cachedSunFactor), 2, 162, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("Moon Phase: %.2f", cachedMoonFactor), 2, 172, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("Can See Sky: %s", cachedCanSeeSky), 2, 182, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("HUD=%.2f ← smooth(%.2f) ← clamp(%.2f+%.2f=%.2f) ←", cachedBrightness, cachedSampled, cachedLocalBrightness, cachedGlobalBrightness, cachedLocalBrightness+cachedGlobalBrightness), 150, 162, 0xFFFFFF);
        fontRenderer.drawStringWithShadow(String.format("(%d/15+(0.1+0.2×%.2f)×(1-%.2f)) [moon=%d, sun=%.2f]", Math.max(cachedFeetBlockLight, cachedHeadBlockLight), cachedMoonBrightness, cachedSunFactor, cachedMoonPhase, cachedSunFactor), 150, 172, 0xFFFFFF);
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