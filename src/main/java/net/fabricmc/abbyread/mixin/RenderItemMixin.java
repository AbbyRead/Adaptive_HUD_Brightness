package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderItem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Redirect(
            method = "renderItemIntoGUI",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"
            ), remap = false
    )
    private void redirectGlColor4f(float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        float brightness = 1.0f;
        if (mc != null && mc.thePlayer != null) {
            brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        }
        GL11.glColor4f(red * brightness, green * brightness, blue * brightness, alpha);
    }
}
