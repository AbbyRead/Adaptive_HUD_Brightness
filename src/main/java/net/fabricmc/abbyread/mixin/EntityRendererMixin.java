package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.HUDBrightnessHelper;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Minecraft;
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
        HUDBrightnessHelper.setTargetBrightness(target);
    }
}
