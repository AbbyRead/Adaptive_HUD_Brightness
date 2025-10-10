package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class FontRendererMixin {

    @Unique
    private void applyBrightness() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
            GL11.glColor4f(brightness, brightness, brightness, 1.0F);
        } else {
            // fallback for menus / no player loaded
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

    @Unique
    private void resetBrightness() {
        org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    @Inject(method = "drawString(Ljava/lang/String;III)I", at = @At("HEAD"))
    private void preDrawString1(String text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
        applyBrightness();
    }

    @Inject(method = "drawString(Ljava/lang/String;IIIZ)I", at = @At("HEAD"))
    private void preDrawString2(String text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        applyBrightness();
    }

    @Inject(method = "drawString(Ljava/lang/String;III)I", at = @At("RETURN"))
    private void postDrawString1(String text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
        resetBrightness();
    }

    @Inject(method = "drawString(Ljava/lang/String;IIIZ)I", at = @At("RETURN"))
    private void postDrawString2(String text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        resetBrightness();
    }
}

