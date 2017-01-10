package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.mobs.TameableCow.EntityAIFollowOwner;
import com.github.epiicthundercat.tameablemobs.mobs.TameableCow.EntityAIOwnerHurtByTarget;
import com.google.common.base.Optional;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class TameableMooshroom extends TameableCow implements net.minecraftforge.common.IShearable
{
	protected static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(TameableMooshroom.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableMooshroom.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableMooshroom.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableMooshroom.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected EntityAISit aiSit;
    public TameableMooshroom(World worldIn)
    {
        super(worldIn);
        setTamed(false);
        setSize(0.9F, 1.4F);
        spawnableBlock = Blocks.MYCELIUM;
    }


	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		aiSit = new TameableMooshroom.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(2, new TameableMooshroom.AIMeleeAttack(this, 1.0D, false));
		tasks.addTask(6, new EntityAIMate(this, 1.0D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new TameableMooshroom.EntityAIBeg(this, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableMooshroom.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

    
    
    
    public static void registerFixesTameableMooshroom(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, "TameableMushroomCow");
    }

    @SuppressWarnings("unused")
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
    {
        if (stack != null && stack.getItem() == Items.BOWL && this.getGrowingAge() >= 0 && !player.capabilities.isCreativeMode)
        {
            if (--stack.stackSize == 0)
            {
                player.setHeldItem(hand, new ItemStack(Items.MUSHROOM_STEW));
            }
            else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MUSHROOM_STEW)))
            {
                player.dropItem(new ItemStack(Items.MUSHROOM_STEW), false);
            }

            return true;
        }
        else 
        	if (false && stack != null && stack.getItem() == Items.SHEARS && this.getGrowingAge() >= 0) //Forge Disable, Moved to onSheared
        	
        {
            this.setDead();
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);

            if (!this.worldObj.isRemote)
            {
                TameableCow TameableCow = new TameableCow(this.worldObj);
                TameableCow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                TameableCow.setHealth(this.getHealth());
                TameableCow.renderYawOffset = this.renderYawOffset;

                if (this.hasCustomName())
                {
                    TameableCow.setCustomNameTag(this.getCustomNameTag());
                }

                this.worldObj.spawnEntityInWorld(TameableCow);

                for (int i = 0; i < 5; ++i)
                {
                    this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Blocks.RED_MUSHROOM)));
                }

                stack.damageItem(1, player);
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
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
		return true;


    }

    public TameableMooshroom createChild(EntityAgeable ageable)
    {
        return new TameableMooshroom(this.worldObj);
    }

    @Override public boolean isShearable(ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos){ return getGrowingAge() >= 0; }
    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, int fortune)
    {
        this.setDead();
        this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);

        TameableCow TameableCow = new TameableCow(this.worldObj);
        TameableCow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        TameableCow.setHealth(this.getHealth());
        TameableCow.renderYawOffset = this.renderYawOffset;

        if (this.hasCustomName())
        {
            TameableCow.setCustomNameTag(this.getCustomNameTag());
        }

        this.worldObj.spawnEntityInWorld(TameableCow);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int i = 0; i < 5; ++i)
        {
            ret.add(new ItemStack(Blocks.RED_MUSHROOM));
        }

        this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_MUSHROOM_COW;
    }
    
    
    static class EntityAISit extends EntityAIBase {
		private final TameableMooshroom theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableMooshroom entityIn) {
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
    
    
    
    
    
    
    
    
    
}