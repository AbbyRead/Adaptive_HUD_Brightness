package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderBlocks;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {

    /**
     * Applies AdaptiveHUD brightness dimming to 3D block items rendered in the hotbar.
     * Only affects rendering when no GUI screen is open (HUD context).
     */
    @Redirect(
            method = "renderBlockAsItemVanilla",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"
            ),
            remap = false
    )
    private void adaptivehud$dimBlockItem(float r, float g, float b, float a) {
        Minecraft mc = Minecraft.getMinecraft();

        // Only dim when HUD is visible (no GUI open)
        if (mc != null && mc.thePlayer != null && mc.currentScreen == null) {
            float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
            GL11.glColor4f(r * brightness, g * brightness, b * brightness, a);
            // Debug
            // System.out.println("[AdaptiveHUD] Dimming block item: " + brightness);
        } else {
            // Normal brightness in inventory or menus
            GL11.glColor4f(r, g, b, a);
        }
    }
}
