package com.github.EPIICTHUNDERCAT.TameableMobs.util;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TMCreativeTabs extends CreativeTabs{
	public TMCreativeTabs(int index, String label) {
		super(index, label);
	}

	public static final TMCreativeTabs TMTabs = new TMCreativeTabs(CreativeTabs.getNextID(), "tmtabs") {
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return TMItems.taming_seed;
		}
	};
	
	@Override
	public Item getTabIconItem() {
		return null;
	}
}
