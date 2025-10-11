package net.fabricmc.abbyread.mixin;

import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("isGamePaused")
    boolean getIsGamePaused();
}
