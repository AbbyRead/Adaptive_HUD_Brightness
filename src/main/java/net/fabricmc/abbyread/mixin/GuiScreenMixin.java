package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin {

    /**
     * Applies brightness dimming before any GUI elements (buttons, slots, etc.)
     * are drawn.
     */
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void applyGuiBrightness(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
        System.out.println("[AdaptiveHUD] GuiScreenMixin drawScreen fired");

    }

    /**
     * Resets the GL color state so subsequent rendering (like tooltips)
     * isn't tinted.
     */
    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void resetGuiBrightness(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
