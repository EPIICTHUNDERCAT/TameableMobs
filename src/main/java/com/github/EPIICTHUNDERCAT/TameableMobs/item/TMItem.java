package com.github.epiicthundercat.tameablemobs.item;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.util.TMCreativeTabs;

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