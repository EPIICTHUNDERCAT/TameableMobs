package com.github.EPIICTHUNDERCAT.TameableMobs.init;


import java.util.*;

import com.github.EPIICTHUNDERCAT.TameableMobs.item.TMItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TMItems {
	
	// Items/Materials //
		public static List<Item> items = new ArrayList();
		
		public static Item taming_seed = new TMItem("taming_seed");
		public static Item creeper_healer = new TMItem("creeper_healer");
		public static Item creeper_tamer = new TMItem("creeper_tamer");
		
		
		private static List<Item> getItems() {
			return items;
		}
		
		public static void register(FMLPreInitializationEvent preEvent) {
			for (Item item : getItems()) {
				GameRegistry.register(item);
			}
		}

		public static void registerRender(FMLInitializationEvent event) {
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			for (Item item : getItems()) {
				renderItem.getItemModelMesher().register(item, 0,
						new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
			}
		}

		

}