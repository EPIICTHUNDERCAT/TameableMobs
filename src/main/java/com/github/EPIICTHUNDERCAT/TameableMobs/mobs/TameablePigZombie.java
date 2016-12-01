package com.github.EPIICTHUNDERCAT.TameableMobs.mobs;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.world.World;

public class TameablePigZombie extends EntityPigZombie implements IEntityOwnable {

	public TameablePigZombie(World worldIn) {
		super(worldIn);

	}

	@Override
	public UUID getOwnerId() {

		return null;
	}

	@Override
	public Entity getOwner() {
		return null;
	}

}
