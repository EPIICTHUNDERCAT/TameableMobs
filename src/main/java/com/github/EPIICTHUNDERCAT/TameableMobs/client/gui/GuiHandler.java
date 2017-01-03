package com.github.epiicthundercat.tameablemobs.client.gui;

import com.github.epiicthundercat.tameablemobs.TameableMobs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	private static ItemStack heldStack;
	
	
	
	public static final int GUI_TAMEABLEMOBS_INFO = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_TAMEABLEMOBS_INFO:
			return new InfoGui();

		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_TAMEABLEMOBS_INFO:
			return new InfoGui();
		default:
			break;
		}
		return null;
	}
	public static void launchGui(int ID, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		playerIn.openGui(TameableMobs.instance, ID, worldIn, x, y, z);
	}
	public static void launchGui(int ID, EntityPlayer playerIn, World worldIn, int x, int y, int z, ItemStack stack) {
		heldStack = stack;
		playerIn.openGui(TameableMobs.instance, ID, worldIn, x, y, z);
	}
}
