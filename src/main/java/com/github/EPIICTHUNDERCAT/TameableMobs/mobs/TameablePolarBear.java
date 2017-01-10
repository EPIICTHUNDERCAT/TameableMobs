package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameablePolarBear extends EntityPolarBear implements IEntityOwnable {

	public static final ResourceLocation LOOT_POLARBEAR = new ResourceLocation(Reference.ID, "entities/tameablepolarbear");
	 private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.<Boolean>createKey(TameablePolarBear.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(TameablePolarBear.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameablePolarBear.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameablePolarBear.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameablePolarBear.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	
	private float headRotationCourse;
	private float headRotationCourseOld;

	 private float clientSideStandAnimation0;
	    private float clientSideStandAnimation;
	    private int warningSoundTicks;
	
	
	protected EntityAISit aiSit;

	public TameablePolarBear(World world) {
		super(world);
		this.setTamed(false);
		this.setSize(1.3F, 1.4F);

	}
	
	private boolean shouldAttackPlayer(EntityPlayer player) {
		return false;
	}
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(BEGGING, Boolean.valueOf(false));
		dataManager.register(IS_STANDING, Boolean.valueOf(false));
	}

	@Override
	protected void initEntityAI() {
		aiSit = new TameablePolarBear.EntityAISit(this);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, aiSit);
		tasks.addTask(1, new TameablePolarBear.AIPanic());
		tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(8, new TameablePolarBear.EntityAIBeg(this, 8.0F));
		tasks.addTask(6, new TameablePolarBear.EntityAIMate(this, 1.0D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameablePolarBear.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));
		
		
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT_POLARBEAR;
	}

	@Override
	protected void updateAITasks() {
		dataManager.set(DATA_HEALTH_ID, Float.valueOf(getHealth()));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (getOwnerId() == null) {
			compound.setString("OwnerUUID", "");
		} else {
			compound.setString("OwnerUUID", getOwnerId().toString());
		}

		compound.setBoolean("Sitting", isSitting());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		String s;

		if (compound.hasKey("OwnerUUID", 8)) {
			s = compound.getString("OwnerUUID");
		} else {
			String s1 = compound.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s1);
		}

		if (!s.isEmpty()) {
			try {
				setOwnerId(UUID.fromString(s));
				setTamed(true);
			} catch (Throwable var4) {
				setTamed(false);
			}
		}

		if (aiSit != null) {
			aiSit.setSitting(compound.getBoolean("Sitting"));
		}

		setSitting(compound.getBoolean("Sitting"));
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		if (isTamed()) {
			if (stack != null) {
				if (stack.getItem() == Items.FISH) {
					if (dataManager.get(DATA_HEALTH_ID).floatValue() < 30.0F) {
						if (!player.capabilities.isCreativeMode) {
							--stack.stackSize;
						}

						heal(20.0F);
						return true;
					}
				}
				if (this.isOwner(player) && !this.worldObj.isRemote && !this.isBreedingItem(stack)) {
					this.aiSit.setSitting(!this.isSitting());
					this.isJumping = false;
					this.navigator.clearPathEntity();
					this.setAttackTarget((EntityLivingBase) null);
				}
			} else {
				if (isOwner(player) && !worldObj.isRemote) {
					aiSit.setSitting(!isSitting());
					isJumping = false;
					navigator.clearPathEntity();
					setAttackTarget((EntityLivingBase) null);
				}
			}
		} else if (stack != null && stack.getItem() == Items.FISH) {
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
			}

			if (!worldObj.isRemote) {
				if (rand.nextInt(10) == 0) {
					setTamed(true);
					navigator.clearPathEntity();
					setAttackTarget((EntityLivingBase) null);
					// aiSit.setSitting(true);
					setHealth(30.0F);
					setOwnerId(player.getUniqueID());
					playTameEffect(true);
					worldObj.setEntityState(this, (byte) 7);
				} else {
					playTameEffect(false);
					worldObj.setEntityState(this, (byte) 6);
				}

			}

			return true;
		}

		return super.processInteract(player, hand, stack);
	}
	public boolean isBreedingItem(@Nullable ItemStack stack)
    {
        return stack != null && stack.getItem() == Items.FISH;
    }
	 
	 
	 @SideOnly(Side.CLIENT)
	    public float getStandingAnimationScale(float p_189795_1_)
	    {
	        return (this.clientSideStandAnimation0 + (this.clientSideStandAnimation - this.clientSideStandAnimation0) * p_189795_1_) / 6.0F;
	    }

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return isTamed() && isOwner(player);
	}
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
				(float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.headRotationCourseOld = this.headRotationCourse;

		if (this.isBegging()) {
			this.headRotationCourse += (1.0F - this.headRotationCourse) * 0.4F;
		} else {
			this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;
		}

	}

	public boolean isTamed() {
		return (dataManager.get(TAMED).byteValue() & 4) != 0;
	}

	public void setTamed(boolean tamed) {
		byte b0 = dataManager.get(TAMED).byteValue();

		if (tamed) {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 4)));
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -5)));
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		}

		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);

	}

	public boolean isSitting() {
		return (dataManager.get(TAMED).byteValue() & 1) != 0;
	}

	public void setSitting(boolean sitting) {
		byte b0 = dataManager.get(TAMED).byteValue();

		if (sitting) {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 1)));
		} else {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -2)));
		}
	}
	public boolean isBegging() {
		return ((Boolean) this.dataManager.get(BEGGING)).booleanValue();
	}

	public void setBegging(boolean beg) {
		this.dataManager.set(BEGGING, Boolean.valueOf(beg));
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}
	@Override
	public TameablePolarBear createChild(EntityAgeable ageable) {
		TameablePolarBear entitytameablepolarBear = new TameablePolarBear(this.worldObj);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entitytameablepolarBear.setOwnerId(uuid);
			entitytameablepolarBear.setTamed(true);
		}

		return entitytameablepolarBear;
	}
	@Override
	public boolean canMateWith(EntityAnimal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return true;
		} else if (!(otherAnimal instanceof TameablePolarBear)) {
			return false;
		} else {
			TameablePolarBear entitytameablePolarBear = (TameablePolarBear) otherAnimal;
			return !entitytameablePolarBear.isTamed() ? false
					: (entitytameablePolarBear.isSitting() ? false
							: this.isInLove() && entitytameablePolarBear.isInLove());
		}
	}

	
	@Override
	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional) dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	public void setOwnerId(@Nullable UUID p_184754_1_) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
	}

	@Override
	@Nullable
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = getOwnerId();
			return uuid == null ? null : worldObj.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entityIn) {
		return entityIn == getOwner();
	}

	public EntityAISit getAISit() {
		return aiSit;
	}

	

	protected void playTameEffect(boolean play) {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

		if (!play) {
			enumparticletypes = EnumParticleTypes.SMOKE_LARGE;
		}

		for (int i = 0; i < 7; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			worldObj.spawnParticle(enumparticletypes, posX + rand.nextFloat() * width * 2.0F - width,
					posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2,
					new int[0]);
		}
	}
	
	
	
	
	
	

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 7) {
			playTameEffect(true);
		} else if (id == 6) {
			playTameEffect(false);
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		if (worldObj.getWorldInfo().getDifficulty() == EnumDifficulty.PEACEFUL) {
			return false;
		}
		return true;
	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameablePolarBear) {
				TameablePolarBear entitypolarbear = (TameablePolarBear) p_142018_1_;

				if (entitypolarbear.isTamed() && entitypolarbear.getOwner() == p_142018_2_) {
					return false;
				}
			}

			return p_142018_1_ instanceof EntityPlayer && p_142018_2_ instanceof EntityPlayer
					&& !((EntityPlayer) p_142018_2_).canAttackPlayer((EntityPlayer) p_142018_1_) ? false
							: !(p_142018_1_ instanceof EntityHorse) || !((EntityHorse) p_142018_1_).isTame();
		} else {
			return false;
		}
	}
	
	
	
	

	public boolean isStanding()
    {
        return ((Boolean)this.dataManager.get(IS_STANDING)).booleanValue();
    }

	public void setStanding(boolean p_189794_1_)
    {
        this.dataManager.set(IS_STANDING, Boolean.valueOf(p_189794_1_));
    }
	
	
	
	
	protected void playWarningSound()
    {
        if (this.warningSoundTicks <= 0)
        {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }
	
	class AIMeleeAttack extends EntityAIAttackMelee
    {
        public AIMeleeAttack()
        {
            super(TameablePolarBear.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(EntityLivingBase Entity, double dub)
        {
            double d0 = this.getAttackReachSqr(Entity);

            if (dub <= d0 && this.attackTick <= 0)
            {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(Entity);
                TameablePolarBear.this.setStanding(false);
            }
            else if (dub <= d0 * 2.0D)
            {
                if (this.attackTick <= 0)
                {
                	TameablePolarBear.this.setStanding(false);
                    this.attackTick = 20;
                }

                if (this.attackTick <= 10)
                {
                	TameablePolarBear.this.setStanding(true);
                	TameablePolarBear.this.playWarningSound();
                }
            }
            else
            {
                this.attackTick = 20;
                TameablePolarBear.this.setStanding(false);
            }
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
        	TameablePolarBear.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget)
        {
            return (double)(4.0F + attackTarget.width);
        }
    }
	
	
	
	
	static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
		private final TameablePolarBear tameablepolarbear;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		

		public AIFindPlayer(TameablePolarBear p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			tameablepolarbear = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = tameablepolarbear.worldObj.getNearestAttackablePlayer(tameablepolarbear.posX, tameablepolarbear.posY, tameablepolarbear.posZ, d0, d0, (Function) null, (@Nullable EntityPlayer player) -> (player != null) && (tameablepolarbear.shouldAttackPlayer(player)));
			return player != null;
		}

		@Override
		public void startExecuting() {
			aggroTime = 5;
			teleportTime = 0;
		}

		@Override
		public void resetTask() {
			player = null;
			super.resetTask();
		}

		@Override
		public boolean continueExecuting() {
			if (player != null) {
				if (!tameablepolarbear.shouldAttackPlayer(player)) {
					return false;
				}
				tameablepolarbear.faceEntity(player, 10.0F, 10.0F);
				return true;
			}
			return (targetEntity != null) && (targetEntity.isEntityAlive()) ? true : super.continueExecuting();
		}

		@Override
		public void updateTask() {
			if (player != null) {
				if (--aggroTime <= 0) {
					targetEntity = player;
					player = null;
					super.startExecuting();
				}
			}
			else {
				if (targetEntity != null) {
					if (tameablepolarbear.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(tameablepolarbear) < 16.0D) {
						}
						teleportTime = 0;
					}
					else if ((targetEntity.getDistanceSqToEntity(tameablepolarbear) > 256.0D) && (teleportTime++ >= 30) && (tameablepolarbear.teleportToEntity(targetEntity))) {
						teleportTime = 0;
					}
				}
				super.updateTask();
			}
		}
	}

	
	 protected boolean teleportToEntity(Entity p_70816_1_)
	    {
	        Vec3d vec3d = new Vec3d(this.posX - p_70816_1_.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.posZ - p_70816_1_.posZ);
	        vec3d = vec3d.normalize();
	        double d0 = 16.0D;
	        double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.xCoord * 16.0D;
	        double d2 = this.posY + (double)(this.rand.nextInt(16) - 8) - vec3d.yCoord * 16.0D;
	        double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.zCoord * 16.0D;
	        return this.teleportTo(d1, d2, d3);
	    }
	 private boolean teleportTo(double x, double y, double z)
	    {
	        net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, x, y, z, 0);
	        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
	        boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

	        if (flag)
	        {
	            this.worldObj.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
	            this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
	        }

	        return flag;
	    }
	
	
	static class EntityAISit extends EntityAIBase {
		private final TameablePolarBear theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameablePolarBear entityIn) {
			theEntity = entityIn;
			setMutexBits(5);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (!theEntity.isTamed()) {
				return false;
			} else if (theEntity.isInWater()) {
				return false;
			} else if (!theEntity.onGround) {
				return false;
			} else {
				EntityLivingBase entitylivingbase = theEntity.getOwner();
				return entitylivingbase == null ? true
						: (theEntity.getDistanceSqToEntity(entitylivingbase) < 144.0D
								&& entitylivingbase.getAITarget() != null ? false : isSitting);
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			theEntity.getNavigator().clearPathEntity();
			theEntity.setSitting(true);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			theEntity.setSitting(false);
		}

		/**
		 * Sets the sitting flag.
		 */
		public void setSitting(boolean sitting) {
			isSitting = sitting;
		}
	}

	static class EntityAIBeg extends EntityAIBase {
		private final TameablePolarBear thePolarBear;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameablePolarBear polarbear, float minDistance) {
			thePolarBear = polarbear;
			worldObject = polarbear.worldObj;
			minPlayerDistance = minDistance;
			setMutexBits(2);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {

			thePlayer = worldObject.getClosestPlayerToEntity(thePolarBear, (double) minPlayerDistance);
			return thePlayer == null ? false : hasPlayerGotFishInHand(this.thePlayer);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean continueExecuting() {

			return !thePlayer.isEntityAlive() ? false
					: (thePolarBear.getDistanceSqToEntity(thePlayer) > (double) (minPlayerDistance * minPlayerDistance)
							? false : timeoutCounter > 0 && hasPlayerGotFishInHand(thePlayer));
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {

			thePolarBear.setBegging(true);
			timeoutCounter = 40 + thePolarBear.getRNG().nextInt(40);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {

			thePolarBear.setBegging(false);
			thePlayer = null;
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			thePolarBear.getLookHelper().setLookPosition(thePlayer.posX,
					thePlayer.posY + (double) thePlayer.getEyeHeight(), thePlayer.posZ, 10.0F,
					(float) thePolarBear.getVerticalFaceSpeed());
			--timeoutCounter;
		}

		/**
		 * Gets if the Player has the Bone in the hand.
		 */
		private boolean hasPlayerGotFishInHand(EntityPlayer player) {
			for (EnumHand enumhand : EnumHand.values()) {
				ItemStack itemstack = player.getHeldItem(enumhand);

				if (itemstack != null) {
					if (thePolarBear.isTamed() && itemstack.getItem() == Items.FISH) {
						return true;
					}

					if (thePolarBear.isBreedingItem(itemstack)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	static class EntityAIMate extends EntityAIBase {
		private final TameablePolarBear theAnimal;
		World theWorld;
		private EntityAnimal targetMate;
		/**
		 * Delay preventing a baby from spawning immediately when two mate-able
		 * animals find each other.
		 */
		int spawnBabyDelay;
		/** The speed the creature moves at during mating behavior. */
		double moveSpeed;

		public EntityAIMate(TameablePolarBear polarBear, double speedIn) {
			theAnimal = polarBear;
			theWorld = polarBear.worldObj;
			moveSpeed = speedIn;
			setMutexBits(3);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (!this.theAnimal.isInLove()) {
				return false;
			} else {
				this.targetMate = this.getNearbyMate();
				return this.targetMate != null;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean continueExecuting() {
			return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			this.targetMate = null;
			this.spawnBabyDelay = 0;
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F,
					(float) this.theAnimal.getVerticalFaceSpeed());
			this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
			++this.spawnBabyDelay;

			if (this.spawnBabyDelay >= 60 && this.theAnimal.getDistanceSqToEntity(this.targetMate) < 9.0D) {
				this.spawnBaby();
			}
		}

		/**
		 * Loops through nearby animals and finds another animal of the same
		 * type that can be mated with. Returns the first valid mate found.
		 */

		private EntityAnimal getNearbyMate() {
			List<EntityAnimal> list = this.theWorld.<EntityAnimal>getEntitiesWithinAABB(this.theAnimal.getClass(),
					this.theAnimal.getEntityBoundingBox().expandXyz(8.0D));
			double d0 = Double.MAX_VALUE;
			EntityAnimal entityanimal = null;

			for (EntityAnimal entityanimal1 : list) {
				if (this.theAnimal.canMateWith(entityanimal1)
						&& this.theAnimal.getDistanceSqToEntity(entityanimal1) < d0) {
					entityanimal = entityanimal1;
					d0 = this.theAnimal.getDistanceSqToEntity(entityanimal1);
				}
			}

			return entityanimal;
		}

		/**
		 * Spawns a baby animal of the same type.
		 */

		private void spawnBaby() {
			EntityAgeable entityageable = this.theAnimal.createChild(this.targetMate);

			final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(
					theAnimal, targetMate, entityageable);
			final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
			entityageable = event.getChild();
			if (cancelled) {
				// Reset the "inLove" state for the animals
				this.theAnimal.setGrowingAge(6000);
				this.targetMate.setGrowingAge(6000);
				this.theAnimal.resetInLove();
				this.targetMate.resetInLove();
				return;
			}

			if (entityageable != null) {
				EntityPlayer entityplayer = this.theAnimal.getPlayerInLove();

				if (entityplayer == null && this.targetMate.getPlayerInLove() != null) {
					entityplayer = this.targetMate.getPlayerInLove();
				}

				if (entityplayer != null) {
					entityplayer.addStat(StatList.ANIMALS_BRED);

					if (this.theAnimal instanceof TameablePolarBear) {
						entityplayer.addStat(AchievementList.BREED_COW);
					}
				}

				this.theAnimal.setGrowingAge(6000);
				this.targetMate.setGrowingAge(6000);
				this.theAnimal.resetInLove();
				this.targetMate.resetInLove();
				entityageable.setGrowingAge(-24000);
				entityageable.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F,
						0.0F);
				this.theWorld.spawnEntityInWorld(entityageable);
				Random random = this.theAnimal.getRNG();

				for (int i = 0; i < 7; ++i) {
					double d0 = random.nextGaussian() * 0.02D;
					double d1 = random.nextGaussian() * 0.02D;
					double d2 = random.nextGaussian() * 0.02D;
					double d3 = random.nextDouble() * (double) this.theAnimal.width * 2.0D
							- (double) this.theAnimal.width;
					double d4 = 0.5D + random.nextDouble() * (double) this.theAnimal.height;
					double d5 = random.nextDouble() * (double) this.theAnimal.width * 2.0D
							- (double) this.theAnimal.width;
					this.theWorld.spawnParticle(EnumParticleTypes.HEART, this.theAnimal.posX + d3,
							this.theAnimal.posY + d4, this.theAnimal.posZ + d5, d0, d1, d2, new int[0]);
				}

				if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
					this.theWorld.spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX,
							this.theAnimal.posY, this.theAnimal.posZ, random.nextInt(7) + 1));
				}
			}
		}
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameablePolarBear thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameablePolarBear thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			thePet = thePetIn;
			theWorld = thePetIn.worldObj;
			followSpeed = followSpeedIn;
			petPathfinder = thePetIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = thePet.getOwner();

			if (entitylivingbase == null) {
				return false;
			} else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()) {
				return false;
			} else if (thePet.isSitting()) {
				return false;
			} else if (thePet.getDistanceSqToEntity(entitylivingbase) < minDist * minDist) {
				return false;
			} else {
				theOwner = entitylivingbase;
				return true;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean continueExecuting() {
			return !petPathfinder.noPath() && thePet.getDistanceSqToEntity(theOwner) > maxDist * maxDist
					&& !thePet.isSitting();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
			thePet.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			theOwner = null;
			petPathfinder.clearPathEntity();
			thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(BlockPos pos) {
			IBlockState iblockstate = theWorld.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, thePet.getVerticalFaceSpeed());

			if (!thePet.isSitting()) {
				if (--timeToRecalcPath <= 0) {
					timeToRecalcPath = 10;

					if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
						if (!thePet.getLeashed()) {
							if (thePet.getDistanceSqToEntity(theOwner) >= 144.0D) {
								int i = MathHelper.floor_double(theOwner.posX) - 2;
								int j = MathHelper.floor_double(theOwner.posZ) - 2;
								int k = MathHelper.floor_double(theOwner.getEntityBoundingBox().minY);

								for (int l = 0; l <= 4; ++l) {
									for (int i1 = 0; i1 <= 4; ++i1) {
										if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
												&& theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1))
														.isFullyOpaque()
												&& isEmptyBlock(new BlockPos(i + l, k, j + i1))
												&& isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
											thePet.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F,
													thePet.rotationYaw, thePet.rotationPitch);
											petPathfinder.clearPathEntity();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	class AIPanic extends EntityAIPanic
    {
        public AIPanic()
        {
            super(TameablePolarBear.this, 2.0D);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return !TameablePolarBear.this.isChild() && !TameablePolarBear.this.isBurning() ? false : super.shouldExecute();
        }
    }
	
	
	
	
	
	
	
	

	static class EntityAIOwnerHurtByTarget extends EntityAITarget {
		TameablePolarBear theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameablePolarBear theDefendingTameableIn) {
			super(theDefendingTameableIn, false);
			theDefendingTameable = theDefendingTameableIn;
			setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (!theDefendingTameable.isTamed()) {
				return false;
			} else {
				EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();

				if (entitylivingbase == null) {
					return false;
				} else {
					theOwnerAttacker = entitylivingbase.getAITarget();
					int i = entitylivingbase.getRevengeTimer();
					return i != timestamp && this.isSuitableTarget(theOwnerAttacker, false)
							&& theDefendingTameable.shouldAttackEntity(theOwnerAttacker, entitylivingbase);
				}
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(theOwnerAttacker);
			EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();

			if (entitylivingbase != null) {
				timestamp = entitylivingbase.getRevengeTimer();
			}

			super.startExecuting();
		}
	}

}
