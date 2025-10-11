package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Target multiple GUI classes
@Mixin({
        net.minecraft.src.GuiMainMenu.class,
        net.minecraft.src.GuiIngameMenu.class,
        net.minecraft.src.GuiOptions.class,
        net.minecraft.src.GuiMultiplayer.class,
        net.minecraft.src.GuiChat.class
})
public abstract class _MenuBrightnessMixin {

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void preDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        // Use accessor to get Minecraft instance
        Minecraft mc = ((GuiScreenAccessor) this).getMinecraft();
        if (mc == null || mc.thePlayer == null) return;

        // Get current HUD brightness
        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);

        // Debug log
        System.out.println("[AdaptiveHUD] Menu brightness = " + brightness);

        // Directly set OpenGL color (no push/pop)
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void postDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        // Reset color back to default white to avoid affecting subsequent rendering
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(
            method = "drawScreen",
            at = @At("HEAD")
    )
    private void testInjection(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        System.out.println("[AdaptiveHUD] drawScreen HEAD for " + this.getClass().getSimpleName());
    }


}
