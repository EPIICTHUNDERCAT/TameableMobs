package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.Calendar;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.Reference;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class TMBug extends EntityAmbientCreature{
	public static final ResourceLocation LOOT_BUG = new ResourceLocation(Reference.ID, "entities/bug");
	
	 private static final DataParameter<Byte> ONGROUND = EntityDataManager.<Byte>createKey(EntityBat.class, DataSerializers.BYTE);
	    private BlockPos spawnPosition;
	 public TMBug(World worldIn)
	    {
	        super(worldIn);
	        this.setSize(0.4F, 0.5F);
	        experienceValue = 9;
	        
	    }
	 protected void entityInit()
	    {
	        super.entityInit();
	        this.dataManager.register(ONGROUND, Byte.valueOf((byte)0));
	    }
	 /**
	     * Returns the volume for the sounds this mob makes.
	     */
	    protected float getSoundVolume()
	    {
	        return 0.1F;
	    }

	    /**
	     * Gets the pitch of living sounds in living entities.
	     */
	    protected float getSoundPitch()
	    {
	        return super.getSoundPitch() * 0.95F;
	    }
/*
	    @Nullable
    protected SoundEvent getAmbientSound()
    {
        return this.getIsBugonGround() && this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
    }

	    protected SoundEvent getHurtSound()
	    {
	        return SoundEvents.ENTITY_BAT_HURT;
	    }

	    protected SoundEvent getDeathSound()
	    {
	        return SoundEvents.ENTITY_BAT_DEATH;
	    }
*/
	    /**
	     * Returns true if this entity should push and be pushed by other entities when colliding.
	     */
	    public boolean canBePushed()
	    {
	        return false;
	    }

	    protected void collideWithEntity(Entity entityIn)
	    {
	    }

	    protected void collideWithNearbyEntities()
	    {
	    }

	    protected void applyEntityAttributes()
	    {
	        super.applyEntityAttributes();
	        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
	    }
	    /**
	     * Called to update the entity's position/logic.
	     */
	    public void onUpdate()
	    {
	        super.onUpdate();

	        if (this.getIsBugOnGround())
	        {
	            this.motionX = 0.0D;
	            this.motionY = 0.0D;
	            this.motionZ = 0.0D;
	            this.posY = (double)MathHelper.floor_double(this.posY) + 1.0D - (double)this.height;
	        }
	        else
	        {
	            this.motionY *= 0.6000000238418579D;
	        }
	    }
	    public boolean getIsBugOnGround()
	    {
	        return (((Byte)this.dataManager.get(ONGROUND)).byteValue() & 1) != 0;
	    }

	    public void setIsBugOnGround(boolean isOnGround)
	    {
	        byte b0 = ((Byte)this.dataManager.get(ONGROUND)).byteValue();

	        if (isOnGround)
	        {
	            this.dataManager.set(ONGROUND, Byte.valueOf((byte)(b0 | 1)));
	        }
	        else
	        {
	            this.dataManager.set(ONGROUND, Byte.valueOf((byte)(b0 & -2)));
	        }
	    }
	    protected void updateAITasks()
	    {
	        super.updateAITasks();
	        BlockPos blockpos = new BlockPos(this);
	        BlockPos blockpos1 = blockpos.down();

	        if (this.getIsBugOnGround())
	        {
	            if (this.worldObj.getBlockState(blockpos1).isNormalCube())
	            {
	                if (this.rand.nextInt(200) == 0)
	                {
	                    this.rotationYawHead = (float)this.rand.nextInt(360);
	                }

	                if (this.worldObj.getNearestPlayerNotCreative(this, 4.0D) != null)
	                {
	                    this.setIsBugOnGround(false);
	                    this.worldObj.playEvent((EntityPlayer)null, 1025, blockpos, 0);
	                }
	            }
	            else
	            {
	                this.setIsBugOnGround(false);
	                this.worldObj.playEvent((EntityPlayer)null, 1025, blockpos, 0);
	            }
	        }
	        else
	        {
	            if (this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1))
	            {
	                this.spawnPosition = null;
	            }

	            if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.distanceSq((double)((int)this.posX), (double)((int)this.posY), (double)((int)this.posZ)) < 4.0D)
	            {
	                this.spawnPosition = new BlockPos((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
	            }

	            double d0 = (double)this.spawnPosition.getX() + 0.5D - this.posX;
	            double d1 = (double)this.spawnPosition.getY() + 0.1D - this.posY;
	            double d2 = (double)this.spawnPosition.getZ() + 0.5D - this.posZ;
	            this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * 0.10000000149011612D;
	            this.motionY += (Math.signum(d1) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
	            this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * 0.10000000149011612D;
	            float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * (180D / Math.PI)) - 90.0F;
	            float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
	            this.moveForward = 0.5F;
	            this.rotationYaw += f1;

	            if (this.rand.nextInt(100) == 0 && this.worldObj.getBlockState(blockpos1).isNormalCube())
	            {
	                this.setIsBugOnGround(true);
	            }
	        }

	    }
	    
	    protected boolean canTriggerWalking()
	    {
	        return false;
	    }

	    public void fall(float distance, float damageMultiplier)
	    {
	    }

	    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	    {
	    }

	    /**
	     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
	     */
	    public boolean doesEntityNotTriggerPressurePlate()
	    {
	        return false;
	    }

	    /**
	     * Called when the entity is attacked.
	     */
	    public boolean attackEntityFrom(DamageSource source, float amount)
	    {
	        if (this.isEntityInvulnerable(source))
	        {
	            return false;
	        }
	        else
	        {
	            if (!this.worldObj.isRemote && this.getIsBugOnGround())
	            {
	                this.setIsBugOnGround(false);
	            }

	            return super.attackEntityFrom(source, amount);
	        }
	    }

	    public static void registerFixesBug(DataFixer fixer)
	    {
	        EntityLiving.registerFixesMob(fixer, "Bug");
	    }

	    /**
	     * (abstract) Protected helper method to read subclass entity data from NBT.
	     */
	    public void readEntityFromNBT(NBTTagCompound compound)
	    {
	        super.readEntityFromNBT(compound);
	        this.dataManager.set(ONGROUND, Byte.valueOf(compound.getByte("BugFlags")));
	    }

	    /**
	     * (abstract) Protected helper method to write subclass entity data to NBT.
	     */
	    public void writeEntityToNBT(NBTTagCompound compound)
	    {
	        super.writeEntityToNBT(compound);
	        compound.setByte("BugFlags", ((Byte)this.dataManager.get(ONGROUND)).byteValue());
	    }

	    /**
	     * Checks if the entity's current position is a valid location to spawn this entity.
	     */
	    public boolean getCanSpawnHere()
	    {
	        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

	        if (blockpos.getY() >= this.worldObj.getSeaLevel())
	        {
	            return false;
	        }
	        else
	        {
	            int i = this.worldObj.getLightFromNeighbors(blockpos);
	            int j = 4;

	            if (this.isDateAroundHalloween(this.worldObj.getCurrentDate()))
	            {
	                j = 7;
	            }
	            else if (this.rand.nextBoolean())
	            {
	                return false;
	            }

	            return i > this.rand.nextInt(j) ? false : super.getCanSpawnHere();
	        }
	    }

	    private boolean isDateAroundHalloween(Calendar p_175569_1_)
	    {
	        return p_175569_1_.get(2) + 1 == 10 && p_175569_1_.get(5) >= 20 || p_175569_1_.get(2) + 1 == 11 && p_175569_1_.get(5) <= 3;
	    }

	    public float getEyeHeight()
	    {
	        return this.height / 2.0F;
	    }

	    @Override
		@Nullable
		protected ResourceLocation getLootTable() {
			return LOOT_BUG;
		}
	}   
	    
	    
	    

