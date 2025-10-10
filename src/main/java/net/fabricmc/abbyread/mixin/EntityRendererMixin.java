package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void updateCurrentHUDBrightness(float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        // Updates lastBrightness once per tick
        BrightnessHelper.getCurrentHUDLight(player);
    }
}
