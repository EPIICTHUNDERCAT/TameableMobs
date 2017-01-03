package com.github.epiicthundercat.tameablemobs.item;

import com.github.epiicthundercat.tameablemobs.client.gui.InfoGui;
import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.util.TMCreativeTabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TMInfoBook extends Item {

	public TMInfoBook(String name) {

		setRegistryName(name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());
		setCreativeTab(TMCreativeTabs.TMTabs);
		addToItems(this);
		
	}
	@Override
	@SideOnly(Side.CLIENT)
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player, EnumHand hand) {
		System.out.println("working");
		if (!player.isSneaking()) {
			Minecraft.getMinecraft().displayGuiScreen(new InfoGui());
			
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);

	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	private void addToItems(Item item) {

		TMItems.items.add(item);

	}
}
