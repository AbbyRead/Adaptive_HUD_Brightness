package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {

    @Final
    @Shadow
    private Minecraft mc;

    /**
     * Inject just before the HUD elements (health, armor, food, hotbar) are drawn.
     * BtW / MC 1.6.4: trigger before InventoryPlayer.currentItem is accessed.
     */
    @SuppressWarnings("all")
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/src/InventoryPlayer;currentItem:I",
                    shift = At.Shift.BEFORE
            )
    )
    private void preHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        // Smoothly calculate current HUD brightness
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);

        // Apply GL11 color to dim the HUD textures
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    /**
     * Reset GL11 color after the HUD has been drawn
     */
    @Inject(
            method = "renderGameOverlay",
            at = @At("TAIL")
    )
    private void postHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    @Unique
    private boolean pushedXpAttrib = false;

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/PlayerControllerMP;func_78763_f()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void preXpBlock(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        pushedXpAttrib = true;
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endSection()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void postXpBlock(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (!pushedXpAttrib) return; // only pop if we actually pushed
        pushedXpAttrib = false;

        try {
            GL11.glPopAttrib();
        } catch (Throwable t) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

}
