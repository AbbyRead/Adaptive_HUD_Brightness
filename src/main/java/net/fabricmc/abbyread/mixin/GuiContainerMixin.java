package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {

    @Inject(method = "drawScreen", at = @At(
            value = "INVOKE",
            target = "Lemi/dev/emi/emi/Hooks;renderBackground(II)V",
            shift = At.Shift.AFTER
            ), remap = false
    )
    private void afterEMIBackground(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = ((GuiScreenAccessor) this).getMinecraft();
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    // Keep your RETURN injection to reset
    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}