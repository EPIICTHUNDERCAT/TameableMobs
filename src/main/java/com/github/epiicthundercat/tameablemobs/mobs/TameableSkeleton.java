package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.Calendar;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSnow;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableSkeleton extends EntityAnimal implements IRangedAttackMob, IEntityOwnable {
	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager
			.<Float>createKey(TameableSkeleton.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableSkeleton.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableSkeleton.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableSkeleton.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private static final DataParameter<Integer> SKELETON_VARIANT = EntityDataManager
			.<Integer>createKey(TameableSkeleton.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager
			.<Boolean>createKey(TameableSkeleton.class, DataSerializers.BOOLEAN);
	protected EntityAISit aiSit;
	private final EntityAIAttackRangedBow aiArrowAttack = new EntityAIAttackRangedBow(this, 1.0D, 20, 15.0F);
	private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false) {
		/**
		 * Resets the task
		 */
		public void resetTask() {
			super.resetTask();
			TameableSkeleton.this.setSwingingArms(false);
		
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			TameableSkeleton.this.setSwingingArms(true);
		}
	};

	public TameableSkeleton(World worldIn) {
		super(worldIn);
		setTamed(false);
		setCombatTask();
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIRestrictSun(this));
		tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
		tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
		tasks.addTask(5, new EntityAIWander(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));

		aiSit = new TameableSkeleton.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(8, new TameableSkeleton.EntityAIBeg(this, 8.0F));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableSkeleton.AIFindPlayer(this));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.9D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SKELETON_VARIANT, Integer.valueOf(SkeletonType.NORMAL.getId()));
		dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(BEGGING, Boolean.valueOf(false));

	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.BONE;
	}

	public static void registerFixesSheep(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableSkeleton");
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
		compound.setByte("SkeletonType", (byte) getSkeletonType().getId());
		if (compound.hasKey("SkeletonType", 99)) {
			int i = compound.getByte("SkeletonType");
			setSkeletonType(SkeletonType.getByOrdinal(i));
		}

		setCombatTask();
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
				if (stack.getItem() == Items.BONE) {
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
		} else if (stack != null && stack.getItem() == Items.BONE) {
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
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
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
		} else if (!(otherAnimal instanceof TameableSkeleton)) {
			return false;
		} else {
			TameableSkeleton entityTameableSkeleton = (TameableSkeleton) otherAnimal;
			return !entityTameableSkeleton.isTamed() ? false
					: (entityTameableSkeleton.isSitting() ? false : isInLove() && entityTameableSkeleton.isInLove());
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
		}
	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof TameableSkeleton) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableSkeleton) {
				TameableSkeleton entityChicken = (TameableSkeleton) p_142018_1_;

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
		private final TameableSkeleton theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableSkeleton blaze, float minDistance) {
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
			return thePlayer == null ? false : hasPlayerGotBlazePowderInHand(thePlayer);
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
					if (theBat.isTamed() && itemstack.getItem() == Items.BLAZE_POWDER) {
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
		private final TameableSkeleton theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableSkeleton entityIn) {
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
	public TameableSkeleton createChild(EntityAgeable ageable) {
		TameableSkeleton entityTameableSkeleton = new TameableSkeleton(worldObj);
		UUID uuid = getOwnerId();

		if (uuid != null) {
			entityTameableSkeleton.setOwnerId(uuid);
			entityTameableSkeleton.setTamed(true);
		}

		return entityTameableSkeleton;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableSkeleton thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableSkeleton thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
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
		TameableSkeleton theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableSkeleton theDefendingTameableIn) {
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
		private final TameableSkeleton TameableSkeleton;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableSkeleton p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableSkeleton = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableSkeleton.worldObj.getNearestAttackablePlayer(TameableSkeleton.posX, TameableSkeleton.posY,
					TameableSkeleton.posZ, d0, d0, (Function) null, (@Nullable EntityPlayer player) -> (player != null)
							&& (TameableSkeleton.shouldAttackPlayer(player)));
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
				if (!TameableSkeleton.shouldAttackPlayer(player)) {
					return false;
				}
				TameableSkeleton.faceEntity(player, 10.0F, 10.0F);
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
					if (TameableSkeleton.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableSkeleton) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableSkeleton) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableSkeleton.teleportToEntity(targetEntity))) {
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
		if (super.attackEntityAsMob(entityIn)) {
			if (getSkeletonType() == SkeletonType.WITHER && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
			}

			return true;
		} else {
			return false;
		}
	}

	

	protected SoundEvent getAmbientSound() {
		return getSkeletonType().getAmbientSound();
	}

	protected SoundEvent getHurtSound() {
		return getSkeletonType().getHurtSound();
	}

	protected SoundEvent getDeathSound() {
		return getSkeletonType().getDeathSound();
	}

	protected void playStepSound(BlockPos pos, Block blockIn) {
		SoundEvent soundevent = getSkeletonType().getStepSound();
		playSound(soundevent, 0.15F, 1.0F);
	}

	

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		if (worldObj.isDaytime() && !worldObj.isRemote) {
			float f = getBrightness(1.0F);
			BlockPos blockpos = getRidingEntity() instanceof EntityBoat
					? (new BlockPos(posX, (double) Math.round(posY), posZ)).up()
					: new BlockPos(posX, (double) Math.round(posY), posZ);

			if (f > 0.5F && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && worldObj.canSeeSky(blockpos)) {
				boolean flag = true;
				ItemStack itemstack = getItemStackFromSlot(EntityEquipmentSlot.HEAD);

				if (itemstack != null) {
					if (itemstack.isItemStackDamageable()) {
						itemstack.setItemDamage(itemstack.getItemDamage() + rand.nextInt(2));

						if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
							renderBrokenItemStack(itemstack);
							setItemStackToSlot(EntityEquipmentSlot.HEAD, (ItemStack) null);
						}
					}

					flag = false;
				}

				if (flag) {
					setFire(8);
				}
			}
		}

		if (worldObj.isRemote) {
			updateSize(getSkeletonType());
		}

		super.onLivingUpdate();
	}

	/**
	 * Handles updating while being ridden by an entity
	 */
	public void updateRidden() {
		super.updateRidden();

		if (getRidingEntity() instanceof EntityCreature) {
			EntityCreature entitycreature = (EntityCreature) getRidingEntity();
			renderYawOffset = entitycreature.renderYawOffset;
		}
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if (cause.getSourceOfDamage() instanceof EntityArrow && cause.getEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) cause.getEntity();
			double d0 = entityplayer.posX - posX;
			double d1 = entityplayer.posZ - posZ;

			if (d0 * d0 + d1 * d1 >= 2500.0D) {
				entityplayer.addStat(AchievementList.SNIPE_SKELETON);
			}
		} else if (cause.getEntity() instanceof EntityCreeper && ((EntityCreeper) cause.getEntity()).getPowered()
				&& ((EntityCreeper) cause.getEntity()).isAIEnabled()) {
			((EntityCreeper) cause.getEntity()).incrementDroppedSkulls();
			entityDropItem(new ItemStack(Items.SKULL, 1, getSkeletonType() == SkeletonType.WITHER ? 1 : 0), 0.0F);
		}
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return getSkeletonType().getLootTable();
	}

	/**
	 * Gives armor or weapon for entity based on given DifficultyInstance
	 */
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);

		if (worldObj.provider instanceof WorldProviderHell && getRNG().nextInt(5) > 0) {
			tasks.addTask(4, aiAttackOnCollide);
			setSkeletonType(SkeletonType.WITHER);
			setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		} else {
			Biome biome = worldObj.getBiome(new BlockPos(this));

			if (biome instanceof BiomeSnow && worldObj.canSeeSky(new BlockPos(this)) && rand.nextInt(5) != 0) {
				setSkeletonType(SkeletonType.STRAY);
			}

			tasks.addTask(4, aiArrowAttack);
			setEquipmentBasedOnDifficulty(difficulty);
			setEnchantmentBasedOnDifficulty(difficulty);
		}

		setCanPickUpLoot(rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());

		if (getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null) {
			Calendar calendar = worldObj.getCurrentDate();

			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && rand.nextFloat() < 0.25F) {
				setItemStackToSlot(EntityEquipmentSlot.HEAD,
						new ItemStack(rand.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
				inventoryArmorDropChances[EntityEquipmentSlot.HEAD.getIndex()] = 0.0F;
			}
		}

		return livingdata;
	}

	/**
	 * sets this entity's combat AI.
	 */
	public void setCombatTask() {
		if (worldObj != null && !worldObj.isRemote) {
			tasks.removeTask(aiAttackOnCollide);
			tasks.removeTask(aiArrowAttack);
			ItemStack itemstack = getHeldItemMainhand();

			if (itemstack != null && itemstack.getItem() == Items.BOW) {
				int i = 20;

				if (worldObj.getDifficulty() != EnumDifficulty.HARD) {
					i = 40;
				}

				aiArrowAttack.setAttackCooldown(i);
				tasks.addTask(4, aiArrowAttack);
			} else {
				tasks.addTask(4, aiAttackOnCollide);
			}
		}
	}

	/**
	 * Attack the specified entity using a ranged attack.
	 * 
	 * @param distanceFactor
	 *            How far the target is, normalized and clamped between 0.1 and
	 *            1.0
	 */
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldObj, this);
		double d0 = target.posX - posX;
		double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entitytippedarrow.posY;
		double d2 = target.posZ - posZ;
		double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		entitytippedarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F,
				(float) (14 - worldObj.getDifficulty().getDifficultyId() * 4));
		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
		int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
		DifficultyInstance difficultyinstance = worldObj.getDifficultyForLocation(new BlockPos(this));
		entitytippedarrow.setDamage((double) (distanceFactor * 2.0F) + rand.nextGaussian() * 0.25D
				+ (double) ((float) worldObj.getDifficulty().getDifficultyId() * 0.11F));

		if (i > 0) {
			entitytippedarrow.setDamage(entitytippedarrow.getDamage() + (double) i * 0.5D + 0.5D);
		}

		if (j > 0) {
			entitytippedarrow.setKnockbackStrength(j);
		}

		boolean flag = isBurning() && difficultyinstance.isHard() && rand.nextBoolean()
				|| getSkeletonType() == SkeletonType.WITHER;
		flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0;

		if (flag) {
			entitytippedarrow.setFire(100);
		}

		ItemStack itemstack = getHeldItem(EnumHand.OFF_HAND);

		if (itemstack != null && itemstack.getItem() == Items.TIPPED_ARROW) {
			entitytippedarrow.setPotionEffect(itemstack);
		} else if (getSkeletonType() == SkeletonType.STRAY) {
			entitytippedarrow.addEffect(new PotionEffect(MobEffects.SLOWNESS, 600));
		}

		playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(entitytippedarrow);
	}

	public SkeletonType getSkeletonType() {
		return SkeletonType.getByOrdinal(((Integer) dataManager.get(SKELETON_VARIANT)).intValue());
	}

	public void setSkeletonType(SkeletonType type) {
		dataManager.set(SKELETON_VARIANT, Integer.valueOf(type.getId()));
		isImmuneToFire = type == SkeletonType.WITHER;
		updateSize(type);
	}

	private void updateSize(SkeletonType p_189769_1_) {
		if (p_189769_1_ == SkeletonType.WITHER) {
			setSize(0.7F, 2.4F);
		} else {
			setSize(0.6F, 1.99F);
		}
	}

	public static void registerFixesSkeleton(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "Skeleton");
	}



	public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
		super.setItemStackToSlot(slotIn, stack);

		if (!worldObj.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) {
			setCombatTask();
		}
	}

	public float getEyeHeight() {
		return getSkeletonType() == SkeletonType.WITHER ? 2.1F : 1.74F;
	}

	/**
	 * Returns the Y Offset of this entity.
	 */
	public double getYOffset() {
		return -0.35D;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSwingingArms() {
		return ((Boolean) dataManager.get(SWINGING_ARMS)).booleanValue();
	}

	public  void setSwingingArms(boolean swingingArms) {
		dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
	}

	static class EntityAIAttackRangedBow extends EntityAIBase {
		private final TameableSkeleton entity;
		private final double moveSpeedAmp;
		private int attackCooldown;
		private final float maxAttackDistance;
		private int attackTime = -1;
		private int seeTime;
		private boolean strafingClockwise;
		private boolean strafingBackwards;
		private int strafingTime = -1;

		public EntityAIAttackRangedBow(TameableSkeleton skeleton, double speedAmplifier, int delay, float maxDistance) {
			entity = skeleton;
			moveSpeedAmp = speedAmplifier;
			attackCooldown = delay;
			maxAttackDistance = maxDistance * maxDistance;
			setMutexBits(3);
		}

		public void setAttackCooldown(int p_189428_1_) {
			attackCooldown = p_189428_1_;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return entity.getAttackTarget() == null ? false : isBowInMainhand();
		}

		protected boolean isBowInMainhand() {
			return entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().getItem() == Items.BOW;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return (shouldExecute() || !entity.getNavigator().noPath()) && isBowInMainhand();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			entity.setSwingingArms(true);
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			super.resetTask();
			entity.setSwingingArms(false);
			seeTime = 0;
			attackTime = -1;
			entity.resetActiveHand();
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			EntityLivingBase entitylivingbase = entity.getAttackTarget();

			if (entitylivingbase != null) {
				double d0 = entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY,
						entitylivingbase.posZ);
				boolean flag = entity.getEntitySenses().canSee(entitylivingbase);
				boolean flag1 = seeTime > 0;

				if (flag != flag1) {
					seeTime = 0;
				}

				if (flag) {
					++seeTime;
				} else {
					--seeTime;
				}

				if (d0 <= (double) maxAttackDistance && seeTime >= 20) {
					entity.getNavigator().clearPathEntity();
					++strafingTime;
				} else {
					entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, moveSpeedAmp);
					strafingTime = -1;
				}

				if (strafingTime >= 20) {
					if ((double) entity.getRNG().nextFloat() < 0.3D) {
						strafingClockwise = !strafingClockwise;
					}

					if ((double) entity.getRNG().nextFloat() < 0.3D) {
						strafingBackwards = !strafingBackwards;
					}

					strafingTime = 0;
				}

				if (strafingTime > -1) {
					if (d0 > (double) (maxAttackDistance * 0.75F)) {
						strafingBackwards = false;
					} else if (d0 < (double) (maxAttackDistance * 0.25F)) {
						strafingBackwards = true;
					}

					entity.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
					entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
				} else {
					entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
				}

				if (entity.isHandActive()) {
					if (!flag && seeTime < -60) {
						entity.resetActiveHand();
					} else if (flag) {
						int i = entity.getItemInUseMaxCount();

						if (i >= 20) {
							entity.resetActiveHand();
							entity.attackEntityWithRangedAttack(entitylivingbase, ItemBow.getArrowVelocity(i));
							attackTime = attackCooldown;
						}
					}
				} else if (--attackTime <= 0 && seeTime >= -60) {
					entity.setActiveHand(EnumHand.MAIN_HAND);
				}
			}
		}
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
	        return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && isValidLightLevel() && super.getCanSpawnHere();
	    }
	
	
	 @Override
	    protected void despawnEntity() {
	        if (!isTamed()) {
	            super.despawnEntity();
	        }
	    }
}
