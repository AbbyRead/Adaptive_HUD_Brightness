package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.HUDBrightnessHelper;
import net.minecraft.src.GuiIngame;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {

    /**
     * Inject just before the hotbar starts drawing, so that all HUD elements
     * (hotbar, health, armor, crosshair) are affected by our brightness.
     */
    @Inject(method = "renderGameOverlay",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/src/InventoryPlayer;currentItem:I",
                    shift = At.Shift.BEFORE))
    private void beforeHotbarRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        float brightness = HUDBrightnessHelper.getSmoothBrightness();
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
        System.out.println("[HUD DRAW] Hotbar Brightness: " + brightness);
    }

    /**
     * Reset the color after all HUD elements have drawn.
     */
    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void afterRenderHUD(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
