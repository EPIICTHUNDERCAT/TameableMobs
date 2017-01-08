package com.github.epiicthundercat.tameablemobs.mobs;

import net.minecraft.item.ItemStack;

public class MobDrop {
	public MobDrop(ItemStack stack, int chance, int min, int max) {
		stackDrop = stack;
		dropChance = chance;
		minAmount = min;
		maxAmount = max;
	}

	public ItemStack stackDrop;
	public int dropChance;
	public int minAmount;
	public int maxAmount;
}
