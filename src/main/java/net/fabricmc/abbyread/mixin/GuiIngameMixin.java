package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void beforeRenderGameOverlay(float partialTicks, boolean unused, int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    private void afterRenderGameOverlay(float partialTicks, boolean unused, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}


