package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Slot;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void beforeDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void afterDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    @Inject(method = "drawSlotInventory", at = @At("HEAD"))
    private void beforeSlotDraw(Slot slot, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawSlotInventory", at = @At("RETURN"))
    private void afterSlotDraw(Slot slot, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    @Inject(method = "drawItemStack", at = @At("HEAD"))
    private void beforeDrawItemStack(ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawItemStack", at = @At("RETURN"))
    private void afterDrawItemStack(ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
