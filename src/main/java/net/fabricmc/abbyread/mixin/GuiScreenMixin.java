package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @Shadow
    protected net.minecraft.src.Minecraft mc;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void preDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
