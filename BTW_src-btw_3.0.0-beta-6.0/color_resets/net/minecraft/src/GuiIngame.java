package net.minecraft.src;

import java.awt.Color;
import java.util.*;
// +++START EDIT+++
import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.client.texture.CustomUpdatingTexture;
import btw.util.status.BTWStatusCategory;
import btw.util.status.StatusEffect;
import btw.world.util.WorldUtils;
// ---END EDIT---
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiIngame extends Gui
{
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private static final RenderItem itemRenderer = new RenderItem();
    private final Random rand = new Random();
    private final Minecraft mc;

    /** ChatGUI instance that retains all previous chat data */
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;

    /** The string specifying which record music is playing */
    private String recordPlaying = "";

    /** How many ticks the record playing message will be displayed */
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;

    /** Previous frame vignette brightness (slowly changes by 1% each frame) */
    public float prevVignetteBrightness = 1.0F;

    /** Remaining ticks the item highlight should be visible */
    private int remainingHighlightTicks;

    /** The ItemStack that is currently being highlighted */
    private ItemStack highlightingItemStack;

    public GuiIngame(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.persistantChatGUI = new GuiNewChat(par1Minecraft);
    }

    /**
     * Render the ingame overlay with quick icon bar, ...
     */
    public void renderGameOverlay(float par1, boolean par2, int par3, int par4)
    {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int var6 = var5.getScaledWidth();
        int var7 = var5.getScaledHeight();
        FontRenderer var8 = this.mc.fontRenderer;
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled())
        {
            this.renderVignette(this.mc.thePlayer.getBrightness(par1), var6, var7);
        }
        else
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        ItemStack var9 = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && var9 != null && var9.itemID == BTWBlocks.carvedPumpkin.blockID)
        {
            this.renderPumpkinBlur(var6, var7);
        }

// +++START EDIT+++
        // FCMOD: Added (client only)
        renderModSpecificPlayerSightEffects();
        // END FCMOD
// ---END EDIT---

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
        {
            float var10 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * par1;

            if (var10 > 0.0F)
            {
                this.func_130015_b(var10, var6, var7);
            }
        }

        int var11;
        int var12;
        int var13;

        if (!this.mc.playerController.enableEverythingIsScrewedUpMode())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            InventoryPlayer var31 = this.mc.thePlayer.inventory;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(var6 / 2 - 91, var7 - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(var6 / 2 - 91 - 1 + var31.currentItem * 20, var7 - 22 - 1, 0, 22, 24, 22);
            this.mc.getTextureManager().bindTexture(icons);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
            this.drawTexturedModalRect(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
            GL11.glDisable(GL11.GL_BLEND);
            this.mc.mcProfiler.startSection("bossHealth");
            this.renderBossHealth();
            this.mc.mcProfiler.endSection();

            if (this.mc.playerController.shouldDrawHUD())
            {
                this.func_110327_a(var6, var7);
            }

            GL11.glDisable(GL11.GL_BLEND);
            this.mc.mcProfiler.startSection("actionBar");
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            for (var11 = 0; var11 < 9; ++var11)
            {
                var12 = var6 / 2 - 90 + var11 * 20 + 2;
                var13 = var7 - 16 - 3;
                this.renderInventorySlot(var11, var12, var13, par1);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            this.mc.mcProfiler.endSection();
        }

        int var32;

        if (this.mc.thePlayer.getSleepTimer() > 0)
        {
            this.mc.mcProfiler.startSection("sleep");
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            var32 = this.mc.thePlayer.getSleepTimer();
            float var33 = (float)var32 / 100.0F;

            if (var33 > 1.0F)
            {
                var33 = 1.0F - (float)(var32 - 100) / 10.0F;
            }

            var12 = (int)(220.0F * var33) << 24 | 1052704;
            drawRect(0, 0, var6, var7, var12);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            this.mc.mcProfiler.endSection();
        }

        var32 = 16777215;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var11 = var6 / 2 - 91;
        int var14;
        int var15;
        int var16;
        int var17;
        float var34;
        short var35;

        if (this.mc.thePlayer.isRidingHorse())
        {
            this.mc.mcProfiler.startSection("jumpBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var34 = this.mc.thePlayer.getHorseJumpPower();
            var35 = 182;
            var14 = (int)(var34 * (float)(var35 + 1));
            var15 = var7 - 32 + 3;
            this.drawTexturedModalRect(var11, var15, 0, 84, var35, 5);

            if (var14 > 0)
            {
                this.drawTexturedModalRect(var11, var15, 0, 89, var14, 5);
            }

            this.mc.mcProfiler.endSection();
        }
        else if (this.mc.playerController.func_78763_f())
        {
            this.mc.mcProfiler.startSection("expBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var12 = this.mc.thePlayer.xpBarCap();

            if (var12 > 0)
            {
                var35 = 182;
                var14 = (int)(this.mc.thePlayer.experience * (float)(var35 + 1));
                var15 = var7 - 32 + 3;
                this.drawTexturedModalRect(var11, var15, 0, 64, var35, 5);

                if (var14 > 0)
                {
                    this.drawTexturedModalRect(var11, var15, 0, 69, var14, 5);
                }
            }

            this.mc.mcProfiler.endSection();

            if (this.mc.thePlayer.experienceLevel > 0)
            {
            this.mc.mcProfiler.startSection("expLevel");
                boolean var37 = false;
                var14 = var37 ? 16777215 : 8453920;
                String var39 = "" + this.mc.thePlayer.experienceLevel;
                var16 = (var6 - var8.getStringWidth(var39)) / 2;
                var17 = var7 - 31 - 4;
                boolean var18 = false;
                var8.drawString(var39, var16 + 1, var17, 0);
                var8.drawString(var39, var16 - 1, var17, 0);
                var8.drawString(var39, var16, var17 + 1, 0);
                var8.drawString(var39, var16, var17 - 1, 0);
                var8.drawString(var39, var16, var17, var14);
            this.mc.mcProfiler.endSection();
        }
        }

        String var36;

        if (this.mc.gameSettings.heldItemTooltips)
        {
            this.mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                var36 = this.highlightingItemStack.getDisplayName();
                var13 = (var6 - var8.getStringWidth(var36)) / 2;
                var14 = var7 - 59;

                if (!this.mc.playerController.shouldDrawHUD())
                {
                    var14 += 14;
                }

                var15 = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

                if (var15 > 255)
                {
                    var15 = 255;
                }

                if (var15 > 0)
                {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    var8.drawStringWithShadow(var36, var13, var14, 16777215 + (var15 << 24));
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
        }

        if (this.mc.isDemo())
        {
            this.mc.mcProfiler.startSection("demo");
            var36 = "";

            if (this.mc.theWorld.getTotalWorldTime() >= 120500L)
            {
                var36 = I18n.getString("demo.demoExpired");
            }
            else
            {
                var36 = I18n.getStringParams("demo.remainingTime", new Object[] {StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime()))});
            }

            var13 = var8.getStringWidth(var36);
            var8.drawStringWithShadow(var36, var6 - var13 - 10, 5, 16777215);
            this.mc.mcProfiler.endSection();
        }

        int var21;
        int var22;
        int var23;

        if (this.mc.gameSettings.showDebugInfo)
        {
            this.mc.mcProfiler.startSection("debug");
            GL11.glPushMatrix();
            int y = -8;
            var8.drawStringWithShadow("Minecraft 1.6.4 (" + this.mc.debug + ")", 2, y+=10, 16777215);
            
            if (!this.mc.theWorld.getDifficulty().isRestricted()) {
                var8.drawStringWithShadow(this.mc.debugInfoRenders(), 2, y+=10, 16777215);
                var8.drawStringWithShadow(this.mc.getEntityDebug(), 2, y+=10, 16777215);
                var8.drawStringWithShadow(this.mc.debugInfoEntities(), 2, y+=10, 16777215);
            }
            var8.drawStringWithShadow(this.mc.getWorldProviderName(), 2, y+=10, 16777215);
// +++START EDIT+++

            long var38 = Runtime.getRuntime().maxMemory();
            long var41 = Runtime.getRuntime().totalMemory();
            long var43 = Runtime.getRuntime().freeMemory();
            long var45 = var41 - var43;
            String var20 = "Used memory: " + var45 * 100L / var38 + "% (" + var45 / 1024L / 1024L + "MB) of " + var38 / 1024L / 1024L + "MB";
            var21 = 14737632;
            this.drawString(var8, var20, var6 - var8.getStringWidth(var20) - 2, 2, 14737632);
            var20 = "Allocated memory: " + var41 * 100L / var38 + "% (" + var41 / 1024L / 1024L + "MB)";
            this.drawString(var8, var20, var6 - var8.getStringWidth(var20) - 2, 12, 14737632);
            // FCMOD: Added (client only)
            renderModDebugOverlay(y+=10);
            // END FCMOD
// ---END EDIT---
            GL11.glPopMatrix();
            this.mc.mcProfiler.endSection();
        }

        if (this.recordPlayingUpFor > 0)
        {
            this.mc.mcProfiler.startSection("overlayMessage");
            var34 = (float)this.recordPlayingUpFor - par1;
            var13 = (int)(var34 * 255.0F / 20.0F);

            if (var13 > 255)
            {
                var13 = 255;
            }

            if (var13 > 8)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(var6 / 2), (float)(var7 - 68), 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                var14 = 16777215;

                if (this.recordIsPlaying)
                {
                    var14 = Color.HSBtoRGB(var34 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                var8.drawString(this.recordPlaying, -var8.getStringWidth(this.recordPlaying) / 2, -4, var14 + (var13 << 24 & -16777216));
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        ScoreObjective var40 = this.mc.theWorld.getScoreboard().func_96539_a(1);

        if (var40 != null)
        {
            this.func_96136_a(var40, var7, var6, var8);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, (float)(var7 - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GL11.glPopMatrix();
        var40 = this.mc.theWorld.getScoreboard().func_96539_a(0);

        if (this.mc.gameSettings.keyBindPlayerList.pressed && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || var40 != null))
        {
            this.mc.mcProfiler.startSection("playerList");
            NetClientHandler var42 = this.mc.thePlayer.sendQueue;
            List var44 = var42.playerInfoList;
            var15 = var42.currentServerMaxPlayers;
            var16 = var15;

            for (var17 = 1; var16 > 20; var16 = (var15 + var17 - 1) / var17)
            {
                ++var17;
            }

            int var46 = 300 / var17;

            if (var46 > 150)
            {
                var46 = 150;
            }

            int var19 = (var6 - var17 * var46) / 2;
            byte var47 = 10;
            drawRect(var19 - 1, var47 - 1, var19 + var46 * var17, var47 + 9 * var16, Integer.MIN_VALUE);

            for (var21 = 0; var21 < var15; ++var21)
            {
                var22 = var19 + var21 % var17 * var46;
                var23 = var47 + var21 / var17 * 9;
                drawRect(var22, var23, var22 + var46 - 1, var23 + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (var21 < var44.size())
                {
                    GuiPlayerInfo var48 = (GuiPlayerInfo)var44.get(var21);
                    ScorePlayerTeam var49 = this.mc.theWorld.getScoreboard().getPlayersTeam(var48.name);
                    String var50 = ScorePlayerTeam.formatPlayerName(var49, var48.name);
                    var8.drawStringWithShadow(var50, var22, var23, 16777215);

                    if (var40 != null)
                    {
                        int var27 = var22 + var8.getStringWidth(var50) + 5;
                        int var28 = var22 + var46 - 12 - 5;

                        if (var28 - var27 > 5)
                        {
                            Score var29 = var40.getScoreboard().func_96529_a(var48.name, var40);
                            String var30 = EnumChatFormatting.YELLOW + "" + var29.getScorePoints();
                            var8.drawStringWithShadow(var30, var28 - var8.getStringWidth(var30), var23, 16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(icons);
                    byte var51 = 0;
                    boolean var52 = false;
                    byte var53;

                    if (var48.responseTime < 0)
                    {
                        var53 = 5;
                    }
                    else if (var48.responseTime < 150)
                    {
                        var53 = 0;
                    }
                    else if (var48.responseTime < 300)
                    {
                        var53 = 1;
                    }
                    else if (var48.responseTime < 600)
                    {
                        var53 = 2;
                    }
                    else if (var48.responseTime < 1000)
                    {
                        var53 = 3;
                    }
                    else
                    {
                        var53 = 4;
                    }

                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(var22 + var46 - 12, var23, 0 + var51 * 10, 176 + var53 * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    private void func_96136_a(ScoreObjective par1ScoreObjective, int par2, int par3, FontRenderer par4FontRenderer)
    {
        Scoreboard var5 = par1ScoreObjective.getScoreboard();
        Collection var6 = var5.func_96534_i(par1ScoreObjective);

        if (var6.size() <= 15)
        {
            int var7 = par4FontRenderer.getStringWidth(par1ScoreObjective.getDisplayName());
            String var11;

            for (Iterator var8 = var6.iterator(); var8.hasNext(); var7 = Math.max(var7, par4FontRenderer.getStringWidth(var11)))
            {
                Score var9 = (Score)var8.next();
                ScorePlayerTeam var10 = var5.getPlayersTeam(var9.getPlayerName());
                var11 = ScorePlayerTeam.formatPlayerName(var10, var9.getPlayerName()) + ": " + EnumChatFormatting.RED + var9.getScorePoints();
            }

            int var22 = var6.size() * par4FontRenderer.FONT_HEIGHT;
            int var23 = par2 / 2 + var22 / 3;
            byte var24 = 3;
            int var25 = par3 - var7 - var24;
            int var12 = 0;
            Iterator var13 = var6.iterator();

            while (var13.hasNext())
            {
                Score var14 = (Score)var13.next();
                ++var12;
                ScorePlayerTeam var15 = var5.getPlayersTeam(var14.getPlayerName());
                String var16 = ScorePlayerTeam.formatPlayerName(var15, var14.getPlayerName());
                String var17 = EnumChatFormatting.RED + "" + var14.getScorePoints();
                int var19 = var23 - var12 * par4FontRenderer.FONT_HEIGHT;
                int var20 = par3 - var24 + 2;
                drawRect(var25 - 2, var19, var20, var19 + par4FontRenderer.FONT_HEIGHT, 1342177280);
                par4FontRenderer.drawString(var16, var25, var19, 553648127);
                par4FontRenderer.drawString(var17, var20 - par4FontRenderer.getStringWidth(var17), var19, 553648127);

                if (var12 == var6.size())
                {
                    String var21 = par1ScoreObjective.getDisplayName();
                    drawRect(var25 - 2, var19 - par4FontRenderer.FONT_HEIGHT - 1, var20, var19 - 1, 1610612736);
                    drawRect(var25 - 2, var19 - 1, var20, var19, 1342177280);
                    par4FontRenderer.drawString(var21, var25 + var7 / 2 - par4FontRenderer.getStringWidth(var21) / 2, var19 - par4FontRenderer.FONT_HEIGHT, 553648127);
                }
            }
        }
    }

    private void func_110327_a(int par1, int par2)
    {
        boolean var3 = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (this.mc.thePlayer.hurtResistantTime < 10)
        {
            var3 = false;
        }

        int var4 = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int var5 = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
        this.rand.setSeed((long)(this.updateCounter * 312871));
        boolean var6 = false;
        FoodStats var7 = this.mc.thePlayer.getFoodStats();
        int var8 = var7.getFoodLevel();
        int var9 = var7.getPrevFoodLevel();
        AttributeInstance var10 = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int var11 = par1 / 2 - 91;
        int var12 = par1 / 2 + 91;
        int var13 = par2 - 39;
        float var14 = (float)var10.getAttributeValue();
        float var15 = this.mc.thePlayer.getAbsorptionAmount();
        int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F / 10.0F);
        int var17 = Math.max(10 - (var16 - 2), 3);
        int var18 = var13 - (var16 - 1) * var17 - 10;
        float var19 = var15;
        int var20 = this.mc.thePlayer.getTotalArmorValue();
        int var21 = -1;

        if (this.mc.thePlayer.isPotionActive(Potion.regeneration))
        {
            var21 = this.updateCounter % MathHelper.ceiling_float_int(var14 + 5.0F);
        }

        this.mc.mcProfiler.startSection("armor");
        int var22;
        int var23;

        for (var22 = 0; var22 < 10; ++var22)
        {
            if (var20 > 0)
            {
                var23 = var11 + var22 * 8;

                if (var22 * 2 + 1 < var20)
                {
                    this.drawTexturedModalRect(var23, var18, 34, 9, 9, 9);
                }

                if (var22 * 2 + 1 == var20)
                {
                    this.drawTexturedModalRect(var23, var18, 25, 9, 9, 9);
                }

                if (var22 * 2 + 1 > var20)
                {
                    this.drawTexturedModalRect(var23, var18, 16, 9, 9, 9);
                }
            }
        }

        this.mc.mcProfiler.endStartSection("health");
        int var25;
        int var26;
        int var27;

        for (var22 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F) - 1; var22 >= 0; --var22)
        {
            var23 = 16;

            if (this.mc.thePlayer.isPotionActive(Potion.poison))
            {
                var23 += 36;
            }
            else if (this.mc.thePlayer.isPotionActive(Potion.wither))
            {
                var23 += 72;
            }

            byte var24 = 0;

            if (var3)
            {
                var24 = 1;
            }

            var25 = MathHelper.ceiling_float_int((float)(var22 + 1) / 10.0F) - 1;
            var26 = var11 + var22 % 10 * 8;
            var27 = var13 - var25 * var17;

            if (var4 <= 4)
            {
                var27 += this.rand.nextInt(2);
            }

            if (var22 == var21)
            {
                var27 -= 2;
            }

            byte var28 = 0;

            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() || (BTWMod.useHardcoreHearts && this.mc.theWorld.getDifficulty().hasHardcoreSpawn()))
            {
                var28 = 5;
            }

            this.drawTexturedModalRect(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);

            if (var3)
            {
                if (var22 * 2 + 1 < var5)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 54, 9 * var28, 9, 9);
                }

                if (var22 * 2 + 1 == var5)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 63, 9 * var28, 9, 9);
                }
            }

            if (var19 > 0.0F)
            {
                if (var19 == var15 && var15 % 2.0F == 1.0F)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 153, 9 * var28, 9, 9);
                }
                else
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 144, 9 * var28, 9, 9);
                }

                var19 -= 2.0F;
            }
            else
            {
                if (var22 * 2 + 1 < var4)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 36, 9 * var28, 9, 9);
                }

                if (var22 * 2 + 1 == var4)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 45, 9 * var28, 9, 9);
                }
            }
        }

        Entity var34 = this.mc.thePlayer.ridingEntity;
        int var35;

        if (!(var34 instanceof EntityLivingBase))
        {
            this.mc.mcProfiler.endStartSection("food");
            drawFoodOverlay(var12, var13);
        }
        else {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase var37 = (EntityLivingBase)var34;
            var35 = (int)Math.ceil((double)var37.getHealth());
            float var38 = var37.getMaxHealth();
            var26 = (int)(var38 + 0.5F) / 2;

            if (var26 > 30)
            {
                var26 = 30;
            }

            var27 = var13;

            for (int var39 = 0; var26 > 0; var39 += 20)
            {
                int var29 = Math.min(var26, 10);
                var26 -= var29;

                for (int var30 = 0; var30 < var29; ++var30)
                {
                    byte var31 = 52;
                    byte var32 = 0;

                    if (var6)
                    {
                        var32 = 1;
                    }

                    int var33 = var12 - var30 * 8 - 9;
                    this.drawTexturedModalRect(var33, var27, var31 + var32 * 9, 9, 9, 9);

                    if (var30 * 2 + 1 + var39 < var35)
                    {
                        this.drawTexturedModalRect(var33, var27, var31 + 36, 9, 9, 9);
                    }

                    if (var30 * 2 + 1 + var39 == var35)
                    {
                        this.drawTexturedModalRect(var33, var27, var31 + 45, 9, 9, 9);
                    }
                }

                var27 -= 10;
            }
        }

        int iSightlessTextOffset = -8;

        this.mc.mcProfiler.endStartSection("air");

// +++START EDIT+++
        if (this.mc.thePlayer.isInsideOfMaterial(Material.water) || mc.thePlayer.getAir() < 300 )
// ---END EDIT---
        {
            var23 = this.mc.thePlayer.getAir();
            var35 = MathHelper.ceiling_double_int((double)(var23 - 2) * 10.0D / 300.0D);
            var25 = MathHelper.ceiling_double_int((double)var23 * 10.0D / 300.0D) - var35;

            for (var26 = 0; var26 < var35 + var25; ++var26)
            {
                if (var26 < var35)
                {
                    this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 16, 18, 9, 9);
                }
                else
                {
                    this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 25, 18, 9, 9);
                }
            }
        }
// +++START EDIT+++
        // FCMOD: Added (client only)
        drawPenaltyText(var12, var18);
        // END FCMOD
// ---END EDIT---

        this.mc.mcProfiler.endSection();
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private void renderBossHealth()
    {
        if (BossStatus.bossName != null && BossStatus.statusBarLength > 0)
        {
            --BossStatus.statusBarLength;
            FontRenderer var1 = this.mc.fontRenderer;
            ScaledResolution var2 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int var3 = var2.getScaledWidth();
            short var4 = 182;
            int var5 = var3 / 2 - var4 / 2;
            int var6 = (int)(BossStatus.healthScale * (float)(var4 + 1));
            byte var7 = 12;
            this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
            this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);

            if (var6 > 0)
            {
                this.drawTexturedModalRect(var5, var7, 0, 79, var6, 5);
            }

            String var8 = BossStatus.bossName;
            var1.drawStringWithShadow(var8, var3 / 2 - var1.getStringWidth(var8) / 2, var7 - 10, 16777215);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinBlur(int par1, int par2)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator var3 = Tessellator.instance;
        var3.startDrawingQuads();
        var3.addVertexWithUV(0.0D, (double)par2, -90.0D, 0.0D, 1.0D);
        var3.addVertexWithUV((double)par1, (double)par2, -90.0D, 1.0D, 1.0D);
        var3.addVertexWithUV((double)par1, 0.0D, -90.0D, 1.0D, 0.0D);
        var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var3.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the vignette. Args: vignetteBrightness, width, height
     */
    private void renderVignette(float par1, int par2, int par3)
    {
        par1 = 1.0F - par1;

        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        if (par1 > 1.0F)
        {
            par1 = 1.0F;
        }

        this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(par1 - this.prevVignetteBrightness) * 0.01D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
        GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        var4.addVertexWithUV(0.0D, (double)par3, -90.0D, 0.0D, 1.0D);
        var4.addVertexWithUV((double)par2, (double)par3, -90.0D, 1.0D, 1.0D);
        var4.addVertexWithUV((double)par2, 0.0D, -90.0D, 1.0D, 0.0D);
        var4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var4.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Renders the portal overlay. Args: portalStrength, width, height. Somehow got lost in mapping.
     */
    private void renderPortalOverlay_backup(float par1, int par2, int par3)
    {
        func_130015_b(par1, par2, par3);
    }

    private void func_130015_b(float par1, int par2, int par3)
    {
        if (par1 < 1.0F)
        {
            par1 *= par1;
            par1 *= par1;
            par1 = par1 * 0.8F + 0.2F;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, par1);
        Icon var4 = Block.portal.getBlockTextureFromSide(1);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        float var5 = var4.getMinU();
        float var6 = var4.getMinV();
        float var7 = var4.getMaxU();
        float var8 = var4.getMaxV();
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(0.0D, (double)par3, -90.0D, (double)var5, (double)var8);
        var9.addVertexWithUV((double)par2, (double)par3, -90.0D, (double)var7, (double)var8);
        var9.addVertexWithUV((double)par2, 0.0D, -90.0D, (double)var7, (double)var6);
        var9.addVertexWithUV(0.0D, 0.0D, -90.0D, (double)var5, (double)var6);
        var9.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the specified item of the inventory slot at the specified location. Args: slot, x, y, partialTick
     */
    private void renderInventorySlot(int par1, int par2, int par3, float par4)
    {
        ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[par1];

        if (var5 != null)
        {
            float var6 = (float)var5.animationsToGo - par4;

            if (var6 > 0.0F)
            {
                GL11.glPushMatrix();
                float var7 = 1.0F + var6 / 5.0F;
                GL11.glTranslatef((float)(par2 + 8), (float)(par3 + 12), 0.0F);
                GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float)(-(par2 + 8)), (float)(-(par3 + 12)), 0.0F);
            }

// +++START EDIT+++
            // FCMOD: Added (client only)
            if (var5.getItem().getIconFromDamage(0) instanceof CustomUpdatingTexture customUpdateTexture)
            {
                customUpdateTexture.updateActive(CustomUpdatingTexture.DRAW_SLOT);
            }
            
            
            // END FCMOD
	        
// ---END EDIT---
            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);

// +++START EDIT+++
            // FCMOD: Added (client only)
            if (var5.getItem().getIconFromDamage(0) instanceof CustomUpdatingTexture customUpdateTexture)
            {
                customUpdateTexture.updateInert(CustomUpdatingTexture.DRAW_SLOT);
            }
            // END FCMOD
	        
// ---END EDIT---
            if (var6 > 0.0F)
            {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick()
    {
        if (this.recordPlayingUpFor > 0)
        {
            --this.recordPlayingUpFor;
        }

        ++this.updateCounter;

        if (this.mc.thePlayer != null)
        {
            ItemStack var1 = this.mc.thePlayer.inventory.getCurrentItem();

            if (var1 == null)
            {
                this.remainingHighlightTicks = 0;
            }
            else if (this.highlightingItemStack != null && var1.itemID == this.highlightingItemStack.itemID && ItemStack.areItemStackTagsEqual(var1, this.highlightingItemStack) && (var1.isItemStackDamageable() || var1.getItemDamage() == this.highlightingItemStack.getItemDamage()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            }
            else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = var1;
        }
    }

    public void setRecordPlayingMessage(String par1Str)
    {
        this.func_110326_a("Now playing: " + par1Str, true);
    }

    public void func_110326_a(String par1Str, boolean par2)
    {
        this.recordPlaying = par1Str;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = par2;
    }

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    public GuiNewChat getChatGUI()
    {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter()
    {
        return this.updateCounter;
    }

// +++START EDIT+++
    // FCMOD: Added New (client only)
	static final int TRUE_SIGHT_RANGE = 10;
	
    private int foodLevelOnLastGUIUpdate = 0;
    private float fatOnLastGUIUpdate = 0F;
    
    public void renderModDebugOverlay(int y)
    {
    	addChunkBoundaryDisplay(2 + (y+=10));
    	
    	//AddMovementSpeedDisplay( 74 );
    	
    	//AddCurrentBiomeDisplay( 74 );
    	
    	addLoadedChunksOnServerDisplay(y+=10);
    }
    
    private void addChunkBoundaryDisplay(int iYPos)
    {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        int chunkX = MathHelper.floor_double(mc.thePlayer.posX) % 16;
        
        if ( chunkX < 0 )
        {
        	chunkX = 16 + chunkX;
        }
        
        int iDistToChunkBndryX = chunkX;
        
        if ( 15 - chunkX < chunkX )
        {
        	iDistToChunkBndryX = 15 - chunkX;
        }
        
        int chunkZ = MathHelper.floor_double(mc.thePlayer.posZ) % 16;
        
        if ( chunkZ < 0 )
        {
        	chunkZ = 16 + chunkZ;
        }
        
        int iDistToChunkBndryZ = chunkZ;
        
        if ( 15 - chunkZ < chunkZ )
        {
        	iDistToChunkBndryZ = 15 - chunkZ;
        }
        
        int iDistToChunkBndry = iDistToChunkBndryX;
        
        if ( iDistToChunkBndryZ < iDistToChunkBndryX )
        {
        	iDistToChunkBndry = iDistToChunkBndryZ;
        }
        
        drawString(fontRenderer, String.format("Dist To Chnk Bndry: %d", new Object[]
			{
			     Integer.valueOf( iDistToChunkBndry )
			}), 2, iYPos, 0xe0e0e0);
    }
    
    private void addCurrentBiomeDisplay(int iYPos)
    {
        FontRenderer fontRenderer = mc.fontRenderer;        
        EntityPlayer player = mc.thePlayer;
        
        String sBiomeDescriptor;        
        
        BiomeGenBase biomeGen = player.worldObj.getBiomeGenForCoords( MathHelper.floor_double( player.posX ),
        	MathHelper.floor_double( player.posZ ) );        
        
        if ( biomeGen != null )
        {
        	sBiomeDescriptor = biomeGen.getClass().getName(); 
        }
        else
        {
        	sBiomeDescriptor = "unknown";
        }
        
        drawString( fontRenderer, "Biome: " + sBiomeDescriptor, 2, iYPos, 0xe0e0e0 );
        
    }
    
    private void addMovementSpeedDisplay(int iYPos)
    {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        double playerSpeed = Math.sqrt( ( mc.thePlayer.motionX * mc.thePlayer.motionX ) + ( mc.thePlayer.motionZ * mc.thePlayer.motionZ ) );
        
        
        String sPlayerSpeedString = String.format("Player Speed: %.5f", new Object[] {
    		Double.valueOf(playerSpeed) } ); 
        	
        drawString(fontRenderer, sPlayerSpeedString , 2, iYPos, 0xe0e0e0);
                                                                                
        double riddenSpeed = 0D;
        
        if ( mc.thePlayer.ridingEntity != null )
        {
        	riddenSpeed = Math.sqrt( ( mc.thePlayer.ridingEntity.motionX * mc.thePlayer.ridingEntity.motionX ) + ( mc.thePlayer.ridingEntity.motionZ * mc.thePlayer.ridingEntity.motionZ ) );
        }
        
        
        String sRiddenSpeedString = String.format("Ridden Speed: %.5f", new Object[] {
    		Double.valueOf(riddenSpeed) } );
        
        ScaledResolution resolution = new ScaledResolution( mc.gameSettings, 
        	mc.displayWidth, mc.displayHeight );
        
        int iXPos = 12 + fontRenderer.getStringWidth( sPlayerSpeedString );
        
        drawString(fontRenderer, String.format("Ridden Speed: %.5f", new Object[]
			{
        		Double.valueOf(riddenSpeed)
			}), iXPos, iYPos, 0xe0e0e0);
    }
    
    private void addLoadedChunksOnServerDisplay(int iYPos)
    {
        if ( net.minecraft.server.MinecraftServer.getServer() != null )
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            
        	if ( net.minecraft.server.MinecraftServer.getServer().worldServers[0] != null )
        	{
	            IChunkProvider provider = 
	            	net.minecraft.server.MinecraftServer.getServer().worldServers[0].getChunkProvider();
	            
	            drawString( fontrenderer, "Overworld " + provider.makeString(), 2, iYPos+=10, 0xe0e0e0 );
        	}
        	
        	if ( net.minecraft.server.MinecraftServer.getServer().worldServers[1] != null )
        	{
	            IChunkProvider provider = 
	            	net.minecraft.server.MinecraftServer.getServer().worldServers[1].getChunkProvider();
	            
	            drawString( fontrenderer, "Nether " + provider.makeString(), 2, iYPos+=10, 0xe0e0e0 );
        	}
        	
        	if ( net.minecraft.server.MinecraftServer.getServer().worldServers[2] != null )
        	{
	            IChunkProvider provider = 
	            	net.minecraft.server.MinecraftServer.getServer().worldServers[2].getChunkProvider();
	            
	            drawString( fontrenderer, "End " + provider.makeString(), 2, iYPos+=10, 0xe0e0e0 );
        	}
        }
    }
    
    public void renderGameOverlayWithGuiDisabled(float fSmoothCameraPartialTicks, boolean bScreenActive, int iMouseX, int iMouseY)
    {
    	// Renders anything that has a gameplay effect when the player has the GUI turned off
    	
        ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int iScreenWidth = resolution.getScaledWidth();
        int iScreenHeight = resolution.getScaledHeight();
        FontRenderer fontRenderer = this.mc.fontRenderer;
        
        mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled())
        {
            this.renderVignette(this.mc.thePlayer.getBrightness(fSmoothCameraPartialTicks), iScreenWidth, iScreenHeight);
        }
        else
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        ItemStack var9 = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && var9 != null && var9.itemID == BTWBlocks.carvedPumpkin.blockID)
        {
            this.renderPumpkinBlur(iScreenWidth, iScreenHeight);
        }

        renderModSpecificPlayerSightEffects();

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
        {
            float var10 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * fSmoothCameraPartialTicks;

            if (var10 > 0.0F)
            {
                this.renderPortalOverlay_backup(var10, iScreenWidth, iScreenHeight);
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
    
    private void renderModSpecificPlayerSightEffects()
    {
        if ( mc.gameSettings.thirdPersonView == 0 )
        {
		    if ( mc.thePlayer.isWearingEnderSpectacles() )
		    {
		        addTrueSightParticles();
		        
		    	// Decided against this as it may trivialize base measurement too much.
		        //AddSpawnChunksParticles();
		    }
		    else if ( mc.thePlayer.isPotionActive( BTWMod.potionTrueSight ) )
		    {
		        addTrueSightParticles();
		        
		        addSpawnChunksParticles();
		    }
        }
    }
    
    private void addTrueSightParticles()
    {
        // create particles where mobs can spawn
        
        if ( !mc.getIsGamePaused() )
        {
            EntityPlayer player = mc.thePlayer;
            World world = mc.theWorld;
            int iParticleSetting = mc.gameSettings.particleSetting;
            
			int iPlayerI = MathHelper.floor_double( player.posX );
			int iPlayerJ = MathHelper.floor_double( player.posY );
			int iPlayerK = MathHelper.floor_double( player.posZ );
			
			for (int iTempI = iPlayerI - TRUE_SIGHT_RANGE; iTempI <= iPlayerI + TRUE_SIGHT_RANGE; iTempI++ )
			{
				for (int iTempJ = iPlayerJ - TRUE_SIGHT_RANGE; iTempJ <= iPlayerJ + TRUE_SIGHT_RANGE; iTempJ++ )
				{
					for (int iTempK = iPlayerK - TRUE_SIGHT_RANGE; iTempK <= iPlayerK + TRUE_SIGHT_RANGE; iTempK++ )
					{
						if ( WorldUtils.canMobsSpawnHere(world, iTempI, iTempJ, iTempK) )
						{
							double dVerticalOffset = 0D;
							
							Block blockBelow = Block.blocksList[world.getBlockId( 
								iTempI, iTempJ - 1, iTempK )];
							
							if ( blockBelow != null )
							{
								dVerticalOffset = blockBelow.mobSpawnOnVerticalOffset(
									world, iTempI, iTempJ - 1, iTempK);
							}
							
							if ( rand.nextInt( 12 ) <= ( 2 - iParticleSetting ) )
							{
								double particleX = (double)iTempI + rand.nextDouble();
								
								double particleY = (double)iTempJ + dVerticalOffset + 
									rand.nextDouble() * 0.25D;
								
								double particleZ = (double)iTempK + rand.nextDouble();
								
								spawnTrueSightParticle(world, particleX, particleY, particleZ);
							}
						}
					}
				}
			}
        }        
    }
    
    private void spawnTrueSightParticle(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityFX particleEntity = new EntitySpellParticleFX(world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        
        particleEntity.setRBGColorF( 0F, 0F, 0F );
        
        mc.effectRenderer.addEffect( (EntityFX)particleEntity );
    }
    
    private void addSpawnChunksParticles()
    {
        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
        
        if (!mc.getIsGamePaused() && player.getSpawnChunksVisualizationLocationJ() != 0 &&
        	world.provider.dimensionId == 0 ) // is overworld
        {   
        	// Note that world.GetClampedViewDistanceInChunks() is not accurate on the client
        	// and will always be 10, regardless of view-distance parameter on dedicated server
        	int iViewDistanceChunks = world.getActiveChunkRangeInChunks();
        	
        	int iFirstPassRange = ( iViewDistanceChunks + 2 ) * 16; // player has to be within these bounds
        	
            int iPlayerX = MathHelper.floor_double( player.posX );            
            int iDeltaPosX = iPlayerX - player.getSpawnChunksVisualizationLocationI();
            
            if ( iDeltaPosX >= -iFirstPassRange && iDeltaPosX <= iFirstPassRange )
            {
                int iPlayerZ = MathHelper.floor_double( player.posZ );            
                int iDeltaPosZ = iPlayerZ - player.getSpawnChunksVisualizationLocationK();
                
                if ( iDeltaPosZ >= -iFirstPassRange && iDeltaPosZ <= iFirstPassRange )
                {
                    int iParticleSetting = mc.gameSettings.particleSetting;
                    
                	int iNumParticles = 200 - ( iParticleSetting * 100 );
                	
                	for ( int iTempCount = 0; iTempCount < iNumParticles; iTempCount++ )
                	{
						double particleY = player.posY -(double) TRUE_SIGHT_RANGE +
                                           (rand.nextDouble() * (double) TRUE_SIGHT_RANGE * 2D );
						
						if ( particleY > 0D && particleY <= 256D )
						{
							double particleX = player.posX -(double) TRUE_SIGHT_RANGE +
                                               (rand.nextDouble() * (double) TRUE_SIGHT_RANGE * 2D );
						
							double particleZ = player.posZ -(double) TRUE_SIGHT_RANGE +
                                               (rand.nextDouble() * (double) TRUE_SIGHT_RANGE * 2D );
						
					    	int iSpawnChunkX = player.getSpawnChunksVisualizationLocationI() >> 4;
	            			int iSpawnChunkZ = player.getSpawnChunksVisualizationLocationK() >> 4;
	                	
							if ( isPosInSpawnChunkZone(particleX, particleY, particleZ,
                                                       iSpawnChunkX, iSpawnChunkZ, iViewDistanceChunks) )
							{
								if ( isPosInSpawnChunkZone(particleX, particleY, particleZ,
                                                           iSpawnChunkX, iSpawnChunkZ, iViewDistanceChunks - 2) )
								{
									if ( isPosInSpawnBlock(player, particleX, particleY, particleZ) )
									{
										spawnSpawnPointParticle(world, particleX, particleY, particleZ);
									}
									else
									{
										spawnSpawnChunkInnerParticle(world, particleX, particleY, particleZ);
									}
								}
								else
								{
									spawnSpawnChunkOuterParticle(world, particleX, particleY, particleZ);
								}
							}
                		}
                	}
                }
            }
        }
    }    
    
    private void spawnSpawnChunkOuterParticle(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityFX particleEntity = new EntityCritFX(world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        
        particleEntity.setRBGColorF( 0F, 0F, 0.5F );
        particleEntity.setAlphaF(0.5F);
        
        mc.effectRenderer.addEffect( (EntityFX)particleEntity );
    }
    
    private void spawnSpawnChunkInnerParticle(World world, double dXPos, double dYPos, double dZPos)
    {
        EntityFX particleEntity = new EntityCritFX(world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        
        particleEntity.setRBGColorF( 0.5F, 0F, 0.5F );
        particleEntity.setAlphaF(0.25F);
        
        mc.effectRenderer.addEffect( (EntityFX)particleEntity );
    }
    
    private void spawnSpawnPointParticle(World world, double dXPos, double dYPos, double dZPos)
    {
        //EntityFX particleEntity = new EntityAuraFX( world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        //EntityFX particleEntity = new EntityCritFX( world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        //EntityFX particleEntity = new EntityPortalFX( world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        EntityFX particleEntity = new EntityEnchantmentTableParticleFX(world, dXPos, dYPos, dZPos, 0.0D, 0.0D, 0.0D);
        
        particleEntity.setRBGColorF( 0.75F, 0F, 0F );
        particleEntity.setAlphaF(0.5F);
        
        mc.effectRenderer.addEffect( (EntityFX)particleEntity );
    }
    
    public boolean isPosInSpawnChunkZone(double posX, double posY, double posZ,
                                         int iSpawnChunkX, int iSpawnChunkZ, int iChunkRange)
    {
    	int iPosChunkX = MathHelper.floor_double( posX / 16D );
    	int iDeltaX = iPosChunkX - iSpawnChunkX;  
    	
    	if ( iDeltaX >= -iChunkRange && iDeltaX <= iChunkRange )
    	{    	
    		int iPosChunkZ = MathHelper.floor_double( posZ / 16D );
        	int iDeltaZ = iPosChunkZ - iSpawnChunkZ;  
    		
        	if ( iDeltaZ >= -iChunkRange && iDeltaZ <= iChunkRange )
        	{
        		return true;
        	}
    	}
    	
    	return false;
    }
    
    public boolean isPosInSpawnBlock(EntityPlayer player, double posX, double posY, double posZ)
    {
    	int iDeltaX = MathHelper.floor_double( posX ) - player.getSpawnChunksVisualizationLocationI();
    	
    	if ( iDeltaX >= -1 && iDeltaX <= 1 )
    	{
    		int iDeltaZ = MathHelper.floor_double( posZ ) - player.getSpawnChunksVisualizationLocationK();
    		
        	if ( iDeltaZ >= -1 && iDeltaZ <= 1 )
        	{
        		return true;
        	}
    	}
    	
    	return false;
    }
    
    private int foodOverlayShakeCounter = 0;
    
    private void drawFoodOverlay(int iScreenX, int iScreenY)
    {
        FoodStats stats = mc.thePlayer.getFoodStats();
        
        int iHungerPenalty = mc.thePlayer.getStatusForCategory(BTWStatusCategory.HUNGER).map(StatusEffect::getLevel).orElse(0);
        
        int iFoodLevel = stats.getFoodLevel();
        float fSaturationLevel = stats.getSaturationLevel();
        int iSaturationPips = (int)( ( stats.getSaturationLevel() + 0.124F ) * 4F );
        
        int iFullHungerPips = iFoodLevel / 6;
        
        if ( mc.thePlayer.exhaustionAddedSinceLastGuiUpdate)
        {
            foodOverlayShakeCounter = 20;
        	
        	mc.thePlayer.exhaustionAddedSinceLastGuiUpdate = false;
        }
        else if (foodOverlayShakeCounter > 0 )
        {
        	foodOverlayShakeCounter--;
        }
        
        for ( int iTempCount = 0; iTempCount < 10; ++iTempCount )
        {
            int iShankScreenY = iScreenY;
            int iShankTextureOffsetX = 16;
            byte iBackgroundTextureOffsetX = 0;

            if ( mc.thePlayer.isPotionActive( Potion.hunger ) )
            {
                iShankTextureOffsetX += 36;
                iBackgroundTextureOffsetX = 13;
            }
            else if ( iTempCount < iSaturationPips >> 3 )
            {
            	iBackgroundTextureOffsetX = 1;
            }

            if ( iHungerPenalty > 0 && updateCounter % ( iFoodLevel * 5 + 1 ) == 0 )
            {
                iShankScreenY = iScreenY + (this.rand.nextInt(3) - 1);
            }
            else if (foodOverlayShakeCounter > 0 )
            {
            	int iShakeAmount = /*rand.nextInt( 2 ) +*/ 1;
            	
            	if ( rand.nextInt( 2 ) == 0 )
            	{
            		iShakeAmount = -iShakeAmount;
            	}
            	
                iShankScreenY = iScreenY + iShakeAmount;
            }

            int iShankScreenX = iScreenX - iTempCount * 8 - 9;
            
            drawTexturedModalRect( iShankScreenX, iShankScreenY, 16 + iBackgroundTextureOffsetX * 9, 27, 9, 9 );
            
            if ( iTempCount == iSaturationPips >> 3 )
            {
            	if ( !mc.thePlayer.isPotionActive( Potion.hunger ) )
            	{
	            	int iPartialPips = iSaturationPips % 8;
	            	
	            	if ( iPartialPips != 0 )
	            	{
		            	// draw partial pips
		            	
		                drawTexturedModalRect( iShankScreenX + 8 - iPartialPips, iShankScreenY, 25 + 8 - iPartialPips, 27, 1 + iPartialPips, 9 );
	            	}
            	}
            }

            if ( iTempCount < iFullHungerPips )
            {
                drawTexturedModalRect( iShankScreenX, iShankScreenY, iShankTextureOffsetX + 36, 27, 9, 9);
            }
            else if ( iTempCount == iFullHungerPips )
            {
            	int iPartialPips = iFoodLevel % 6;
            	
            	if ( iPartialPips != 0 )
            	{
            		drawTexturedModalRect( iShankScreenX + 7 - iPartialPips, iShankScreenY, iShankTextureOffsetX + 36 + 7 - iPartialPips, 27, 3 + iPartialPips, 9);
            	}
            }
        }        
    }
    
    private void drawPenaltyText(int screenX, int screenY) {
        if (!mc.thePlayer.isDead) {
            ArrayList<StatusEffect> activeStatuses = mc.thePlayer.getAllActiveStatusEffects();
    
            FontRenderer fontRenderer = this.mc.fontRenderer;
		
            for (int i = 0; i < activeStatuses.size(); i++) {
                String status = StringTranslate.getInstance().translateKey(activeStatuses.get(i).getUnlocalizedName());
        
                int stringWidth = fontRenderer.getStringWidth(status);
                int offset = i * 10;
        
                fontRenderer.drawStringWithShadow(status, screenX - stringWidth, screenY - offset, 0XFFFFFF);
            }
        }
    }
        
    static public boolean installationIntegrityTest()
    {
    	return true;
    }
    // END FCMOD
// ---END EDIT---
}
