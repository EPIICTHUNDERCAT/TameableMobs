package com.github.EPIICTHUNDERCAT.TameableMobs.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.gui.controls.GuiInfoButtonNext;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class InfoGui extends GuiScreen {
	boolean showRightArrow = false;
	boolean showLeftArrow = true;
	public int currentPage = 0;
	private GuiInfoButtonNext modeBtn;
	boolean nextPage;
	GuiInfoButtonNext prevPage;
	int guiWidth = 256;

	int guiHeight = 256;
	public double mouseX = 0;
	public double mouseY = 0;
	public double smoothMouseX = 0;
	public double smoothMouseY = 0;
	public int layer = 0;
	public double cycle = 0;

	boolean renderTooltip = false;
	int tooltipX = 0;
	int tooltipY = 0;
	ItemStack tooltipStack = null;

	public void markTooltipForRender(ItemStack stack, int x, int y) {
		renderTooltip = true;
		tooltipX = x;
		tooltipY = y;
		tooltipStack = stack;
	}

	public void doRenderTooltip() {
		if (renderTooltip) {
			this.renderToolTip(tooltipStack, tooltipX, tooltipY);
			renderTooltip = false;
		}
	}

	@Override
	public void drawScreen(int x, int y, float ticks) {

		int guiX = (width - guiWidth) / 2;
		int guiY = (height - guiHeight) / 2;
		GL11.glColor4f(1, 1, 1, 1);
		drawDefaultBackground();
		mc.renderEngine.bindTexture(new ResourceLocation(Reference.ID, "textures/gui/gui_tameablemobs_book.png"));
		drawTexturedModalRect(guiX, guiY, 0, 0, guiWidth, guiHeight);

		fontRendererObj.drawString(new TextComponentString("Tameable Mobs Info").getFormattedText(), guiX + 77,
				guiY + 20, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Taming Guide").getFormattedText(), guiX + 95, guiY + 29,
				0x444de5);

		fontRendererObj.drawString(new TextComponentString("Tame / Heal / Breed").getFormattedText(), guiX + 93,
				guiY + 40, 0x000000);

		fontRendererObj.drawString(new TextComponentString("Polar Bear:Fish / Fish / Fish ").getFormattedText(),
				guiX + 24, guiY + 58, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Bat:").getFormattedText(), guiX + 24, guiY + 72, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Bug / Bug / Bug").getFormattedText(), guiX + 93, guiY + 72,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze: Powder / BlazeRod / Powder").getFormattedText(),
				guiX + 24, guiY + 86, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Cave Spider: SpiderTamer/SpiderEye/").getFormattedText(),
				guiX + 24, guiY + 100, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Cave Spider:").getFormattedText(), guiX + 24, guiY + 110,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 120,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Chicken:").getFormattedText(), guiX + 24, guiY + 130,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 140,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Cow:").getFormattedText(), guiX + 24, guiY + 150, 0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 160,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Creeper:").getFormattedText(), guiX + 24, guiY + 170,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 180,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Enderman:").getFormattedText(), guiX + 24, guiY + 190,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 200,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Endermite:").getFormattedText(), guiX + 24, guiY + 210,
				0x000000);
		fontRendererObj.drawString(new TextComponentString("Blaze").getFormattedText(), guiX + 24, guiY + 220,
				0x000000);

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
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		float basePosX = ((float) width / 2.0f) - 96;
		float basePosY = ((float) height / 2.0f) - 128;
		if (showLeftArrow) {
			if (mouseX >= basePosX - 16 && mouseX < basePosX + 16 && mouseY >= basePosY + 224
					&& mouseY < basePosY + 240) {
				this.currentPage--;
			}
		}
		if (showRightArrow) {
			if (mouseX >= basePosX + 176 && mouseX < basePosX + 208 && mouseY >= basePosY + 224
					&& mouseY < basePosY + 240) {
				this.currentPage++;
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(modeBtn = new GuiInfoButtonNext(0, 10, 4, nextPage));
	}

	@Override
	protected void setText(String newChatText, boolean shouldOverwrite) {
		super.setText(newChatText, shouldOverwrite);
	}
}
