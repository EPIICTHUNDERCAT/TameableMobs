package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class TameableCaveSpider extends TameableSpider implements IMob {
	public TameableCaveSpider(World worldIn) {
		super(worldIn);
		setTamed(false);
		this.setSize(0.7F, 0.5F);
	}

	@Override
	protected void initEntityAI() {

		tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		tasks.addTask(4, new TameableCaveSpider.AISpiderAttack(this));
		tasks.addTask(5, new EntityAIWander(this, 0.8D));
		
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.STRING, false));
		
		aiSit = new TameableCaveSpider.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));

		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new TameableCaveSpider.EntityAIBeg(this, 8.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new TameableCaveSpider.AISpiderTarget(this, EntityPlayer.class));
		targetTasks.addTask(2, new TameableCaveSpider.AISpiderTarget(this, EntityIronGolem.class));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(4, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(5, new TameableCaveSpider.AIFindPlayer(this));
		targetTasks.addTask(6, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	public static void registerFixesTameableCaveSpider(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableCaveSpider");
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (super.attackEntityAsMob(entityIn)) {
			if (entityIn instanceof EntityLivingBase) {
				int i = 0;

				if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
					i = 7;
				} else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
					i = 15;
				}

				if (i > 0) {
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, i * 20, 0));
				}
			}

			return true;
		} else {
			boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
					(float) ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

			if (flag) {
				applyEnchantments(this, entityIn);
			}

			return flag;
		}
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		return livingdata;
	}

	@Override
	public float getEyeHeight() {
		return 0.45F;
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_CAVE_SPIDER;
	}
	@Override
	public TameableCaveSpider createChild(EntityAgeable ageable) {
		TameableCaveSpider entityTameableSpider = new TameableCaveSpider(worldObj);
		UUID uuid = getOwnerId();

		if (uuid != null) {
			entityTameableSpider.setOwnerId(uuid);
			entityTameableSpider.setTamed(true);
		}

		return entityTameableSpider;
	}
	 protected boolean isValidLightLevel()
	    {
	        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

	        if (this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32))
	        {
	            return false;
	        }
	        else
	        {
	            int i = this.worldObj.getLightFromNeighbors(blockpos);

	            if (this.worldObj.isThundering())
	            {
	                int j = this.worldObj.getSkylightSubtracted();
	                this.worldObj.setSkylightSubtracted(10);
	                i = this.worldObj.getLightFromNeighbors(blockpos);
	                this.worldObj.setSkylightSubtracted(j);
	            }

	            return i <= this.rand.nextInt(8);
	        }
	    }

	    /**
	     * Checks if the entity's current position is a valid location to spawn this entity.
	     */
	 @Override
	    public boolean getCanSpawnHere()
	    {
	        return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && !isValidLightLevel() && super.getCanSpawnHere();
	    }
	
	
	 @Override
	    protected void despawnEntity() {
	        if (!isTamed()) {
	            super.despawnEntity();
	        }
	    }
}
