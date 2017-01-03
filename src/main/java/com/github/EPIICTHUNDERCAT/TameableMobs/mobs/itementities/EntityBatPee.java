package com.github.epiicthundercat.tameablemobs.mobs.itementities;

import com.github.epiicthundercat.tameablemobs.mobs.TameableBat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBatPee extends EntityThrowable {

	public static boolean entity;
	public static boolean player;
	public static boolean block;
	private EntityLivingBase shootingEntity;

	public EntityBatPee(World worldIn) {
		super(worldIn);
	}

	public EntityBatPee(World worldIn, EntityLivingBase shooter) {
		this(worldIn, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D,
				shooter.posZ);
		this.shootingEntity = shooter;

	}

	public EntityBatPee(World worldIn, double x, double y, double z) {
		this(worldIn);
		this.setPosition(x, y, z);
	}

	protected float getGravityVelocity() {
		return -0.01F;

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		World world = this.worldObj;
		int x = this.ticksExisted;
		if ((this.ticksExisted % 2) == 0) {
			world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 1.0, 1.0, 0.3);
		}

	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null && result.entityHit != this.shootingEntity) {
			result.entityHit.attackEntityFrom(DamageSource.causeMobDamage((TameableBat) this.shootingEntity),
					10.0F);
			}
		World world = this.worldObj;
		if (result.typeOfHit.equals(result.typeOfHit.ENTITY)) {
			if (result.entityHit instanceof EntityPlayer && player) {
				EntityLivingBase entity = (EntityLivingBase) result.entityHit;
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(1), 700, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(5), 700, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(6), 700, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(10), 700, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(21), 700, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(22), 700, 4, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(22), 700, 4, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(23), 700, 4, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(26), 700, 4, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(11), 700, 5, false, false));
				}
			}
			if (result.entityHit instanceof EntityLiving && entity) {
				EntityLivingBase entity = (EntityLivingBase) result.entityHit;
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 400, 3, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(20), 400, 4, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(24), 300, 5, false, false));
					entity.addPotionEffect(new PotionEffect(Potion.getPotionById(25), 500, 5, false, false));
					
				}
			}
			if (result.typeOfHit.equals(result.typeOfHit.BLOCK) && block) {
				if (!world.isRemote) {
					this.setDead();
				}
			}

		}
	}
}