package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableCow extends EntityAnimal implements IEntityOwnable {

	protected static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(TameableCow.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableCow.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableCow.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableCow.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	protected EntityAISit aiSit;

	public TameableCow(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.4F);
		this.setTamed(false);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		aiSit = new TameableCow.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(2, new TameableCow.AIMeleeAttack(this, 1.0D, false));
		tasks.addTask(6, new EntityAIMate(this, 1.0D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new TameableCow.EntityAIBeg(this, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableCow.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(BEGGING, Boolean.valueOf(false));

	}

	private boolean shouldAttackPlayer(EntityPlayer player) {
		return false;
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
	public boolean canBeLeashedTo(EntityPlayer player) {
		return isTamed() && isOwner(player);
	}

	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.WHEAT;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		if (stack != null && stack.getItem() == Items.BUCKET && !player.capabilities.isCreativeMode
				&& !this.isChild()) {
			player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);

			if (--stack.stackSize == 0) {
				player.setHeldItem(hand, new ItemStack(Items.MILK_BUCKET));
			} else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET))) {
				player.dropItem(new ItemStack(Items.MILK_BUCKET), false);
			}

			return true;
		}
		if (isTamed()) {
			if (stack != null) {
				if (stack.getItem() == TMItems.nutritious_wheat) {
					if (dataManager.get(DATA_HEALTH_ID).floatValue() < 60.0F) {
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
		} else if (stack != null && stack.getItem() == TMItems.taming_wheat) {
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
			}

			if (!worldObj.isRemote) {
				if (rand.nextInt(5) == 0) {
					setTamed(true);
					navigator.clearPathEntity();
					setAttackTarget((EntityLivingBase) null);
					// aiSit.setSitting(true);
					setHealth(60.0F);
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
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		}
		// getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);

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

	public boolean canMateWith(EntityAnimal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return true;
		} else if (!(otherAnimal instanceof TameableCow)) {
			return false;
		} else {
			TameableCow entityTameableCow = (TameableCow) otherAnimal;
			return !entityTameableCow.isTamed() ? false
					: (entityTameableCow.isSitting() ? false : this.isInLove() && entityTameableCow.isInLove());
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

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableCow) {
				TameableCow entityChicken = (TameableCow) p_142018_1_;

				if (entityChicken.isTamed() && entityChicken.getOwner() == p_142018_2_) {
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

	static class EntityAIBeg extends EntityAIBase {
		private final TameableCow theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableCow blaze, float minDistance) {
			theBat = blaze;
			worldObject = blaze.worldObj;
			minPlayerDistance = minDistance;
			setMutexBits(2);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {

			thePlayer = worldObject.getClosestPlayerToEntity(theBat, (double) minPlayerDistance);
			return thePlayer == null ? false : hasPlayerGotBlazePowderInHand(this.thePlayer);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean continueExecuting() {

			return !thePlayer.isEntityAlive() ? false
					: (theBat.getDistanceSqToEntity(thePlayer) > (double) (minPlayerDistance * minPlayerDistance)
							? false : timeoutCounter > 0 && hasPlayerGotBlazePowderInHand(thePlayer));
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {

			theBat.setBegging(true);
			timeoutCounter = 40 + theBat.getRNG().nextInt(40);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {

			theBat.setBegging(false);
			thePlayer = null;
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			theBat.getLookHelper().setLookPosition(thePlayer.posX, thePlayer.posY + (double) thePlayer.getEyeHeight(),
					thePlayer.posZ, 10.0F, (float) theBat.getVerticalFaceSpeed());
			--timeoutCounter;
		}

		/**
		 * Gets if the Player has the BlazePowder in the hand.
		 */
		private boolean hasPlayerGotBlazePowderInHand(EntityPlayer player) {
			for (EnumHand enumhand : EnumHand.values()) {
				ItemStack itemstack = player.getHeldItem(enumhand);

				if (itemstack != null) {
					if (theBat.isTamed() && itemstack.getItem() == Items.WHEAT) {
						return true;
					}

					if (theBat.isBreedingItem(itemstack)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	static class EntityAISit extends EntityAIBase {
		private final TameableCow theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableCow entityIn) {
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

	@Override
	public TameableCow createChild(EntityAgeable ageable) {
		TameableCow entityTameableCow = new TameableCow(this.worldObj);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entityTameableCow.setOwnerId(uuid);
			entityTameableCow.setTamed(true);
		}

		return entityTameableCow;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableCow thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableCow thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
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

	static class EntityAIOwnerHurtByTarget extends EntityAITarget {
		TameableCow theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableCow theDefendingTameableIn) {
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

	static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
		private final TameableCow TameableCow;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableCow p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableCow = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableCow.worldObj.getNearestAttackablePlayer(TameableCow.posX, TameableCow.posY,
					TameableCow.posZ, d0, d0, (Function) null,
					(@Nullable EntityPlayer player) -> (player != null) && (TameableCow.shouldAttackPlayer(player)));
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
				if (!TameableCow.shouldAttackPlayer(player)) {
					return false;
				}
				TameableCow.faceEntity(player, 10.0F, 10.0F);
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
			} else {
				if (targetEntity != null) {
					if (TameableCow.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableCow) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableCow) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableCow.teleportToEntity(targetEntity))) {
						teleportTime = 0;
					}
				}
				super.updateTask();
			}
		}
	}

	protected boolean teleportToEntity(Entity p_70816_1_) {
		Vec3d vec3d = new Vec3d(this.posX - p_70816_1_.posX, this.getEntityBoundingBox().minY
				+ (double) (this.height / 2.0F) - p_70816_1_.posY + (double) p_70816_1_.getEyeHeight(),
				this.posZ - p_70816_1_.posZ);
		vec3d = vec3d.normalize();
		double d0 = 16.0D;
		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.xCoord * 16.0D;
		double d2 = this.posY + (double) (this.rand.nextInt(16) - 8) - vec3d.yCoord * 16.0D;
		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.zCoord * 16.0D;
		return this.teleportTo(d1, d2, d3);
	}

	private boolean teleportTo(double x, double y, double z) {
		net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(
				this, x, y, z, 0);
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
			return false;
		boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (flag) {
			this.worldObj.playSound((EntityPlayer) null, this.prevPosX, this.prevPosY, this.prevPosZ,
					SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
			this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
				(float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	static class AIMeleeAttack extends EntityAIAttackMelee {

		World worldObj;
		protected EntityCreature attacker;
		/**
		 * An amount of decrementing ticks that allows the entity to attack once
		 * the tick reaches 0.
		 */
		protected int attackTick;
		/** The speed with which the mob will approach the target */
		double speedTowardsTarget;
		/**
		 * When true, the mob will continue chasing its target, even if it can't
		 * find a path to them right now.
		 */
		boolean longMemory;
		/** The PathEntity of our entity. */
		Path entityPathEntity;
		private int delayCounter;
		private double targetX;
		private double targetY;
		private double targetZ;
		protected final int attackInterval = 20;
		private int failedPathFindingPenalty = 0;
		private boolean canPenalize = false;

		public AIMeleeAttack(EntityCreature creature, double speedIn, boolean useLongMemory) {
			super(creature, speedIn, useLongMemory);
			this.attacker = creature;
			this.worldObj = creature.worldObj;
			this.speedTowardsTarget = speedIn;
			this.longMemory = useLongMemory;
			this.setMutexBits(3);

		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

			if (entitylivingbase == null) {
				return false;
			} else if (!entitylivingbase.isEntityAlive()) {
				return false;
			} else {
				if (canPenalize) {
					if (--this.delayCounter <= 0) {
						this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
						this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
						return this.entityPathEntity != null;
					} else {
						return true;
					}
				}
				this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
				return this.entityPathEntity != null;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
			return entitylivingbase == null ? false
					: (!entitylivingbase.isEntityAlive() ? false
							: (!this.longMemory ? !this.attacker.getNavigator().noPath()
									: (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase))
											? false
											: !(entitylivingbase instanceof EntityPlayer)
													|| !((EntityPlayer) entitylivingbase).isSpectator()
															&& !((EntityPlayer) entitylivingbase).isCreative())));
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
			this.delayCounter = 0;
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

			if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer) entitylivingbase).isSpectator()
					|| ((EntityPlayer) entitylivingbase).isCreative())) {
				this.attacker.setAttackTarget((EntityLivingBase) null);
			}

			this.attacker.getNavigator().clearPathEntity();
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
			this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY,
					entitylivingbase.posZ);
			--this.delayCounter;

			if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0
					&& (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
							|| entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D
							|| this.attacker.getRNG().nextFloat() < 0.05F)) {
				this.targetX = entitylivingbase.posX;
				this.targetY = entitylivingbase.getEntityBoundingBox().minY;
				this.targetZ = entitylivingbase.posZ;
				this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);

				if (this.canPenalize) {
					this.delayCounter += failedPathFindingPenalty;
					if (this.attacker.getNavigator().getPath() != null) {
						net.minecraft.pathfinding.PathPoint finalPathPoint = this.attacker.getNavigator().getPath()
								.getFinalPathPoint();
						if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.xCoord,
								finalPathPoint.yCoord, finalPathPoint.zCoord) < 1)
							failedPathFindingPenalty = 0;
						else
							failedPathFindingPenalty += 10;
					} else {
						failedPathFindingPenalty += 10;
					}
				}

				if (d0 > 1024.0D) {
					this.delayCounter += 10;
				} else if (d0 > 256.0D) {
					this.delayCounter += 5;
				}

				if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)) {
					this.delayCounter += 15;
				}
			}

			this.attackTick = Math.max(this.attackTick - 1, 0);
			this.checkAndPerformAttack(entitylivingbase, d0);
		}

		protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_) {
			double d0 = this.getAttackReachSqr(p_190102_1_);

			if (p_190102_2_ <= d0 && this.attackTick <= 0) {
				this.attackTick = 20;
				this.attacker.swingArm(EnumHand.MAIN_HAND);
				this.attacker.attackEntityAsMob(p_190102_1_);
			}
		}

		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
		}

	}

	public static void registerFixesTameableCow(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableCow");
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_COW_AMBIENT;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_COW_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_COW_DEATH;
	}

	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_COW;
	}

	public float getEyeHeight() {
		return this.isChild() ? this.height : 1.3F;
	}

}
