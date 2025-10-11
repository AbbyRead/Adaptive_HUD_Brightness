package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Shadow
    protected Minecraft mc;

    @Unique
    private boolean pushedAttrib = false;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void preDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;
        System.out.println("[AdaptiveHUD] GuiScreenMixin.preDrawScreen called for " + mc.currentScreen);

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        pushedAttrib = true;

        // LWJGL2 requires at least 16 floats in the buffer
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, colorBuffer);
        colorBuffer.rewind();

        float r = colorBuffer.get();
        float g = colorBuffer.get();
        float b = colorBuffer.get();
        float a = colorBuffer.get();

        System.out.println("[AdaptiveHUD] GL current color before: R=" + r + " G=" + g + " B=" + b + " A=" + a);

        // Multiply RGB by brightness, keep alpha
        GL11.glColor4f(r * brightness, g * brightness, b * brightness, a);
        System.out.println("[AdaptiveHUD] GL color applied: R=" + (r * brightness)
                + " G=" + (g * brightness)
                + " B=" + (b * brightness)
                + " A=" + a);

    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!pushedAttrib) return;
        pushedAttrib = false;

        try {
            GL11.glPopAttrib();
        } catch (Throwable t) {
            // Fallback: reset color
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void preDrawScreenTest(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        System.out.println("[AdaptiveHUD] GuiScreenMixin HEAD called!");
    }

}
