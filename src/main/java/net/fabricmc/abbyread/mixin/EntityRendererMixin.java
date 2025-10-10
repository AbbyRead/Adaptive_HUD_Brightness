package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Shadow private Minecraft mc;

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void updateHudBrightnessTarget (float partialTicks, CallbackInfo ci) {
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) return;

        // Player eye position
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        int z = MathHelper.floor_double(mc.thePlayer.posZ);

        // Use Minecraft's combined light brightness (0.0F–1.0F)
        float lightLevel = mc.theWorld.getLightBrightness(x, y, z);

        // Convert to 0.2–1.0 HUD brightness
        float target = 0.2F + 0.8F * lightLevel;

        // Update helper
        BrightnessHelper.setTargetBrightness(target);
    }
    @SuppressWarnings("all")
    @Inject(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiScreen;drawScreen(IIF)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void beforeGuiDraw(float partialTicks, CallbackInfo ci) {
        Minecraft mc = this.mc;
        if (mc == null || mc.thePlayer == null) return;

        float brightness = btw.community.abbyread.adaptivehud.BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
        System.out.println("[AdaptiveHUD] Tinting GUI at brightness=" + brightness);
    }

    @Inject(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiScreen;drawScreen(IIF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterGuiDraw(float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
