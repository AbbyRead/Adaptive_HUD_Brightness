package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RenderItem mixin that dims items and item damage bars when being drawn in the HUD.
 */
@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Invoker("renderQuad")
    public abstract void invokeRenderQuad(Tessellator tess, int x, int y, int w, int h, int color);

    // ThreadLocal flag so nested calls won't clobber one another
    @Unique
    private static final ThreadLocal<Boolean> DIM_FLAG = ThreadLocal.withInitial(() -> Boolean.FALSE);

    // --- renderItemIntoGUI ---
    @Inject(
            method = "renderItemIntoGUI",
            at = @At("HEAD")
    )
    private void onRenderItemIntoGUIHead(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean shouldDim = mc != null && mc.thePlayer != null && mc.currentScreen == null;
        DIM_FLAG.set(shouldDim);
    }

    @Inject(
            method = "renderItemIntoGUI",
            at = @At("RETURN")
    )
    private void onRenderItemIntoGUIReturn(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, CallbackInfo ci) {
        DIM_FLAG.set(Boolean.FALSE);
    }

    // --- renderItemOverlayIntoGUI ---
    @Inject(
            method = "renderItemOverlayIntoGUI(Lnet/minecraft/src/FontRenderer;Lnet/minecraft/src/TextureManager;Lnet/minecraft/src/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD")
    )
    private void onRenderOverlayHead(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean shouldDim = mc != null && mc.thePlayer != null && mc.currentScreen == null;
        DIM_FLAG.set(shouldDim);
    }

    @Inject(
            method = "renderItemOverlayIntoGUI(Lnet/minecraft/src/FontRenderer;Lnet/minecraft/src/TextureManager;Lnet/minecraft/src/ItemStack;IILjava/lang/String;)V",
            at = @At("RETURN")
    )
    private void onRenderOverlayReturn(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        DIM_FLAG.set(Boolean.FALSE);
    }

    // --- Redirect GL11.glColor4f to apply HUD brightness inside renderItemIntoGUI ---
    @Redirect(
            method = "renderItemIntoGUI",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"
            ),
            remap = false
    )
    private void adaptivehud$applyBrightness(float r, float g, float b, float a) {
        boolean dim = Boolean.TRUE.equals(DIM_FLAG.get());
        if (dim) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null) {
                float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
                GL11.glColor4f(r * brightness, g * brightness, b * brightness, a);
                return;
            }
        }
        GL11.glColor4f(r, g, b, a);
    }
    @Redirect(
            method = "renderItemOverlayIntoGUI(Lnet/minecraft/src/FontRenderer;Lnet/minecraft/src/TextureManager;Lnet/minecraft/src/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderItem;renderQuad(Lnet/minecraft/src/Tessellator;IIIII)V"
            )
    )
    private void dimItemQuad(RenderItem instance, Tessellator tess, int x, int y, int w, int h, int color) {
        boolean dim = Boolean.TRUE.equals(DIM_FLAG.get());
        if (dim) {
            float brightness = BrightnessHelper.getCurrentHUDLight(Minecraft.getMinecraft().thePlayer);
            int r = (color >> 16 & 0xFF);
            int g = (color >> 8 & 0xFF);
            int b = (color & 0xFF);
            int newColor = ((int)(r * brightness) << 16) | ((int)(g * brightness) << 8) | (int)(b * brightness);
            ((RenderItemMixin)(Object)instance).invokeRenderQuad(tess, x, y, w, h, newColor);
            return;
        }
        ((RenderItemMixin)(Object)instance).invokeRenderQuad(tess, x, y, w, h, color);
    }


    // --- Redirect GL11.glColor4f to apply HUD brightness inside renderItemOverlayIntoGUI ---
    @Redirect(
            method = "renderItemOverlayIntoGUI(Lnet/minecraft/src/FontRenderer;Lnet/minecraft/src/TextureManager;Lnet/minecraft/src/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"
            ),
            remap = false
    )
    private void adaptivehud$applyBrightnessOverlay(float r, float g, float b, float a) {
        boolean dim = Boolean.TRUE.equals(DIM_FLAG.get());
        if (dim) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null) {
                float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
                GL11.glColor4f(r * brightness, g * brightness, b * brightness, a);
                return;
            }
        }
        GL11.glColor4f(r, g, b, a);
    }
}
