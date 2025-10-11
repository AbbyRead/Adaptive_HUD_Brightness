package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RenderItem mixin that only dims items when they are being drawn for the HUD (hotbar).
 * Uses a thread-local flag set at the start of renderItemIntoGUI and cleared at return,
 * so any GL color calls inside that method can consult the flag to decide whether to apply brightness.
 */
@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    // ThreadLocal flag so nested calls won't clobber one another if something weird happens.
    @Unique
    private static final ThreadLocal<Boolean> DIM_FLAG = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * Set DIM_FLAG when entering renderItemIntoGUI so redirected GL calls inside that method know to dim.
     * Method signature based on MCP 1.6.4: renderItemIntoGUI(FontRenderer, TextureManager, ItemStack, int, int)
     */
    @Inject(
            method = "renderItemIntoGUI",
            at = @At("HEAD"),
            remap = false
    )
    private void onRenderItemIntoGUIHead(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean shouldDim = mc != null && mc.thePlayer != null && mc.currentScreen == null;
        DIM_FLAG.set(shouldDim);
    }

    /**
     * Clear DIM_FLAG on return.
     */
    @Inject(
            method = "renderItemIntoGUI",
            at = @At("RETURN"),
            remap = false
    )
    private void onRenderItemIntoGUIReturn(FontRenderer font, TextureManager tm, ItemStack stack, int x, int y, CallbackInfo ci) {
        DIM_FLAG.set(Boolean.FALSE);
    }

    /**
     * Redirect GL11.glColor4f inside renderItemIntoGUI so we can multiply by brightness when DIM_FLAG is set.
     * This keeps inventory drawing untouched because DIM_FLAG will be false there.
     *
     * Note: remap=false because we're redirecting the LWJGL call as it appears in MCP / 1.6.4 builds.
     */
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
        // default: don't change
        GL11.glColor4f(r, g, b, a);
    }
}
