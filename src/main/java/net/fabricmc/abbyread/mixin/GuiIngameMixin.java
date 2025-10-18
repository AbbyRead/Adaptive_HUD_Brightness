package net.fabricmc.abbyread.mixin;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DiscouragedShift", "UnnecessaryLocalVariable", "RedundantIfStatement", "FieldCanBeLocal"})
@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    @Unique
    private final boolean DEBUG = true;

    @Final
    @Shadow
    private Minecraft mc;

    // Cache debug values to avoid recalculating
    @Unique
    private float cachedBrightness;
    @Unique
    private int cachedFeetBlockLight;
    @Unique
    private int cachedHeadBlockLight;
    @Unique
    private int cachedSkyLightRaw;
    @Unique
    private float cachedSunFactor;
    @Unique
    private float cachedMoonFactor;
    @Unique
    private boolean cachedCanSeeSky;

    // --- Compatibility ---
    // Freelook is of particular concern because it's awesome, but it prevents the crosshair
    //   reticule from dimming the way it should when using Adaptive HUD Brightness.
    @Unique
    private BTWAddon getFreeLookAddon() {
        // "btwfreelook" is the modID of jeffinitup's btw-freelook addon
        BTWAddon addon = AddonHandler.getModByID("btwfreelook");
        return addon; // returns null if not loaded
    }
    @Unique
    private boolean isFreeLookActive() {
        BTWAddon freeLook = getFreeLookAddon();
        if (freeLook == null) return false;
        return true; // freelook is running
    }
    @Unique
    private void applyBrightnessCompat() {
        if (isFreeLookActive()) {
            // Opportunity to do something special if FreeLookAddon is present
            float b = getHudBrightness();
            GL11.glColor4f(b, b, b, 1.0F);
        } else {
            // Default behavior
            applyBrightness();
        }
    }



    // --- Utility methods ---
    @Unique
    private float getHudBrightness() {
        if (mc == null || mc.thePlayer == null || ((MinecraftAccessor) mc).getIsGamePaused()) return 1.0F;
        World world = mc.thePlayer.worldObj;

        int x = (int) Math.floor(mc.thePlayer.posX);
        int y = (int) Math.floor(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        int z = (int) Math.floor(mc.thePlayer.posZ);

        // Get all the raw values - using EnumSkyBlock.Block for ONLY artificial light
        cachedFeetBlockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, (int) Math.floor(mc.thePlayer.posY), z);
        cachedHeadBlockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);

        cachedSkyLightRaw = Math.max(world.getSavedLightValue(EnumSkyBlock.Sky, x, (int) Math.floor(mc.thePlayer.posY), z),
                world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));

        cachedSunFactor = world.getSunBrightness(1.0F);
        cachedMoonFactor = world.getCurrentMoonPhaseFactor();
        cachedCanSeeSky = world.canBlockSeeTheSky(x, y, z);

        cachedBrightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);

        return cachedBrightness;
    }

    @Unique
    private void applyBrightness() {
        float b = getHudBrightness();
        GL11.glColor4f(b, b, b, 1.0F);
    }

    @Unique
    private void resetColor() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // --- Debug rendering (separate from main HUD rendering to avoid corruption) ---
    @Inject(
            method = "renderGameOverlay",
            at = @At("TAIL")
    )
    private void renderDebugInfo(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (!mc.gameSettings.showDebugInfo || !DEBUG) return;

        // Ensure color is reset before drawing debug text
        resetColor();

        FontRenderer fr = this.mc.fontRenderer;
        float localBrightness = Math.max(cachedFeetBlockLight, cachedHeadBlockLight) / 15.0F;
        float rawSkyBrightness = cachedSkyLightRaw / 15.0F;

        fr.drawStringWithShadow(String.format("HUD Brightness: %.2f", cachedBrightness), 2, 132, 0xFFFFFF);
        fr.drawStringWithShadow(String.format("Block Light: %d (%.2f)", Math.max(cachedFeetBlockLight, cachedHeadBlockLight), localBrightness), 2, 142, 0xFFFFFF);
        fr.drawStringWithShadow(String.format("Sky Light Raw: %d (%.2f)", cachedSkyLightRaw, rawSkyBrightness), 2, 152, 0xFFFFFF);
        fr.drawStringWithShadow(String.format("Sun Factor: %.2f", cachedSunFactor), 2, 162, 0xFFFFFF);
        fr.drawStringWithShadow(String.format("Moon Phase: %.2f", cachedMoonFactor), 2, 172, 0xFFFFFF);
        fr.drawStringWithShadow(String.format("Can See Sky: %s", cachedCanSeeSky), 2, 182, 0xFFFFFF);
    }

    // --- Action Bar (health, armor, food) dimming ---
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiIngame;func_110327_a(II)V", // player stats render method
                    shift = At.Shift.BEFORE
            )
    )
    private void prePlayerStatsRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (isFreeLookActive()) {
            applyBrightnessCompat();
        } else {
            applyBrightness();
        }
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiIngame;func_110327_a(II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void postPlayerStatsRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }


    // --- XP bar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PlayerControllerMP;func_78763_f()Z", shift = At.Shift.AFTER))
    private void preXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endSection()V", shift = At.Shift.BEFORE))
    private void postXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

    // --- Hotbar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;drawTexturedModalRect(IIIIII)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void preHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderHelper;enableGUIStandardItemLighting()V", shift = At.Shift.BEFORE))
    private void postHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        resetColor();
    }

    // --- Inventory slot + item damage bar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderInventorySlot(IIIF)V", shift = At.Shift.BEFORE))
    private void preInventorySlotRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderInventorySlot(IIIF)V", shift = At.Shift.AFTER))
    private void postInventorySlotRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

    // --- Chat display dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiNewChat;drawChat(I)V", shift = At.Shift.BEFORE))
    private void preChatRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiNewChat;drawChat(I)V", shift = At.Shift.AFTER))
    private void postChatRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

}