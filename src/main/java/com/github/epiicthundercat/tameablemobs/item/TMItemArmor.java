package com.github.epiicthundercat.tameablemobs.item;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.util.TMCreativeTabs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class TMItemArmor extends ItemArmor{
	public static ArmorMaterial polar_bear_fur = EnumHelper.addArmorMaterial("polar_bear_fur", "tameablemobs:polar_bear_fur",
			150, new int[] { 3, 6, 8, 3 }, 30, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 5.0F);
	
	public TMItemArmor(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
		super(material, renderIndex, armorType);
		this.setRegistryName(name.toLowerCase());
		this.setUnlocalizedName(name.toLowerCase());
		this.setCreativeTab(TMCreativeTabs.TMTabs);
		addToItems(this);
	}

	private void addToItems(Item item) {
		TMItems.items.add(item);

	}
	   @Override
	    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
	        //    NaturesGift.proxy.spawnParticleLeaf(world, player.posX, player.posY, player.posZ, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	}
}
