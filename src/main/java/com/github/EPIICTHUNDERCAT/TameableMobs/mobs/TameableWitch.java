package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.mobs.itementities.EntityWitchProjectile;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
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
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableWitch extends EntityAnimal implements IEntityOwnable, IRangedAttackMob {

	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(TameableWitch.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableWitch.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableWitch.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableWitch.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty",
			-0.25D, 0)).setSaved(false);
	private static final DataParameter<Boolean> IS_AGGRESSIVE = EntityDataManager.<Boolean>createKey(EntityWitch.class,
			DataSerializers.BOOLEAN);
	/**
	 * Timer used as interval for a witch's attack, decremented every tick if
	 * aggressive and when reaches zero the witch will throw a potion at the
	 * target entity.
	 */
	private int witchAttackTimer;
	protected EntityAISit aiSit;

	public TameableWitch(World worldIn) {
		super(worldIn);
		setTamed(false);
		this.setSize(0.6F, 1.95F);

	}

	@Override
	protected void initEntityAI() {

        tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 60, 10.0F));
  
		
		
		
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.SUGAR, false));
		aiSit = new TameableWitch.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(1, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new TameableWitch.EntityAIBeg(this, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(1, new TameableWitch.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(IS_AGGRESSIVE, Boolean.valueOf(false));
		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
	//	dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(BEGGING, Boolean.valueOf(false));

	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.GLOWSTONE_DUST;
	}

	public static void registerFixesSheep(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableWitch");
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

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {

		if (isTamed()) {
			if (stack != null) {
				if (stack.getItem() == Items.SUGAR) {
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
		} else if (stack != null && stack.getItem() == TMItems.witch_compound) {
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
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
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
			return false;
		} else if (!(otherAnimal instanceof TameableWitch)) {
			return false;
		} else {
			TameableWitch entityTameableWitch = (TameableWitch) otherAnimal;
			return !entityTameableWitch.isTamed() ? false
					: (entityTameableWitch.isSitting() ? false : this.isInLove() && entityTameableWitch.isInLove());
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
		if (id == 15) {
			for (int i = 0; i < this.rand.nextInt(35) + 10; ++i) {
				this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH,
						this.posX + this.rand.nextGaussian() * 0.12999999523162842D,
						this.getEntityBoundingBox().maxY + 0.5D + this.rand.nextGaussian() * 0.12999999523162842D,
						this.posZ + this.rand.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		} else {
			super.handleStatusUpdate(id);
		}
		if (id == 7) {
			playTameEffect(true);
		} else if (id == 6) {
			playTameEffect(false);
		}
	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof TameableWitch) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableWitch) {
				TameableWitch entityChicken = (TameableWitch) p_142018_1_;

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
		private final TameableWitch theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableWitch blaze, float minDistance) {
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
		private final TameableWitch theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableWitch entityIn) {
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
	public TameableWitch createChild(EntityAgeable ageable) {
		TameableWitch entityTameableWitch = new TameableWitch(this.worldObj);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entityTameableWitch.setOwnerId(uuid);
			entityTameableWitch.setTamed(true);
		}

		return entityTameableWitch;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableWitch thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableWitch thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
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
		TameableWitch theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableWitch theDefendingTameableIn) {
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
		private final TameableWitch TameableWitch;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableWitch p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableWitch = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableWitch.worldObj.getNearestAttackablePlayer(TameableWitch.posX, TameableWitch.posY,
					TameableWitch.posZ, d0, d0, (Function) null,
					(@Nullable EntityPlayer player) -> (player != null) && (TameableWitch.shouldAttackPlayer(player)));
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
				if (!TameableWitch.shouldAttackPlayer(player)) {
					return false;
				}
				TameableWitch.faceEntity(player, 10.0F, 10.0F);
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
					if (TameableWitch.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableWitch) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableWitch) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableWitch.teleportToEntity(targetEntity))) {
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

	public static void registerFixesWitch(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "Witch");
	}

	

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITCH_DEATH;
	}

	/**
	 * Set whether this witch is aggressive at an entity.
	 */
	public void setAggressive(boolean aggressive) {
		this.getDataManager().set(IS_AGGRESSIVE, Boolean.valueOf(aggressive));
	}

	public boolean isDrinkingPotion() {
		return ((Boolean) this.getDataManager().get(IS_AGGRESSIVE)).booleanValue();
	}

	

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			if (this.isDrinkingPotion()) {
				if (this.witchAttackTimer-- <= 0) {
					this.setAggressive(false);
					ItemStack itemstack = this.getHeldItemMainhand();
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, (ItemStack) null);

					if (itemstack != null && itemstack.getItem() == Items.POTIONITEM) {
						List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);

						if (list != null) {
							for (PotionEffect potioneffect : list) {
								this.addPotionEffect(new PotionEffect(potioneffect));
							}
						}
					}

					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
				}
			} else {
				PotionType potiontype = null;

				if (this.rand.nextFloat() < 0.15F && this.isInsideOfMaterial(Material.WATER)
						&& !this.isPotionActive(MobEffects.WATER_BREATHING)) {
					potiontype = PotionTypes.WATER_BREATHING;
				} else if (this.rand.nextFloat() < 0.15F
						&& (this.isBurning()
								|| this.getLastDamageSource() != null && this.getLastDamageSource().isFireDamage())
						&& !this.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
					potiontype = PotionTypes.FIRE_RESISTANCE;
				} else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
					potiontype = PotionTypes.HEALING;
				} else if (this.rand.nextFloat() < 0.5F && this.getAttackTarget() != null
						&& !this.isPotionActive(MobEffects.SPEED)
						&& this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
					potiontype = PotionTypes.SWIFTNESS;
				}

				if (potiontype != null) {
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
							PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
					this.witchAttackTimer = this.getHeldItemMainhand().getMaxItemUseDuration();
					this.setAggressive(true);
					this.worldObj.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ,
							SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F,
							0.8F + this.rand.nextFloat() * 0.4F);
					IAttributeInstance iattributeinstance = this
							.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
					iattributeinstance.removeModifier(MODIFIER);
					iattributeinstance.applyModifier(MODIFIER);
				}
			}

			if (this.rand.nextFloat() < 7.5E-4F) {
				this.worldObj.setEntityState(this, (byte) 15);
			}
		}

		super.onLivingUpdate();
	}



	/**
	 * Reduces damage, depending on potions
	 */
	protected float applyPotionDamageCalculations(DamageSource source, float damage) {
		damage = super.applyPotionDamageCalculations(source, damage);

		if (source.getEntity() == this) {
			damage = 0.0F;
		}

		if (source.isMagicDamage()) {
			damage = (float) ((double) damage * 0.15D);
		}

		return damage;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_WITCH;
	}

	/**
	 * Attack the specified entity using a ranged attack.
	 * 
	 * @param distanceFactor
	 *            How far the target is, normalized and clamped between 0.1 and
	 *            1.0
	 */
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		if (isTamed()){
			 EntityWitchProjectile entitywitchprojectile = new EntityWitchProjectile(this.worldObj, this);
		        double d0 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
		        double d1 = target.posX - this.posX;
		        double d2 = d0 - entitywitchprojectile.posY;
		        double d3 = target.posZ - this.posZ;
		        float f = MathHelper.sqrt_double(d1 * d1 + d3 * d3) * 0.2F;
		        entitywitchprojectile.setThrowableHeading(d1, d2 + (double)f, d3, 1.6F, 12.0F);
		        this.playSound(SoundEvents.ENTITY_WITCH_THROW, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		        this.worldObj.spawnEntityInWorld(entitywitchprojectile);
		}else{
		if (!isTamed()){	
		
		if (!this.isDrinkingPotion()) {
			double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
			double d1 = target.posX + target.motionX - this.posX;
			double d2 = d0 - this.posY;
			double d3 = target.posZ + target.motionZ - this.posZ;
			float f = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
			PotionType potiontype = PotionTypes.HARMING;

			if (f >= 8.0F && !target.isPotionActive(MobEffects.SLOWNESS)) {
				potiontype = PotionTypes.SLOWNESS;
			} else if (target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON)) {
				potiontype = PotionTypes.POISON;
			} else if (f <= 3.0F && !target.isPotionActive(MobEffects.WEAKNESS) && this.rand.nextFloat() < 0.25F) {
				potiontype = PotionTypes.WEAKNESS;
			}

			EntityPotion entitypotion = new EntityPotion(this.worldObj, this,
					PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
			entitypotion.rotationPitch -= -20.0F;
			entitypotion.setThrowableHeading(d1, d2 + (double) (f * 0.2F), d3, 0.75F, 8.0F);
			this.worldObj.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ,
					SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
			this.worldObj.spawnEntityInWorld(entitypotion);
		}
		}
	}
	}
	public float getEyeHeight() {
		return 1.62F;
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
