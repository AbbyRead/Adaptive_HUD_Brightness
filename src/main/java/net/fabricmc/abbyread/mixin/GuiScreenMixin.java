package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DiscouragedShift")
@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Shadow
    protected Minecraft mc;

    @Inject(method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiButton;drawButton(Lnet/minecraft/src/Minecraft;II)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void preDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);

        // Set color manually
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        // Reset color manually
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
