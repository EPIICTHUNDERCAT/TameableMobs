package com.github.EPIICTHUNDERCAT.TameableMobs.item;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;
import com.github.EPIICTHUNDERCAT.TameableMobs.util.TMCreativeTabs;

import net.minecraft.item.Item;

public class TMItem extends Item{
	
	
	public TMItem(String name) {
		
		setRegistryName(name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());
		setCreativeTab(TMCreativeTabs.TMTabs);
		addToItems(this);
	}

	private void addToItems(Item item) {

		TMItems.items.add(item);

	}

}