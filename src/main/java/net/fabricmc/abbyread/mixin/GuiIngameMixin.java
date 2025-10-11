package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DiscouragedShift")
@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {

    @Final
    @Shadow
    private Minecraft mc;

    // --- Main HUD rendering ---
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/src/InventoryPlayer;currentItem:I",
                    shift = At.Shift.BEFORE
            )
    )
    private void preHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null || ((MinecraftAccessor) mc).getIsGamePaused()) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void postHudRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // --- XP bar ---
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/PlayerControllerMP;func_78763_f()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void preXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null || ((MinecraftAccessor) mc).getIsGamePaused()) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endSection()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void postXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // --- Hotbar ---
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiIngame;drawTexturedModalRect(IIIIII)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void preHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null || ((MinecraftAccessor) mc).getIsGamePaused()) return;

        float brightness = BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderHelper;enableGUIStandardItemLighting()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void postHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
