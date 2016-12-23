package com.github.EPIICTHUNDERCAT.TameableMobs.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableShulker extends EntityAnimal implements IEntityOwnable, IMob {
	
	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(TameableShulker.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableShulker.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableShulker.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableShulker.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	
	private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	private static final AttributeModifier COVERED_ARMOR_BONUS_MODIFIER = (new AttributeModifier(COVERED_ARMOR_BONUS_ID,
			"Covered armor bonus", 20.0D, 0)).setSaved(false);
	protected static final DataParameter<EnumFacing> ATTACHED_FACE = EntityDataManager
			.<EnumFacing>createKey(TameableShulker.class, DataSerializers.FACING);
	protected static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS = EntityDataManager
			.<Optional<BlockPos>>createKey(TameableShulker.class, DataSerializers.OPTIONAL_BLOCK_POS);
	protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.<Byte>createKey(TameableShulker.class,
			DataSerializers.BYTE);
	
	
	protected EntityAISit aiSit;
	private float prevPeekAmount;
	private float peekAmount;
	private BlockPos currentAttachmentPosition;
	private int clientSideTeleportInterpolation;

	public TameableShulker(World worldIn) {
		super(worldIn);
		setTamed(false);
		setSize(1.0F, 1.0F);
		prevRenderYawOffset = 180.0F;
		renderYawOffset = 180.0F;
		isImmuneToFire = true;
		currentAttachmentPosition = null;
		experienceValue = 5;
	}
	

	@Override
	protected void initEntityAI() {
		
		tasks.addTask(4, new TameableShulker.AIAttack());
		tasks.addTask(7, new TameableShulker.AIPeek());
		tasks.addTask(8, new EntityAILookIdle(this));
		

		tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.GUNPOWDER, false));
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		aiSit = new TameableShulker.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));

		tasks.addTask(6, new EntityAIMate(this, 1.0D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new TameableShulker.EntityAIBeg(this, 8.0F));

		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableShulker.AIFindPlayer(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		targetTasks.addTask(2, new TameableShulker.AIAttackNearest(this));
		targetTasks.addTask(3, new TameableShulker.AIDefenseAttack(this));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(ATTACHED_FACE, EnumFacing.DOWN);
		dataManager.register(ATTACHED_BLOCK_POS, Optional.<BlockPos>absent());
		dataManager.register(PEEK_TICK, Byte.valueOf((byte) 0));
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
		compound.setByte("AttachFace", (byte) ((EnumFacing) this.dataManager.get(ATTACHED_FACE)).getIndex());
		compound.setByte("Peek", ((Byte) this.dataManager.get(PEEK_TICK)).byteValue());
		BlockPos blockpos = this.getAttachmentPos();

		if (blockpos != null) {
			compound.setInteger("APX", blockpos.getX());
			compound.setInteger("APY", blockpos.getY());
			compound.setInteger("APZ", blockpos.getZ());
		}
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
		this.dataManager.set(ATTACHED_FACE, EnumFacing.getFront(compound.getByte("AttachFace")));
		this.dataManager.set(PEEK_TICK, Byte.valueOf(compound.getByte("Peek")));

		if (compound.hasKey("APX")) {
			int i = compound.getInteger("APX");
			int j = compound.getInteger("APY");
			int k = compound.getInteger("APZ");
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.<BlockPos>absent());
		}
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
		return stack == null ? false : stack.getItem() == Items.ENDER_PEARL;
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
				if (stack.getItem() == Items.ENDER_PEARL) {
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
		} else if (stack != null && stack.getItem() == TMItems.ender_tamer) {
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
		} else if (!(otherAnimal instanceof TameableShulker)) {
			return false;
		} else {
			TameableShulker entityTameableShulker = (TameableShulker) otherAnimal;
			return !entityTameableShulker.isTamed() ? false
					: (entityTameableShulker.isSitting() ? false : this.isInLove() && entityTameableShulker.isInLove());
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
		super.handleStatusUpdate(id);

	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableShulker) {
				TameableShulker entityChicken = (TameableShulker) p_142018_1_;

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
		private final TameableShulker theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableShulker blaze, float minDistance) {
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
		private final TameableShulker theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableShulker entityIn) {
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
	public TameableShulker createChild(EntityAgeable ageable) {
		TameableShulker entityTameableShulker = new TameableShulker(this.worldObj);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entityTameableShulker.setOwnerId(uuid);
			entityTameableShulker.setTamed(true);
		}

		return entityTameableShulker;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableShulker thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableShulker thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
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
					timeToRecalcPath = 20;

					if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
						if (!thePet.getLeashed()) {
							if (thePet.getDistanceSqToEntity(theOwner) >= 200.0D) {
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
		TameableShulker theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableShulker theDefendingTameableIn) {
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
		private final TameableShulker TameableShulker;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableShulker p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableShulker = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableShulker.worldObj.getNearestAttackablePlayer(TameableShulker.posX, TameableShulker.posY,
					TameableShulker.posZ, d0, d0, (Function) null,
					(@Nullable EntityPlayer player) -> (player != null) && (TameableShulker.shouldAttackPlayer(player)));
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
				if (!TameableShulker.shouldAttackPlayer(player)) {
					return false;
				}
				TameableShulker.faceEntity(player, 10.0F, 10.0F);
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
					if (TameableShulker.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableShulker) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableShulker) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableShulker.teleportToEntity(targetEntity))) {
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		this.renderYawOffset = 180.0F;
		this.prevRenderYawOffset = 180.0F;
		this.rotationYaw = 180.0F;
		this.prevRotationYaw = 180.0F;
		this.rotationYawHead = 180.0F;
		this.prevRotationYawHead = 180.0F;
		return super.onInitialSpawn(difficulty, livingdata);
	}

	

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SHULKER_AMBIENT;
	}

	/**
	 * Plays living's sound at its position
	 */
	public void playLivingSound() {
		if (!this.isClosed()) {
			super.playLivingSound();
		}
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}

	protected SoundEvent getHurtSound() {
		return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
	}

	

	

	protected EntityBodyHelper createBodyHelper() {
		return new TameableShulker.BodyHelper(this);
	}

	public static void registerFixesTameableShulker(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableShulker");
	}



	

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();
		BlockPos blockpos = (BlockPos) ((Optional) this.dataManager.get(ATTACHED_BLOCK_POS)).orNull();

		if (blockpos == null && !this.worldObj.isRemote) {
			blockpos = new BlockPos(this);
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
		}

		if (this.isRiding()) {
			blockpos = null;
			float f = this.getRidingEntity().rotationYaw;
			this.rotationYaw = f;
			this.renderYawOffset = f;
			this.prevRenderYawOffset = f;
			this.clientSideTeleportInterpolation = 0;
		} else if (!this.worldObj.isRemote) {
			IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

			if (iblockstate.getMaterial() != Material.AIR) {
				if (iblockstate.getBlock() == Blocks.PISTON_EXTENSION) {
					EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(BlockPistonBase.FACING);
					blockpos = blockpos.offset(enumfacing);
					this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
				} else if (iblockstate.getBlock() == Blocks.PISTON_HEAD) {
					EnumFacing enumfacing3 = (EnumFacing) iblockstate.getValue(BlockPistonExtension.FACING);
					blockpos = blockpos.offset(enumfacing3);
					this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
				} else {
					this.tryTeleportToNewPosition();
				}
			}

			BlockPos blockpos1 = blockpos.offset(this.getAttachmentFacing());

			if (!this.worldObj.isBlockNormalCube(blockpos1, false)) {
				boolean flag = false;

				for (EnumFacing enumfacing1 : EnumFacing.values()) {
					blockpos1 = blockpos.offset(enumfacing1);

					if (this.worldObj.isBlockNormalCube(blockpos1, false)) {
						this.dataManager.set(ATTACHED_FACE, enumfacing1);
						flag = true;
						break;
					}
				}

				if (!flag) {
					this.tryTeleportToNewPosition();
				}
			}

			BlockPos blockpos2 = blockpos.offset(this.getAttachmentFacing().getOpposite());

			if (this.worldObj.isBlockNormalCube(blockpos2, false)) {
				this.tryTeleportToNewPosition();
			}
		}

		float f1 = (float) this.getPeekTick() * 0.01F;
		this.prevPeekAmount = this.peekAmount;

		if (this.peekAmount > f1) {
			this.peekAmount = MathHelper.clamp_float(this.peekAmount - 0.05F, f1, 1.0F);
		} else if (this.peekAmount < f1) {
			this.peekAmount = MathHelper.clamp_float(this.peekAmount + 0.05F, 0.0F, f1);
		}

		if (blockpos != null) {
			if (this.worldObj.isRemote) {
				if (this.clientSideTeleportInterpolation > 0 && this.currentAttachmentPosition != null) {
					--this.clientSideTeleportInterpolation;
				} else {
					this.currentAttachmentPosition = blockpos;
				}
			}

			this.posX = (double) blockpos.getX() + 0.5D;
			this.posY = (double) blockpos.getY();
			this.posZ = (double) blockpos.getZ() + 0.5D;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.lastTickPosX = this.posX;
			this.lastTickPosY = this.posY;
			this.lastTickPosZ = this.posZ;
			double d3 = 0.5D - (double) MathHelper.sin((0.5F + this.peekAmount) * (float) Math.PI) * 0.5D;
			double d4 = 0.5D - (double) MathHelper.sin((0.5F + this.prevPeekAmount) * (float) Math.PI) * 0.5D;
			double d5 = d3 - d4;
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			EnumFacing enumfacing2 = this.getAttachmentFacing();

			switch (enumfacing2) {
			case DOWN:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D,
						this.posX + 0.5D, this.posY + 1.0D + d3, this.posZ + 0.5D));
				d1 = d5;
				break;
			case UP:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY - d3, this.posZ - 0.5D,
						this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
				d1 = -d5;
				break;
			case NORTH:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D,
						this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D + d3));
				d2 = d5;
				break;
			case SOUTH:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D - d3,
						this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
				d2 = -d5;
				break;
			case WEST:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D,
						this.posX + 0.5D + d3, this.posY + 1.0D, this.posZ + 0.5D));
				d0 = d5;
				break;
			case EAST:
				this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D - d3, this.posY, this.posZ - 0.5D,
						this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
				d0 = -d5;
			}

			if (d5 > 0.0D) {
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
						this.getEntityBoundingBox());

				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof TameableShulker) && !entity.noClip) {
							entity.moveEntity(d0, d1, d2);
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the x,y,z of the entity from the given parameters. Also seems to set
	 * up a bounding box.
	 */
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);

		if (this.dataManager != null && this.ticksExisted != 0) {
			Optional<BlockPos> optional = (Optional) this.dataManager.get(ATTACHED_BLOCK_POS);
			Optional<BlockPos> optional1 = Optional.<BlockPos>of(new BlockPos(x, y, z));

			if (!optional1.equals(optional)) {
				this.dataManager.set(ATTACHED_BLOCK_POS, optional1);
				this.dataManager.set(PEEK_TICK, Byte.valueOf((byte) 0));
				this.isAirBorne = true;
			}
		}
	}

	protected boolean tryTeleportToNewPosition() {
		if (!this.isAIDisabled() && this.isEntityAlive()) {
			BlockPos blockpos = new BlockPos(this);

			for (int i = 0; i < 5; ++i) {
				BlockPos blockpos1 = blockpos.add(8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17),
						8 - this.rand.nextInt(17));

				if (blockpos1.getY() > 0 && this.worldObj.isAirBlock(blockpos1)
						&& this.worldObj.isInsideBorder(this.worldObj.getWorldBorder(), this)
						&& this.worldObj.getCollisionBoxes(this, new AxisAlignedBB(blockpos1)).isEmpty()) {
					boolean flag = false;

					for (EnumFacing enumfacing : EnumFacing.values()) {
						if (this.worldObj.isBlockNormalCube(blockpos1.offset(enumfacing), false)) {
							this.dataManager.set(ATTACHED_FACE, enumfacing);
							flag = true;
							break;
						}
					}

					if (flag) {
						net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(
								this, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), 0);
						if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
							flag = false;
						blockpos1 = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
					}

					if (flag) {
						this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
						this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos1));
						this.dataManager.set(PEEK_TICK, Byte.valueOf((byte) 0));
						this.setAttackTarget((EntityLivingBase) null);
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevRenderYawOffset = 180.0F;
		this.renderYawOffset = 180.0F;
		this.rotationYaw = 180.0F;
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (ATTACHED_BLOCK_POS.equals(key) && this.worldObj.isRemote && !this.isRiding()) {
			BlockPos blockpos = this.getAttachmentPos();

			if (blockpos != null) {
				if (this.currentAttachmentPosition == null) {
					this.currentAttachmentPosition = blockpos;
				} else {
					this.clientSideTeleportInterpolation = 6;
				}

				this.posX = (double) blockpos.getX() + 0.5D;
				this.posY = (double) blockpos.getY();
				this.posZ = (double) blockpos.getZ() + 0.5D;
				this.prevPosX = this.posX;
				this.prevPosY = this.posY;
				this.prevPosZ = this.posZ;
				this.lastTickPosX = this.posX;
				this.lastTickPosY = this.posY;
				this.lastTickPosZ = this.posZ;
			}
		}

		super.notifyDataManagerChange(key);
	}

	/**
	 * Set the position and rotation values directly without any clamping.
	 */
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		this.newPosRotationIncrements = 0;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isClosed()) {
			Entity entity = source.getSourceOfDamage();

			if (entity instanceof EntityArrow) {
				return false;
			}
		}

		if (super.attackEntityFrom(source, amount)) {
			if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.rand.nextInt(4) == 0) {
				this.tryTeleportToNewPosition();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean isClosed() {
		return this.getPeekTick() == 0;
	}

	/**
	 * Returns the collision bounding box for this entity
	 */
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
	}

	public EnumFacing getAttachmentFacing() {
		return (EnumFacing) this.dataManager.get(ATTACHED_FACE);
	}

	@Nullable
	public BlockPos getAttachmentPos() {
		return (BlockPos) ((Optional) this.dataManager.get(ATTACHED_BLOCK_POS)).orNull();
	}

	public void setAttachmentPos(@Nullable BlockPos pos) {
		this.dataManager.set(ATTACHED_BLOCK_POS, Optional.fromNullable(pos));
	}

	public int getPeekTick() {
		return ((Byte) this.dataManager.get(PEEK_TICK)).byteValue();
	}

	/**
	 * Applies or removes armor modifier
	 */
	public void updateArmorModifier(int p_184691_1_) {
		if (!this.worldObj.isRemote) {
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS_MODIFIER);

			if (p_184691_1_ == 0) {
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(COVERED_ARMOR_BONUS_MODIFIER);
				this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
			} else {
				this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
			}
		}

		this.dataManager.set(PEEK_TICK, Byte.valueOf((byte) p_184691_1_));
	}

	@SideOnly(Side.CLIENT)
	public float getClientPeekAmount(float p_184688_1_) {
		return this.prevPeekAmount + (this.peekAmount - this.prevPeekAmount) * p_184688_1_;
	}

	@SideOnly(Side.CLIENT)
	public int getClientTeleportInterp() {
		return this.clientSideTeleportInterpolation;
	}

	@SideOnly(Side.CLIENT)
	public BlockPos getOldAttachPos() {
		return this.currentAttachmentPosition;
	}

	public float getEyeHeight() {
		return 0.5F;
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the
	 * faceEntity method. This is only currently use in wolves.
	 */
	public int getVerticalFaceSpeed() {
		return 180;
	}

	public int getHorizontalFaceSpeed() {
		return 180;
	}

	/**
	 * Applies a velocity to the entities, to push them away from eachother.
	 */
	public void applyEntityCollision(Entity entityIn) {
	}

	public float getCollisionBorderSize() {
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	public boolean isAttachedToBlock() {
		return this.currentAttachmentPosition != null && this.getAttachmentPos() != null;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_SHULKER;
	}

	class AIAttack extends EntityAIBase {
		private int attackTime;

		public AIAttack() {
			this.setMutexBits(3);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = TameableShulker.this.getAttackTarget();
			return entitylivingbase != null && entitylivingbase.isEntityAlive()
					? TameableShulker.this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL : false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.attackTime = 20;
			TameableShulker.this.updateArmorModifier(100);
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			TameableShulker.this.updateArmorModifier(0);
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			if (TameableShulker.this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) {
				--this.attackTime;
				EntityLivingBase entitylivingbase = TameableShulker.this.getAttackTarget();
				TameableShulker.this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
				double d0 = TameableShulker.this.getDistanceSqToEntity(entitylivingbase);

				if (d0 < 400.0D) {
					if (this.attackTime <= 0) {
						this.attackTime = 20 + TameableShulker.this.rand.nextInt(10) * 20 / 2;
						EntityShulkerBullet EntityShulkerBullet = new EntityShulkerBullet(
								TameableShulker.this.worldObj, TameableShulker.this, entitylivingbase,
								TameableShulker.this.getAttachmentFacing().getAxis());
						TameableShulker.this.worldObj.spawnEntityInWorld(EntityShulkerBullet);
						TameableShulker.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F,
								(TameableShulker.this.rand.nextFloat() - TameableShulker.this.rand.nextFloat()) * 0.2F
										+ 1.0F);
					}
				} else {
					TameableShulker.this.setAttackTarget((EntityLivingBase) null);
				}

				super.updateTask();
			}
		}
	}

	class AIAttackNearest extends EntityAINearestAttackableTarget<EntityPlayer> {
		public AIAttackNearest(TameableShulker shulker) {
			super(shulker, EntityPlayer.class, true);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return TameableShulker.this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL ? false
					: super.shouldExecute();
		}

		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			EnumFacing enumfacing = ((TameableShulker) this.taskOwner).getAttachmentFacing();
			return enumfacing.getAxis() == EnumFacing.Axis.X
					? this.taskOwner.getEntityBoundingBox().expand(4.0D, targetDistance, targetDistance)
					: (enumfacing.getAxis() == EnumFacing.Axis.Z
							? this.taskOwner.getEntityBoundingBox().expand(targetDistance, targetDistance, 4.0D)
							: this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance));
		}
	}

	static class AIDefenseAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {
		public AIDefenseAttack(TameableShulker shulker) {
			super(shulker, EntityLivingBase.class, 10, true, false, new Predicate<EntityLivingBase>() {
				public boolean apply(@Nullable EntityLivingBase p_apply_1_) {
					return p_apply_1_ instanceof IMob;
				}
			});
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return this.taskOwner.getTeam() == null ? false : super.shouldExecute();
		}

		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			EnumFacing enumfacing = ((TameableShulker) this.taskOwner).getAttachmentFacing();
			return enumfacing.getAxis() == EnumFacing.Axis.X
					? this.taskOwner.getEntityBoundingBox().expand(4.0D, targetDistance, targetDistance)
					: (enumfacing.getAxis() == EnumFacing.Axis.Z
							? this.taskOwner.getEntityBoundingBox().expand(targetDistance, targetDistance, 4.0D)
							: this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance));
		}
	}

	class AIPeek extends EntityAIBase {
		private int peekTime;

		private AIPeek() {
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return TameableShulker.this.getAttackTarget() == null && TameableShulker.this.rand.nextInt(40) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return TameableShulker.this.getAttackTarget() == null && this.peekTime > 0;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.peekTime = 20 * (1 + TameableShulker.this.rand.nextInt(3));
			TameableShulker.this.updateArmorModifier(30);
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			if (TameableShulker.this.getAttackTarget() == null) {
				TameableShulker.this.updateArmorModifier(0);
			}
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			--this.peekTime;
		}
	}

	class BodyHelper extends EntityBodyHelper {
		public BodyHelper(EntityLivingBase p_i47062_2_) {
			super(p_i47062_2_);
		}

		/**
		 * Update the Head and Body rendenring angles
		 */
		public void updateRenderAngles() {
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 public void fall(float distance, float damageMultiplier)
	    {
	    }

	  

	    /**
	     * Get number of ticks, at least during which the living entity will be silent.
	     */
	    public int getTalkInterval()
	    {
	        return 120;
	    }

	    /**
	     * Determines if an entity can be despawned, used on idle far away entities
	     */
	    protected boolean canDespawn()
	    {
	        return false;
	    }
}
