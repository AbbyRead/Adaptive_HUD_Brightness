package net.fabricmc.abbyread.mixin;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiScreen.class)
public interface GuiScreenAccessor {

    @Accessor("mc")
    Minecraft getMinecraft();
}
