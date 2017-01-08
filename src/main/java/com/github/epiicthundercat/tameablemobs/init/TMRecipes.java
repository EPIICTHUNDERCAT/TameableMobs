package com.github.epiicthundercat.tameablemobs.init;

import com.github.epiicthundercat.tameablemobs.mobs.MobDrops;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TMRecipes {

	public static void register(FMLPreInitializationEvent preEvent) {
		//Witch Compound
		GameRegistry.addShapelessRecipe(new ItemStack(TMItems.witch_compound, 1), new ItemStack(Items.REDSTONE),
				new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.STICK), new ItemStack(Items.SUGAR),
				new ItemStack(Items.GLASS_BOTTLE));
		//Creeper Healer
		GameRegistry.addShapelessRecipe(new ItemStack(TMItems.creeper_healer, 1), new ItemStack(Items.GUNPOWDER),
				new ItemStack(TMItems.creeper_tamer), new ItemStack(TMItems.heart));
		//Ender Tamer
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.ender_tamer, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.ENDER_PEARL), 'H', new ItemStack(TMItems.heart), });
		//Taming Carrot
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.taming_carrot, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.CARROT), 'H', new ItemStack(TMItems.heart), });
		//Taming Wheat
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.taming_wheat, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.WHEAT), 'H', new ItemStack(TMItems.heart), });
		//Taming Seed
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.taming_seed, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.BEETROOT_SEEDS), 'E', new ItemStack(Items.MELON_SEEDS), 'E', new ItemStack(Items.PUMPKIN_SEEDS), 'E', new ItemStack(Items.WHEAT_SEEDS),'H', new ItemStack(TMItems.heart), });
		//Creeper Tamer
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.creeper_tamer, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.GUNPOWDER), 'H', new ItemStack(TMItems.heart), });
		//Ink Supplement
		GameRegistry.addShapelessRecipe(new ItemStack(TMItems.ink_supplement), new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.DYE));
		//Spider Tamer
		GameRegistry.addShapedRecipe(new ItemStack(TMItems.spider_tamer, 8), new Object[] { "EEE", "EHE", "EEE", 'E', new ItemStack(Items.STRING), 'H', new ItemStack(TMItems.heart), });
		//Info Book
		GameRegistry.addShapelessRecipe(new ItemStack(TMItems.info_book, 1), new ItemStack(Items.BOOK),
						 new ItemStack(TMItems.taming_seed));
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		MobDrops.addMobDrop(new ItemStack(TMItems.heart), 1, 2, 3);

	}

}
