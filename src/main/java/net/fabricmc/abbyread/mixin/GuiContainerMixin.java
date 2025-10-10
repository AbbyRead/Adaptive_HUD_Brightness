package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void preDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = ((GuiScreenAccessor)this).getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    // Optional: dim individual slots
    @Inject(method = "drawSlotInventory", at = @At("HEAD"))
    private void preDrawSlotInventory(CallbackInfo ci) {
        Minecraft mc = ((GuiScreenAccessor)this).getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawSlotInventory", at = @At("RETURN"))
    private void postDrawSlotInventory(CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
