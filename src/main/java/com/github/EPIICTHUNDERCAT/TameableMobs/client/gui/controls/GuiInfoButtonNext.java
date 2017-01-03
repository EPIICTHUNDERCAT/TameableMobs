package com.github.epiicthundercat.tameablemobs.client.gui.controls;

import org.lwjgl.opengl.GL11;

import com.github.epiicthundercat.tameablemobs.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiInfoButtonNext extends GuiButton{
	/**
	 * True for pointing right (next page), false for pointing left (previous page).
	 */
	private final boolean nextPage;
	private final static int sourceWidth = 12;
	private final static int sourceHeight = 12;

	private static final ResourceLocation buttonImage = new ResourceLocation(Reference.ID, "textures/gui/button.png");

	public GuiInfoButtonNext(int id, int xPos, int yPos, boolean isNextPage){
		super(id, xPos, yPos, sourceWidth, sourceHeight, "");
		this.nextPage = isNextPage;
	}

	public void setDimensions(int width, int height){
		this.width = width;
		this.height = height;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3){
		if (this.visible){
			boolean isMousedOver = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			par1Minecraft.renderEngine.bindTexture(buttonImage);

			if (isMousedOver){
				GL11.glColor4f(0.6f, 0.6f, 0.6f, 1.0f);
			}

			int u = 364;
			int v = 240;
			if (!this.nextPage){
				u += 12;
			}

		}
	}


	
}