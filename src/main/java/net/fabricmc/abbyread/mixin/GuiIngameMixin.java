package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.adaptivehud.BrightnessHelper;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DiscouragedShift")
@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {

    @Final
    @Shadow
    private Minecraft mc;

    // --- Utility methods ---
    @Unique
    private float getHudBrightness() {
        if (mc == null || mc.thePlayer == null || ((MinecraftAccessor) mc).getIsGamePaused()) return 1.0F;
        return BrightnessHelper.getCurrentHUDLight(mc.thePlayer);
    }

    @Unique
    private void applyBrightness() {
        float b = getHudBrightness();
        GL11.glColor4f(b, b, b, 1.0F);
    }

    @Unique
    private void resetColor() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // --- Action Bar (health, armor, food) dimming ---
    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiIngame;func_110327_a(II)V", // player stats render method
                    shift = At.Shift.BEFORE
            )
    )
    private void prePlayerStatsRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GuiIngame;func_110327_a(II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void postPlayerStatsRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }


    // --- XP bar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PlayerControllerMP;func_78763_f()Z", shift = At.Shift.AFTER))
    private void preXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endSection()V", shift = At.Shift.BEFORE))
    private void postXpRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

    // --- Hotbar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;drawTexturedModalRect(IIIIII)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void preHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderHelper;enableGUIStandardItemLighting()V", shift = At.Shift.BEFORE))
    private void postHotbarRender(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        resetColor();
    }

    // --- Inventory slot + item damage bar dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderInventorySlot(IIIF)V", shift = At.Shift.BEFORE))
    private void preInventorySlotRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderInventorySlot(IIIF)V", shift = At.Shift.AFTER))
    private void postInventorySlotRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

    // --- Chat display dimming ---
    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiNewChat;drawChat(I)V", shift = At.Shift.BEFORE))
    private void preChatRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        applyBrightness();
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiNewChat;drawChat(I)V", shift = At.Shift.AFTER))
    private void postChatRender(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        resetColor();
    }

}
