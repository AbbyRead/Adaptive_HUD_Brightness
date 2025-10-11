package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DiscouragedShift")
@Mixin(GuiButton.class)
public abstract class GuiButtonMixin {

    @Inject(
            method = "drawButton(Lnet/minecraft/src/Minecraft;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiButton;drawTexturedModalRect(IIIIII)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0 // first call to drawTexturedModalRect
            )
    )
    private void preDrawButton(Minecraft mc, int mouseX, int mouseY, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }
}
