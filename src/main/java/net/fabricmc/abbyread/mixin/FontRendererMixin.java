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
     * Adjust the color argument for 5-arg drawString (with dropShadow).
     */
    @ModifyArg(
            method = "drawString(Ljava/lang/String;IIIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/FontRenderer;renderString(Ljava/lang/String;IIIZ)I"
            ),
            index = 3 // color argument
    )
    private int adjustTextBrightness(int color) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = (mc != null) ? mc.thePlayer : null;

        float brightness = (player != null) ? BrightnessHelper.getCurrentHUDLight(player) : 1.0F;

        // Extract original RGBA
        int alpha = (color >> 24) & 0xFF;
        int red   = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue  = (color) & 0xFF;

        // Apply brightness multiplier to RGB
        red   = (int) (red * brightness);
        green = (int) (green * brightness);
        blue  = (int) (blue * brightness);

        // Repack color
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Adjust the color argument for 4-arg drawString.
     */
    @ModifyArg(
            method = "drawString(Ljava/lang/String;III)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/FontRenderer;drawString(Ljava/lang/String;IIIZ)I"
            ),
            index = 3 // color argument
    )
    private int adjustTextBrightnessShort(int color) {
        return adjustTextBrightness(color);
    }
}
