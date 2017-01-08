package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobDrops {
	
	static List<MobDrop> drops = new ArrayList<MobDrop>();

	// Life Core Drop
	@SubscribeEvent
	public void livingEntityDrops(LivingDropsEvent event) {
		for (MobDrop drop : drops) {
			Random rand = new Random();
			int x = rand.nextInt(100) + 1;
			if (x <= drop.dropChance) {
				ItemStack stack = drop.stackDrop.copy();
				stack.stackSize = rand.nextInt(drop.maxAmount+1-drop.minAmount)+drop.minAmount;
				EntityItem entityItem = event.getEntityLiving().entityDropItem(stack, 0);
			}
		}
	}

	public static void addMobDrop(ItemStack stack, int chance, int min, int max) {
		MobDrop drop = new MobDrop(stack, chance, min, max);
		drops.add(drop);
	}
	
	
}
