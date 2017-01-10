package com.github.epiicthundercat.tameablemobs.init;


import java.util.ArrayList;
import java.util.List;

import com.github.epiicthundercat.tameablemobs.item.TMInfoBook;
import com.github.epiicthundercat.tameablemobs.item.TMItem;
import com.github.epiicthundercat.tameablemobs.item.TMItemArmor;
import com.github.epiicthundercat.tameablemobs.item.TMProvisions;
import com.github.epiicthundercat.tameablemobs.item.TMProvisions.PackType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
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
		public static Item taming_wheat = new TMItem("taming_wheat");
		public static Item taming_carrot = new TMItem("taming_carrot");
		public static Item ender_tamer = new TMItem("ender_tamer");
		public static Item spider_tamer = new TMItem("spider_tamer");
		public static Item witch_compound = new TMItem("witch_compound");
		public static Item witch_projectile = new TMItem("witch_projectile");
		public static Item bat_pee = new TMItem("bat_pee");
		public static Item info_book = new TMInfoBook("info_book");
		public static Item bug = new TMItem("bug");
		public static Item ink_supplement = new TMItem("ink_supplement");
		public static Item heart = new TMProvisions("heart", 16, PackType.SMALL);
		public static Item polar_bear_fur = new TMItem("polar_bear_fur");
		public static Item polar_bear_fur_chestplate = new TMItemArmor("polar_bear_fur_chestplate",TMItemArmor.polar_bear_fur, 7,
				EntityEquipmentSlot.CHEST);
		public static Item polar_bear_fur_helmet = new TMItemArmor("polar_bear_fur_helmet", TMItemArmor.polar_bear_fur, 7,
				EntityEquipmentSlot.HEAD);
		public static Item polar_bear_fur_leggings = new TMItemArmor("polar_bear_fur_leggings",TMItemArmor.polar_bear_fur, 7,
				EntityEquipmentSlot.LEGS);
		public static Item polar_bear_fur_boots = new TMItemArmor("polar_bear_fur_boots",TMItemArmor.polar_bear_fur, 7,
				EntityEquipmentSlot.FEET);
		public static Item nutritious_wheat = new TMItem("nutritious_wheat");
		public static Item nutritious_carrot = new TMItem("nutritious_carrot");
		public static Item nutritious_seeds = new TMItem("nutritious_seeds");
		//public static Item nutritious_wheat = new TMItem("nutritious_wheat");
		public static Item nutrients = new TMItem("nutrients");
		public static Item nullified_ender_pearl = new TMItem("nullified_ender_pearl");
		public static Item brain = new TMItem("brain");

		
		
		
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