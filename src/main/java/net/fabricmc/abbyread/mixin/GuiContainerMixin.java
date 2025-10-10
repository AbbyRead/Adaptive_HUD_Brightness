package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {

    /**
     * Dim everything at the start of drawScreen, before background draws.
     * Disables lighting to make the tint effective.
     */
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void beforeDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);

        // Disable lighting so color tint works
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    /**
     * Reset GL color and re-enable lighting at the end of drawScreen.
     */
    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void afterDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPopMatrix();
    }

    /**
     * Dim each slot before it's drawn.
     */
    @Inject(method = "drawSlotInventory", at = @At("HEAD"))
    private void beforeSlotDraw(Slot slot, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    /**
     * Reset color after each slot.
     */
    @Inject(method = "drawSlotInventory", at = @At("RETURN"))
    private void afterSlotDraw(Slot slot, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    /**
     * Dim dragged items and items drawn by drawItemStack.
     */
    @Inject(method = "drawItemStack", at = @At("HEAD"))
    private void beforeDrawItemStack(ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    /**
     * Reset color after each item stack draw.
     */
    @Inject(method = "drawItemStack", at = @At("RETURN"))
    private void afterDrawItemStack(ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
