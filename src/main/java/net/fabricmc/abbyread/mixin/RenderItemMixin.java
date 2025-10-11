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
            ),
            remap = false
    )
    private void adaptivehud$applyBrightness(float r, float g, float b, float a) {
        Minecraft mc = Minecraft.getMinecraft();
        float brightness = 1.0F;

        // Only dim items when NO GUI is open (i.e. HUD/hotbar context)
        if (mc != null && mc.thePlayer != null && mc.currentScreen == null) {
            brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        }

        // Apply brightness multiplier
        GL11.glColor4f(r * brightness, g * brightness, b * brightness, a);
    }
}
