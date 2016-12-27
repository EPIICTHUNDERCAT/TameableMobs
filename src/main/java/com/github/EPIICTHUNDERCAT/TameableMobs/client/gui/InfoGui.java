package com.github.EPIICTHUNDERCAT.TameableMobs.client.gui;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InfoGui extends GuiScreen{
	
	int guiWidth = 256;
    int guiHeight = 256;

    @Override
    public void drawScreen(int x, int y, float ticks) {
        int guiX = (width - guiWidth) / 2;
        int guiY = (height - guiHeight) / 2;
        GL11.glColor4f(1, 1, 1, 1);
        drawDefaultBackground();
        mc.renderEngine.bindTexture(new ResourceLocation(Reference.ID, "textures/gui/gui_tameablemobs_book.png"));
        drawTexturedModalRect(guiX, guiY, 0, 0, guiWidth, guiHeight);

        fontRendererObj.drawString(new TextComponentString("Tameable Mobs Info").getFormattedText(), guiX + 77, guiY + 20, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Taming Guide").getFormattedText(), guiX + 95, guiY + 29, 0x444de5);

        fontRendererObj.drawString(new TextComponentString("Tame / Heal / Breed").getFormattedText(), guiX + 93, guiY + 40, 0x000000);
        
        fontRendererObj.drawString(new TextComponentString("Polar Bear:Fish / Fish / Fish ").getFormattedText(), guiX + 24, guiY + 58, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Bat:").getFormattedText(), guiX + 24, guiY + 72, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Bug / Bug / Bug").getFormattedText(), guiX + 93, guiY + 72, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze: Powder / BlazeRod / Powder").getFormattedText(), guiX + 24, guiY + 86, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Cave Spider: Spider Tamer/Spider Eye").getFormattedText(), guiX + 24, guiY + 100, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Cave Spider:").getFormattedText(), guiX + 24, guiY + 110, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 120, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Chicken:").getFormattedText(), guiX + 24, guiY + 130, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 140, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Cow:").getFormattedText(), guiX + 24, guiY + 150, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 160, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Creeper:").getFormattedText(), guiX + 24, guiY + 170, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 180, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Enderman:").getFormattedText(), guiX + 24, guiY + 190, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 200, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Endermite:").getFormattedText(), guiX + 24, guiY + 210, 0x000000);
        fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 220, 0x000000);
        
        super.drawScreen(x, y, ticks);
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        switch (key) {
            case Keyboard.KEY_E:
                mc.displayGuiScreen(null);
        }
        super.keyTyped(c, key);
    }

    @Override
    protected void setText(String newChatText, boolean shouldOverwrite) {
        super.setText(newChatText, shouldOverwrite);
    }
}
