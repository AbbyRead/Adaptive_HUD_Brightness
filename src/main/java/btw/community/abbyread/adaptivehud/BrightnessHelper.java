package btw.community.abbyread.adaptivehud;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

/**
 * Computes adaptive HUD brightness based on player light exposure,
 * accounting for moon phase and day/night cycles.
 *
 * Optimized for performance: when DEBUG == false, all debug
 * tracking and string rendering are skipped.
 */
public class BrightnessHelper {

    private static float lastBrightness = 1.0F;
    private static long lastUpdateTime = System.currentTimeMillis();
    public static boolean DEBUG = true;

    // Brightness bounds
    private static final float MIN_BRIGHTNESS = 0.1F;
    private static final float MAX_BRIGHTNESS = 1.0F;

    // Debug info holder
    private static final DebugState debug = new DebugState();

    /**
     * Computes the current adaptive HUD brightness for the given player.
     */
    public static float getCurrentHUDLight(EntityPlayer player) {
        if (player == null || player.worldObj == null)
            return 0.8F;

        World world = player.worldObj;
        int x = (int) Math.floor(player.posX);
        int feetY = (int) Math.floor(player.posY);               // feet
        int headY = (int) Math.floor(player.posY + player.getEyeHeight()); // head
        int z = (int) Math.floor(player.posZ);

        int blockLightFeet = world.getBlockLightValue(x, feetY, z);
        int blockLightHead = world.getBlockLightValue(x, headY, z);
        int blockLight = Math.max(blockLightFeet, blockLightHead);

        int skyLightFeet = world.getSavedLightValue(EnumSkyBlock.Sky, x, feetY, z);
        int skyLightHead  = world.getSavedLightValue(EnumSkyBlock.Sky, x, headY, z);
        int rawSky = Math.max(skyLightFeet, skyLightHead);

        int adjustedSky = Math.max(rawSky - world.skylightSubtracted, 0);

        float localBrightness = blockLight / 15.0F;
        float globalBrightness = computeGlobalBrightness(world);

        // Combine contributions
        float target = localBrightness + globalBrightness;

        // Update debug info only when needed
        if (DEBUG) debug.capture(blockLight, adjustedSky, localBrightness, globalBrightness, target);

        // Smooth transition and clamp
        lastBrightness = smooth(lastBrightness, target);
        lastBrightness = clamp(lastBrightness);

        if (DEBUG) debug.finalBrightness = lastBrightness;

        return lastBrightness;
    }

    /**
     * Computes the contribution of sky and moonlight to overall brightness.
     */
    private static float computeGlobalBrightness(World world) {
        float sunFactor = world.getSunBrightness(1.0F);

        // Moon phase: 0 = full, 4 = new
        int moonPhase = world.provider.getMoonPhase(world.getWorldTime());
        float moonBrightness = Math.abs(4 - moonPhase) / 4.0F;

        float nightBase = 0.25F * moonBrightness;
        float nightBlend = 1.0F - sunFactor;
        float nightScale = sunFactor < 0.5F ? 0.5F : 1.0F;

        if (DEBUG) {
            debug.nightBase = nightBase;
            debug.nightBlend = nightBlend;
            debug.nightScale = nightScale;
            debug.moonPhase = moonPhase;
            debug.moonBrightness = moonBrightness;
        }

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

    /**
     * Renders on-screen debug info if enabled.
     */
    public static void renderDebugInfo(FontRenderer fontRenderer, boolean showDebugScreen) {
        if (!showDebugScreen || !DEBUG || fontRenderer == null) return;

        GL11.glColor4f(1, 1, 1, 1);
        debug.render(fontRenderer);
    }

    // ----------------------------------------------------------
    // Internal debug record
    // ----------------------------------------------------------
    private static class DebugState {
        float finalBrightness;
        float local, global, target;
        int blockLight, skyLight;
        float nightBase, nightBlend, nightScale;
        int moonPhase;
        float moonBrightness;

        void capture(int blockLight, int skyLight, float local, float global, float target) {
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            this.local = local;
            this.global = global;
            this.target = target;
        }

        void render(FontRenderer fr) {
            int y = 122;
            fr.drawStringWithShadow(String.format("HUD Brightness: %.2f", finalBrightness), 2, y, 0xFFFFFF);
            fr.drawStringWithShadow(String.format("Block Light: %d (%.2f)", blockLight, local), 2, y + 10, 0xFFFFFF);
            fr.drawStringWithShadow(String.format("Sky Light: %d (%.2f)", skyLight, global), 2, y + 20, 0xFFFFFF);
            fr.drawStringWithShadow(String.format("Total (local+global)=%.2f", target), 2, y + 30, 0xFFFFFF);

            // Added detailed breakdown
            fr.drawStringWithShadow("nightBase * nightBlend * nightScale", 2, y + 45, 0xFFFFFF);
            fr.drawStringWithShadow(
                    String.format("%.3f * %.3f * %.3f", nightBase, nightBlend, nightScale),
                    2, y + 55, 0xFFFFFF
            );
            fr.drawStringWithShadow(String.format("Moon Phase: %d (%.2f)", moonPhase, moonBrightness), 2, y + 70, 0xFFFFFF);
        }
    }
}
