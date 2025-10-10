package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {

    /**
     * Modifies the color argument passed to renderString() inside drawString().
     * This dims HUD and inventory text according to ambient light.
     */
    @ModifyArg(
            method = "drawString(Ljava/lang/String;IIIZ)I", // 5-arg version
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/FontRenderer;renderString(Ljava/lang/String;IIIZ)I"
            ),
            index = 3
    )
    private int adjustTextBrightness(int color) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        float brightness = BrightnessHelper.getCurrentHUDLight(player);

        int r = (int) ((color >> 16 & 0xFF) * brightness);
        int g = (int) ((color >> 8 & 0xFF) * brightness);
        int b = (int) ((color & 0xFF) * brightness);
        int alpha = (color >> 24) & 0xFF;

        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    @ModifyArg(
            method = "drawString(Ljava/lang/String;III)I", // 4-arg version
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/FontRenderer;drawString(Ljava/lang/String;IIIZ)I"
            ),
            index = 3
    )
    private int adjustTextBrightnessShort(int color) {
        return adjustTextBrightness(color);
    }

}
