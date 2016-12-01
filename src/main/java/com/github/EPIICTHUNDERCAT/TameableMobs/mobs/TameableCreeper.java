package com.github.EPIICTHUNDERCAT.TameableMobs.mobs;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

public class TameableCreeper extends EntityCreeper implements IEntityOwnable{

	public TameableCreeper(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public UUID getOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

}
