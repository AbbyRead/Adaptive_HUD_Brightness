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

    /* These maybe don't work.  I don't know.
    @Inject(method = "renderInventorySlot", at = @At("HEAD"))
    private void applyHUDBrightnessHead(int par1, int par2, int par3, float par4, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "renderInventorySlot", at = @At("TAIL"))
    private void resetHUDBrightnessTail(int par1, int par2, int par3, float par4, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    */

    /**
     * Apply brightness before HUD rendering (health, armor, hotbar, etc.).
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
    private void beforeHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    /**
     * Reset color after HUD has been drawn.
     */
    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void afterHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

