package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableGuardian extends EntityAnimal implements IEntityOwnable {

	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager
			.<Float>createKey(TameablePigZombie.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameablePigZombie.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameablePigZombie.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameablePigZombie.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private static final DataParameter<Byte> STATUS = EntityDataManager.<Byte>createKey(TameableGuardian.class,
			DataSerializers.BYTE);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager
			.<Integer>createKey(TameableGuardian.class, DataSerializers.VARINT);
	private float clientSideTailAnimation;
	private float clientSideTailAnimationO;
	private float clientSideTailAnimationSpeed;
	private float clientSideSpikesAnimation;
	private float clientSideSpikesAnimationO;
	private EntityLivingBase targetedEntity;
	private int clientSideAttackTime;
	private boolean clientSideTouchedGround;
	private EntityAIWander wander;
	protected EntityAISit aiSit;

	public TameableGuardian(World worldIn) {
		super(worldIn);
		setTamed(false);
		this.experienceValue = 10;
		this.setSize(0.85F, 0.85F);
		this.moveHelper = new TameableGuardian.GuardianMoveHelper(this);
		this.clientSideTailAnimation = this.rand.nextFloat();
		this.clientSideTailAnimationO = this.clientSideTailAnimation;
	}

	@Override
	protected void initEntityAI() {
		EntityAIMoveTowardsRestriction entityaimovetowardsrestriction = new EntityAIMoveTowardsRestriction(this, 1.0D);
		this.wander = new EntityAIWander(this, 1.0D, 80);
		this.tasks.addTask(4, new TameableGuardian.AIGuardianAttack(this));
		this.tasks.addTask(5, entityaimovetowardsrestriction);
		this.tasks.addTask(7, this.wander);
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, TameableGuardian.class, 12.0F, 0.01F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.wander.setMutexBits(3);
		entityaimovetowardsrestriction.setMutexBits(3);
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 10, true, false,
				new TameableGuardian.GuardianTargetSelector(this)));

		tasks.addTask(2, new EntityAIMate(this, 1.0D));

		aiSit = new TameableGuardian.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(8, new TameableGuardian.EntityAIBeg(this, 8.0F));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableGuardian.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(STATUS, Byte.valueOf((byte) 0));
		this.dataManager.register(TARGET_ENTITY, Integer.valueOf(0));
		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(BEGGING, Boolean.valueOf(false));

	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.FISH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Elder", this.isElder());
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
		this.setElder(compound.getBoolean("Elder"));
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
				if (isOwner(player) && !worldObj.isRemote && !isBreedingItem(stack)) {
					aiSit.setSitting(!isSitting());
					isJumping = false;
					navigator.clearPathEntity();
					setAttackTarget((EntityLivingBase) null);
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
		return ((Boolean) dataManager.get(BEGGING)).booleanValue();
	}

	public void setBegging(boolean beg) {
		dataManager.set(BEGGING, Boolean.valueOf(beg));
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public boolean canMateWith(EntityAnimal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!isTamed()) {
			return false;
		} else if (!(otherAnimal instanceof TameableGuardian)) {
			return false;
		} else {
			TameableGuardian entityTameableGuardian = (TameableGuardian) otherAnimal;
			return !entityTameableGuardian.isTamed() ? false
					: (entityTameableGuardian.isSitting() ? false : isInLove() && entityTameableGuardian.isInLove());
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
		if (!(p_142018_1_ instanceof TameableGuardian) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableGuardian) {
				TameableGuardian entityGuardian = (TameableGuardian) p_142018_1_;

				if (entityGuardian.isTamed() && entityGuardian.getOwner() == p_142018_2_) {
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
		private final TameableGuardian theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableGuardian blaze, float minDistance) {
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
			return thePlayer == null ? false : hasPlayerGotFishInHand(thePlayer);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean continueExecuting() {

			return !thePlayer.isEntityAlive() ? false
					: (theBat.getDistanceSqToEntity(thePlayer) > (double) (minPlayerDistance * minPlayerDistance)
							? false : timeoutCounter > 0 && hasPlayerGotFishInHand(thePlayer));
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
		 * Gets if the Player has the Fish in the hand.
		 */
		private boolean hasPlayerGotFishInHand(EntityPlayer player) {
			for (EnumHand enumhand : EnumHand.values()) {
				ItemStack itemstack = player.getHeldItem(enumhand);

				if (itemstack != null) {
					if (theBat.isTamed() && itemstack.getItem() == Items.FISH) {
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
		private final TameableGuardian theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableGuardian entityIn) {
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
	public TameableGuardian createChild(EntityAgeable ageable) {
		TameableGuardian entityTameableGuardian = new TameableGuardian(worldObj);
		UUID uuid = getOwnerId();

		if (uuid != null) {
			entityTameableGuardian.setOwnerId(uuid);
			entityTameableGuardian.setTamed(true);
		}

		return entityTameableGuardian;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableGuardian thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableGuardian thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			thePet = thePetIn;
			theWorld = thePetIn.worldObj;
			followSpeed = followSpeedIn;
			petPathfinder = thePetIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			// if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
			// throw new IllegalArgumentException("Unsupported mob type for
			// FollowOwnerGoal");
			// }
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
		TameableGuardian theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableGuardian theDefendingTameableIn) {
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
					return i != timestamp && isSuitableTarget(theOwnerAttacker, false)
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
		private final TameableGuardian TameableGuardian;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableGuardian p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableGuardian = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableGuardian.worldObj.getNearestAttackablePlayer(TameableGuardian.posX, TameableGuardian.posY,
					TameableGuardian.posZ, d0, d0, (Function) null, (@Nullable EntityPlayer player) -> (player != null)
							&& (TameableGuardian.shouldAttackPlayer(player)));
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
				if (!TameableGuardian.shouldAttackPlayer(player)) {
					return false;
				}
				TameableGuardian.faceEntity(player, 10.0F, 10.0F);
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
					if (TameableGuardian.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableGuardian) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableGuardian) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableGuardian.teleportToEntity(targetEntity))) {
						teleportTime = 0;
					}
				}
				super.updateTask();
			}
		}
	}

	protected boolean teleportToEntity(Entity p_70816_1_) {
		Vec3d vec3d = new Vec3d(posX - p_70816_1_.posX, getEntityBoundingBox().minY + (double) (height / 2.0F)
				- p_70816_1_.posY + (double) p_70816_1_.getEyeHeight(), posZ - p_70816_1_.posZ);
		vec3d = vec3d.normalize();
		double d0 = 16.0D;
		double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.xCoord * 16.0D;
		double d2 = posY + (double) (rand.nextInt(16) - 8) - vec3d.yCoord * 16.0D;
		double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.zCoord * 16.0D;
		return teleportTo(d1, d2, d3);
	}

	private boolean teleportTo(double x, double y, double z) {
		net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(
				this, x, y, z, 0);
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
			return false;
		boolean flag = attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (flag) {
			worldObj.playSound((EntityPlayer) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
				(float) ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			applyEnchantments(this, entityIn);
		}

		return flag;
	}

	private boolean shouldAttackPlayer(EntityPlayer player) {

		return false;

	}

	public static void registerFixesTameableGuardian(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableGuardian");
	}

	/**
	 * Returns new PathNavigateGround instance
	 */
	protected PathNavigate getNewNavigator(World worldIn) {
		return new PathNavigateSwimmer(this, worldIn);
	}

	/**
	 * Returns true if given flag is set
	 */
	private boolean isSyncedFlagSet(int flagId) {
		return (((Byte) this.dataManager.get(STATUS)).byteValue() & flagId) != 0;
	}

	/**
	 * Sets a flag state "on/off" on both sides (client/server) by using
	 * DataWatcher
	 */
	private void setSyncedFlag(int flagId, boolean state) {
		byte b0 = ((Byte) this.dataManager.get(STATUS)).byteValue();

		if (state) {
			this.dataManager.set(STATUS, Byte.valueOf((byte) (b0 | flagId)));
		} else {
			this.dataManager.set(STATUS, Byte.valueOf((byte) (b0 & ~flagId)));
		}
	}

	public boolean isMoving() {
		return this.isSyncedFlagSet(2);
	}

	private void setMoving(boolean moving) {
		this.setSyncedFlag(2, moving);
	}

	public int getAttackDuration() {
		return this.isElder() ? 60 : 80;
	}

	public boolean isElder() {
		return this.isSyncedFlagSet(4);
	}

	/**
	 * Sets this Guardian to be an elder or not.
	 */
	public void setElder(boolean elder) {
		this.setSyncedFlag(4, elder);

		if (elder) {
			this.setSize(1.9975F, 1.9975F);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
			this.enablePersistence();

			if (this.wander != null) {
				this.wander.setExecutionChance(400);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void setElder() {
		this.setElder(true);
		this.clientSideSpikesAnimation = 1.0F;
		this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
	}

	private void setTargetedEntity(int entityId) {
		this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
	}

	public boolean hasTargetedEntity() {
		return ((Integer) this.dataManager.get(TARGET_ENTITY)).intValue() != 0;
	}

	public EntityLivingBase getTargetedEntity() {
		if (!this.hasTargetedEntity()) {
			return null;
		} else if (this.worldObj.isRemote) {
			if (this.targetedEntity != null) {
				return this.targetedEntity;
			} else {
				Entity entity = this.worldObj.getEntityByID(((Integer) this.dataManager.get(TARGET_ENTITY)).intValue());

				if (entity instanceof EntityLivingBase) {
					this.targetedEntity = (EntityLivingBase) entity;
					return this.targetedEntity;
				} else {
					return null;
				}
			}
		} else {
			return this.getAttackTarget();
		}
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);

		if (STATUS.equals(key)) {
			if (this.isElder() && this.width < 1.0F) {
				this.setSize(1.9975F, 1.9975F);
			}
		} else if (TARGET_ENTITY.equals(key)) {
			this.clientSideAttackTime = 0;
			this.targetedEntity = null;
		}
	}

	/**
	 * Get number of ticks, at least during which the living entity will be
	 * silent.
	 */
	public int getTalkInterval() {
		return 160;
	}

	protected SoundEvent getAmbientSound() {
		return this.isElder()
				? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT
						: SoundEvents.ENTITY_ELDERGUARDIAN_AMBIENTLAND)
				: (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND);
	}

	protected SoundEvent getHurtSound() {
		return this.isElder()
				? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT
						: SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND)
				: (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND);
	}

	protected SoundEvent getDeathSound() {
		return this.isElder()
				? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH
						: SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND)
				: (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	public float getBlockPathWeight(BlockPos pos) {
		return this.worldObj.getBlockState(pos).getMaterial() == Material.WATER
				? 10.0F + this.worldObj.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		if (this.worldObj.isRemote) {
			this.clientSideTailAnimationO = this.clientSideTailAnimation;

			if (!this.isInWater()) {
				this.clientSideTailAnimationSpeed = 2.0F;

				if (this.motionY > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
					this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GUARDIAN_FLOP,
							this.getSoundCategory(), 1.0F, 1.0F, false);
				}

				this.clientSideTouchedGround = this.motionY < 0.0D
						&& this.worldObj.isBlockNormalCube((new BlockPos(this)).down(), false);
			} else if (this.isMoving()) {
				if (this.clientSideTailAnimationSpeed < 0.5F) {
					this.clientSideTailAnimationSpeed = 4.0F;
				} else {
					this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
				}
			} else {
				this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
			}

			this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
			this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;

			if (!this.isInWater()) {
				this.clientSideSpikesAnimation = this.rand.nextFloat();
			} else if (this.isMoving()) {
				this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
			} else {
				this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
			}

			if (this.isMoving() && this.isInWater()) {
				Vec3d vec3d = this.getLook(0.0F);

				for (int i = 0; i < 2; ++i) {
					this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
							this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width - vec3d.xCoord * 1.5D,
							this.posY + this.rand.nextDouble() * (double) this.height - vec3d.yCoord * 1.5D,
							this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width - vec3d.zCoord * 1.5D,
							0.0D, 0.0D, 0.0D, new int[0]);
				}
			}

			if (this.hasTargetedEntity()) {
				if (this.clientSideAttackTime < this.getAttackDuration()) {
					++this.clientSideAttackTime;
				}

				EntityLivingBase entitylivingbase = this.getTargetedEntity();

				if (entitylivingbase != null) {
					this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
					this.getLookHelper().onUpdateLook();
					double d5 = (double) this.getAttackAnimationScale(0.0F);
					double d0 = entitylivingbase.posX - this.posX;
					double d1 = entitylivingbase.posY + (double) (entitylivingbase.height * 0.5F)
							- (this.posY + (double) this.getEyeHeight());
					double d2 = entitylivingbase.posZ - this.posZ;
					double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					d0 = d0 / d3;
					d1 = d1 / d3;
					d2 = d2 / d3;
					double d4 = this.rand.nextDouble();

					while (d4 < d3) {
						d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
						this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + d0 * d4,
								this.posY + d1 * d4 + (double) this.getEyeHeight(), this.posZ + d2 * d4, 0.0D, 0.0D,
								0.0D, new int[0]);
					}
				}
			}
		}

		if (this.inWater) {
			this.setAir(300);
		} else if (this.onGround) {
			this.motionY += 0.5D;
			this.motionX += (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.motionZ += (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.rotationYaw = this.rand.nextFloat() * 360.0F;
			this.onGround = false;
			this.isAirBorne = true;
		}

		if (this.hasTargetedEntity()) {
			this.rotationYaw = this.rotationYawHead;
		}

		super.onLivingUpdate();
	}

	@SideOnly(Side.CLIENT)
	public float getTailAnimation(float p_175471_1_) {
		return this.clientSideTailAnimationO
				+ (this.clientSideTailAnimation - this.clientSideTailAnimationO) * p_175471_1_;
	}

	@SideOnly(Side.CLIENT)
	public float getSpikesAnimation(float p_175469_1_) {
		return this.clientSideSpikesAnimationO
				+ (this.clientSideSpikesAnimation - this.clientSideSpikesAnimationO) * p_175469_1_;
	}

	public float getAttackAnimationScale(float p_175477_1_) {
		return ((float) this.clientSideAttackTime + p_175477_1_) / (float) this.getAttackDuration();
	}

	protected void updateAITasks() {
		super.updateAITasks();

		if (this.isElder()) {
			int i = 1200;
			int j = 1200;
			int k = 6000;
			int l = 2;

			if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
				Potion potion = MobEffects.MINING_FATIGUE;

				for (EntityPlayerMP entityplayermp : this.worldObj.getPlayers(EntityPlayerMP.class,
						new Predicate<EntityPlayerMP>() {
							public boolean apply(@Nullable EntityPlayerMP p_apply_1_) {
								return TameableGuardian.this.getDistanceSqToEntity(p_apply_1_) < 2500.0D
										&& p_apply_1_.interactionManager.survivalOrAdventure();
							}
						})) {
					if (!entityplayermp.isPotionActive(potion)
							|| entityplayermp.getActivePotionEffect(potion).getAmplifier() < 2
							|| entityplayermp.getActivePotionEffect(potion).getDuration() < 1200) {
						entityplayermp.connection.sendPacket(new SPacketChangeGameState(10, 0.0F));
						entityplayermp.addPotionEffect(new PotionEffect(potion, 6000, 2));
					}
				}
			}

			if (!this.hasHome()) {
				this.setHomePosAndDistance(new BlockPos(this), 16);
			}
		}
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return this.isElder() ? LootTableList.ENTITIES_ELDER_GUARDIAN : LootTableList.ENTITIES_GUARDIAN;
	}

	/**
	 * Checks to make sure the light is not too bright where the mob is spawning
	 */
	protected boolean isValidLightLevel() {
		return true;
	}

	/**
	 * Checks that the entity is not colliding with any blocks / liquids
	 */
	public boolean isNotColliding() {
		return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
				&& this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {

		return (this.rand.nextInt(20) == 0 || !this.worldObj.canBlockSeeSky(new BlockPos(this)))
				&& this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!this.isMoving() && !source.isMagicDamage() && source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) source.getSourceOfDamage();

			if (!source.isExplosion()) {
				entitylivingbase.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
			}
		}

		if (this.wander != null) {
			this.wander.makeUpdate();
		}

		return super.attackEntityFrom(source, amount);
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the
	 * faceEntity method. This is only currently use in wolves.
	 */
	public int getVerticalFaceSpeed() {
		return 180;
	}

	/**
	 * Moves the entity based on the specified heading.
	 */
	public void moveEntityWithHeading(float strafe, float forward) {
		if (this.isServerWorld()) {
			if (this.isInWater()) {
				this.moveRelative(strafe, forward, 0.1F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.8999999761581421D;
				this.motionY *= 0.8999999761581421D;
				this.motionZ *= 0.8999999761581421D;

				if (!this.isMoving() && this.getAttackTarget() == null) {
					this.motionY -= 0.005D;
				}
			} else {
				super.moveEntityWithHeading(strafe, forward);
			}
		} else {
			super.moveEntityWithHeading(strafe, forward);
		}
	}

	static class AIGuardianAttack extends EntityAIBase {
		private final TameableGuardian theEntity;
		private int tickCounter;

		public AIGuardianAttack(TameableGuardian guardian) {
			this.theEntity = guardian;
			this.setMutexBits(3);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
			return entitylivingbase != null && entitylivingbase.isEntityAlive();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return super.continueExecuting() && (this.theEntity.isElder()
					|| this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) > 9.0D);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.tickCounter = -10;
			this.theEntity.getNavigator().clearPathEntity();
			this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
			this.theEntity.isAirBorne = true;
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			this.theEntity.setTargetedEntity(0);
			this.theEntity.setAttackTarget((EntityLivingBase) null);
			this.theEntity.wander.makeUpdate();
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
			this.theEntity.getNavigator().clearPathEntity();
			this.theEntity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);

			if (!this.theEntity.canEntityBeSeen(entitylivingbase)) {
				this.theEntity.setAttackTarget((EntityLivingBase) null);
			} else {
				++this.tickCounter;

				if (this.tickCounter == 0) {
					this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
					this.theEntity.worldObj.setEntityState(this.theEntity, (byte) 21);
				} else if (this.tickCounter >= this.theEntity.getAttackDuration()) {
					float f = 1.0F;

					if (this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
						f += 2.0F;
					}

					if (this.theEntity.isElder()) {
						f += 2.0F;
					}

					entitylivingbase
							.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), f);
					entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.theEntity),
							(float) this.theEntity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
									.getAttributeValue());
					this.theEntity.setAttackTarget((EntityLivingBase) null);
				}

				super.updateTask();
			}
		}
	}

	static class GuardianMoveHelper extends EntityMoveHelper {
		private final TameableGuardian TameableGuardian;

		public GuardianMoveHelper(TameableGuardian guardian) {
			super(guardian);
			this.TameableGuardian = guardian;
		}

		public void onUpdateMoveHelper() {
			if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.TameableGuardian.getNavigator().noPath()) {
				double d0 = this.posX - this.TameableGuardian.posX;
				double d1 = this.posY - this.TameableGuardian.posY;
				double d2 = this.posZ - this.TameableGuardian.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt_double(d3);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				this.TameableGuardian.rotationYaw = this.limitAngle(this.TameableGuardian.rotationYaw, f, 90.0F);
				this.TameableGuardian.renderYawOffset = this.TameableGuardian.rotationYaw;
				float f1 = (float) (this.speed * this.TameableGuardian
						.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				this.TameableGuardian.setAIMoveSpeed(this.TameableGuardian.getAIMoveSpeed()
						+ (f1 - this.TameableGuardian.getAIMoveSpeed()) * 0.125F);
				double d4 = Math
						.sin((double) (this.TameableGuardian.ticksExisted + this.TameableGuardian.getEntityId()) * 0.5D)
						* 0.05D;
				double d5 = Math.cos((double) (this.TameableGuardian.rotationYaw * 0.017453292F));
				double d6 = Math.sin((double) (this.TameableGuardian.rotationYaw * 0.017453292F));
				this.TameableGuardian.motionX += d4 * d5;
				this.TameableGuardian.motionZ += d4 * d6;
				d4 = Math.sin(
						(double) (this.TameableGuardian.ticksExisted + this.TameableGuardian.getEntityId()) * 0.75D)
						* 0.05D;
				this.TameableGuardian.motionY += d4 * (d6 + d5) * 0.25D;
				this.TameableGuardian.motionY += (double) this.TameableGuardian.getAIMoveSpeed() * d1 * 0.1D;
				EntityLookHelper entitylookhelper = this.TameableGuardian.getLookHelper();
				double d7 = this.TameableGuardian.posX + d0 / d3 * 2.0D;
				double d8 = (double) this.TameableGuardian.getEyeHeight() + this.TameableGuardian.posY + d1 / d3;
				double d9 = this.TameableGuardian.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				this.TameableGuardian.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D,
						d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
				this.TameableGuardian.setMoving(true);
			} else {
				this.TameableGuardian.setAIMoveSpeed(0.0F);
				this.TameableGuardian.setMoving(false);
			}
		}
	}

	static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
		private final TameableGuardian parentEntity;

		public GuardianTargetSelector(TameableGuardian guardian) {
			this.parentEntity = guardian;
		}

		public boolean apply(@Nullable EntityLivingBase p_apply_1_) {
			return (p_apply_1_ instanceof EntityPlayer || p_apply_1_ instanceof EntitySquid)
					&& p_apply_1_.getDistanceSqToEntity(this.parentEntity) > 9.0D;
		}
	}

	@Override
	protected void despawnEntity() {
		if (!isTamed()) {
			super.despawnEntity();
		}
	}

}
