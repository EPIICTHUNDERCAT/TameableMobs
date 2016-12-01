package com.github.EPIICTHUNDERCAT.TameableMobs.item;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;

import net.minecraft.item.Item;

public class TMItem extends Item{
	
	
	public TMItem(String name) {
		
		this.setRegistryName(name.toLowerCase());
		this.setUnlocalizedName(name.toLowerCase());
	//	this.setCreativeTab(TCatsCreativeTabs.TCats);
		addToItems(this);
	}

	private void addToItems(Item item) {

		TMItems.items.add(item);

	}

}