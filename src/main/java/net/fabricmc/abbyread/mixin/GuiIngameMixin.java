package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiIngame;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Final
    @Shadow
    private net.minecraft.src.Minecraft mc;

    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void preRenderGameOverlay(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(player);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    private void postRenderGameOverlay(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
