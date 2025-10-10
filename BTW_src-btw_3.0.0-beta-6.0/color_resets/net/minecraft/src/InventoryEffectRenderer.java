package net.minecraft.src;

import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.config.EffectLocation;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class InventoryEffectRenderer extends GuiContainer
{
    /** If potion effects should be rendered.
     * */
    private boolean field_74222_o;

    public InventoryEffectRenderer(Container par1Container) {
        super(par1Container);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();

        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            if (EmiConfig.effectLocation == EffectLocation.RIGHT) {
                this.guiLeft = 160 + (this.width - this.xSize - 400) / 2;
            }
            else if (EmiConfig.effectLocation == EffectLocation.LEFT) {
                this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            }
            
            this.field_74222_o = true;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);

        if (EmiConfig.effectLocation == EffectLocation.TOP) {
            drawCenteredEffects(par1, par2);
        }
        else if (this.field_74222_o) {
            this.displayDebuffEffects(par1, par2);
        }
    }
    
    //EMI feature, lots of magic numbers :)
    private void drawCenteredEffects(int mouseX, int mouseY) {
        EmiDrawContext context = EmiDrawContext.instance();
        context.resetColor();
        Minecraft client = Minecraft.getMinecraft();
        Collection<PotionEffect> effects = client.thePlayer.getActivePotionEffects();
        int size = effects.size();
        if (size == 0) {
            return;
        }
        boolean wide = size == 1;
        int y = this.guiTop - 34;
        if (this instanceof GuiContainerCreative) {
            y -= 28;
        }
        int xOff = 34;
        if (wide) {
            xOff = 122;
        } else if (size > 5) {
            xOff = (this.width - 32) / (size - 1);
        }
        int width = (size - 1) * xOff + (wide ? 120 : 32);
        int x = this.guiLeft + (this.xSize - width) / 2;
        PotionEffect hovered = null;
        int restoreY = this.ySize;
        try {
            this.ySize = y;
            for (PotionEffect inst : effects) {
                int ew = wide ? 120 : 32;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);
                
                drawPotionIcon(x, y, inst);
                if (mouseX >= x && mouseX < x + ew && mouseY >= y && mouseY < y + 32) {
                    hovered = inst;
                }
                x += xOff;
                drawStatusEffectDescriptions(x - width, y, inst, wide);
            }
        } finally {
            this.ySize = restoreY;
        }
        if (size > 1) {
            renderTooltip(mouseX, mouseY, hovered);
        }
    }
    
    /**
     * Displays debuff/potion effects that are currently being applied to the player
     */
    private void displayDebuffEffects(int mouseX, int mouseY) {
        int debuffX = changeEffectSpace(this.guiLeft - 124);
        int debuffY = this.guiTop;
        boolean wide = !EmiConfig.effectLocation.compressed;
        Collection<PotionEffect> activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
        if (!activePotionEffects.isEmpty() && EmiConfig.effectLocation != EffectLocation.HIDDEN) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            int spacing = 33;
            
            if (activePotionEffects.size() > 5 && EmiConfig.effectLocation != EffectLocation.HIDDEN && EmiConfig.effectLocation != EffectLocation.TOP) {
                spacing = 132 / (activePotionEffects.size() - 1);
            }
            
            PotionEffect hovered = null;
            
            for (Iterator<PotionEffect> iterator = activePotionEffects.iterator(); iterator.hasNext(); debuffY += spacing) {
                PotionEffect potionEffect = iterator.next();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(debuffX, debuffY, wide);
                
                int ew = wide ? 120 : 32;
                if (mouseX >= debuffX && mouseX < debuffX + ew && mouseY >= debuffY && mouseY < debuffY + 32) {
                    hovered = potionEffect;
                }
                drawPotionIcon(debuffX, debuffY, potionEffect);
                drawStatusEffectDescriptions(debuffX, debuffY, potionEffect, wide);
            }
            if (!wide) {
                renderTooltip(mouseX, mouseY, hovered);
            }
        }
    }
    
    private void renderTooltip(int mouseX, int mouseY, PotionEffect effect) {
        if (effect != null) {
            String amplifier = getPotionAmplifier(effect);
            TooltipComponent name = TooltipComponent.of(Text.translatable(effect.getEffectName()).append(Text.literal(amplifier)));
            TooltipComponent duration = TooltipComponent.of(Text.literal(Potion.getDurationString(effect)));
            EmiRenderHelper.drawTooltip(this, EmiDrawContext.instance(), List.of(name, duration), mouseX, Math.max(mouseY, 16));
        }
    }
    
    private String getPotionAmplifier(PotionEffect effect) {
        return switch (effect.getAmplifier()) {
            case 1 -> " II";
            case 2 -> " III";
            case 3 -> " IV";
            default -> "";
        };
    }
    
    private void drawStatusEffectBackgrounds(int x, int y, boolean wide) {
        if (wide) {
            this.drawTexturedModalRect(x, y, 0, 166, 120, 32);
        }
        else {
            //split so it renders the edge properly
            this.drawTexturedModalRect(x, y, 0, 166, 28, 32);
            this.drawTexturedModalRect(x + 28, y, 116, 166, 4, 32);
        }
    }
    
    private void drawStatusEffectDescriptions(int x, int y, PotionEffect potionEffect, boolean wide) {
        if (wide) {
            String potionName = I18n.getString(potionEffect.getEffectName()) + getPotionAmplifier(potionEffect);
            
            this.fontRenderer.drawStringWithShadow(potionName, x + 10 + 18, y + 6, 16777215);
            String durationString = Potion.getDurationString(potionEffect);
            this.fontRenderer.drawStringWithShadow(durationString, x + 10 + 18, y + 16, 8355711);
        }
    }
    
    private void drawPotionIcon(int x, int y, PotionEffect potionEffect) {
        Potion potionType = Potion.potionTypes[potionEffect.getPotionID()];
        if (potionType.hasStatusIcon()) {
            int statusIconIndex = potionType.getStatusIconIndex();
            this.drawTexturedModalRect(x + 6, y + 7, 0 + statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
        }
    }
    
    private int changeEffectSpace(int original) {
        if (EmiConfig.effectLocation == EffectLocation.LEFT) {
            return original;
        }
        else if (EmiConfig.effectLocation == EffectLocation.LEFT_COMPRESSED) {
            return this.guiLeft - 2 - 32;
        }
        else if (EmiConfig.effectLocation == EffectLocation.TOP) {
            return this.guiLeft;
        }
        else {
            return this.guiLeft + this.xSize + 2;
        }
    }
}