package com.github.EPIICTHUNDERCAT.TameableMobs.util;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableBat;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableBlaze;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCaveSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableChicken;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCow;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCreeper;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEnderman;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEndermite;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableGhast;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableGiantZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableGuardian;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableIronGolem;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableMagmaCube;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableMooshroom;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePig;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePigZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePolarBear;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableRabbit;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSheep;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableShulker;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSilverfish;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSlime;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSquid;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableWitch;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableZombie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TMEventHandler {
	/*
	 * @SubscribeEvent public void onMobRighClick(EntityInteract event) { World
	 * world = event.getWorld(); if (world.isRemote) {
	 * 
	 * return; }
	 * 
	 * Entity entityTarget = event.getTarget();
	 * 
	 * if (entityTarget instanceof EntityCow && !(entityTarget instanceof
	 * TameableCow)) { ItemStack heldItem =
	 * event.getEntityPlayer().getHeldItemMainhand(); if (heldItem != null) { if
	 * (heldItem.getItem() == TMItems.taming_wheat) { // if
	 * (!ModWorldData.get(world).getHasCastleSpawned()) {
	 * 
	 * EntityPlayer player = event.getEntityPlayer();
	 * 
	 * if (!((EntityCow) entityTarget).isChild()) { // player.addChatMessage(new
	 * // TextComponentString(Utilities.stringToRainbow("This // cow is now your
	 * Family Cow!")));
	 * 
	 * TameableCow spawnTCow = new TameableCow(world);
	 * spawnTCow.setLocationAndAngles(entityTarget.posX, entityTarget.posY,
	 * entityTarget.posZ, MathHelper.wrapDegrees(world.rand.nextFloat() *
	 * 360.0F), 0.0F); world.spawnEntityInWorld(spawnTCow);
	 * 
	 * entityTarget.setDead();
	 * 
	 * // player.addStat(MOD.achievementNamme, 1); } } } } }
	 */
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onMobRighClick(EntityInteract event) {
		World world = event.getEntityLiving().worldObj;
		if (world.isRemote || event.getHand() == EnumHand.OFF_HAND) {
			return;
		}

		Entity entityTarget = event.getTarget();

		if (entityTarget instanceof EntityCow && !(entityTarget instanceof TameableCow)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_wheat) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityCow) entityTarget).isChild()) {

						TameableCow spawnTCow = new TameableCow(world);
						spawnTCow.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTCow);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityPig && !(entityTarget instanceof TameablePig)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_carrot) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityPig) entityTarget).isChild()) {

						TameablePig spawnTPig = new TameablePig(world);
						spawnTPig.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTPig);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityPigZombie && !(entityTarget instanceof TameablePigZombie)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.GOLD_INGOT) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityPigZombie) entityTarget).isChild()) {

						TameablePigZombie spawnTPigZombie = new TameablePigZombie(world);
						spawnTPigZombie.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTPigZombie);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityBat && !(entityTarget instanceof TameableBat)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.GOLD_INGOT) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityBat) entityTarget).isChild()) {

						TameableBat spawnTBat = new TameableBat(world);
						spawnTBat.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTBat);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityBlaze && !(entityTarget instanceof TameableBlaze)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.BLAZE_POWDER) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityBlaze) entityTarget).isChild()) {

						TameableBlaze spawnTBlaze = new TameableBlaze(world);
						spawnTBlaze.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTBlaze);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityCaveSpider && !(entityTarget instanceof TameableCaveSpider)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.spider_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityCaveSpider) entityTarget).isChild()) {

						TameableCaveSpider spawnTCSpider = new TameableCaveSpider(world);
						spawnTCSpider.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTCSpider);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityChicken && !(entityTarget instanceof TameableChicken)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_seed) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityChicken) entityTarget).isChild()) {

						TameableChicken spawnTChicken = new TameableChicken(world);
						spawnTChicken.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTChicken);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityCreeper && !(entityTarget instanceof TameableCreeper)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.creeper_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityCreeper) entityTarget).isChild()) {

						TameableCreeper spawnTCreeper = new TameableCreeper(world);
						spawnTCreeper.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTCreeper);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityEnderman && !(entityTarget instanceof TameableEnderman)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.ender_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityEnderman) entityTarget).isChild()) {

						TameableEnderman spawnTEnderman = new TameableEnderman(world);
						spawnTEnderman.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTEnderman);

						entityTarget.setDead();

					}
				}
			}

		}
		if (entityTarget instanceof EntityEndermite && !(entityTarget instanceof TameableEndermite)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.ender_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityEnderman) entityTarget).isChild()) {

						TameableEndermite spawnTEndermite = new TameableEndermite(world);
						spawnTEndermite.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTEndermite);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityGhast && !(entityTarget instanceof TameableGhast)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.creeper_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityGhast) entityTarget).isChild()) {

						TameableGhast spawnTGhast = new TameableGhast(world);
						spawnTGhast.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTGhast);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityPolarBear && !(entityTarget instanceof TameablePolarBear)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.FISH) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityPolarBear) entityTarget).isChild()) {

						TameablePolarBear spawnTPolar = new TameablePolarBear(world);
						spawnTPolar.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTPolar);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntitySheep && !(entityTarget instanceof TameableSheep)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_wheat) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntitySheep) entityTarget).isChild()) {

						TameableSheep spawnTSheep = new TameableSheep(world);
						spawnTSheep.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTSheep);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntityShulker && !(entityTarget instanceof TameableShulker)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.ender_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityShulker) entityTarget).isChild()) {

						TameableShulker spawnTShulker = new TameableShulker(world);
						spawnTShulker.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTShulker);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntitySpider && !(entityTarget instanceof TameableSpider)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.spider_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntitySpider) entityTarget).isChild()) {

						TameableSpider spawnTSpider = new TameableSpider(world);
						spawnTSpider.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTSpider);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntitySquid && !(entityTarget instanceof TameableSquid)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.FISH) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntitySquid) entityTarget).isChild()) {

						TameableSquid spawnTSquid = new TameableSquid(world);
						spawnTSquid.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTSquid);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntityWitch && !(entityTarget instanceof TameableWitch)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.creeper_tamer) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityWitch) entityTarget).isChild()) {

						TameableWitch spawnTWitch = new TameableWitch(world);
						spawnTWitch.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTWitch);

						entityTarget.setDead();

					}
				}
			}
		}

		if (entityTarget instanceof EntitySlime && !(entityTarget instanceof TameableSlime)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.SLIME_BALL) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntitySlime) entityTarget).isChild()) {

						TameableSlime spawnTSlime = new TameableSlime(world);
						spawnTSlime.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTSlime);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityMagmaCube && !(entityTarget instanceof TameableMagmaCube)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.SLIME_BALL) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityMagmaCube) entityTarget).isChild()) {

						TameableMagmaCube spawnTLavaSlime = new TameableMagmaCube(world);
						spawnTLavaSlime.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTLavaSlime);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityGuardian && !(entityTarget instanceof TameableGuardian)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.FISH) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityGuardian) entityTarget).isChild()) {

						TameableGuardian spawnTGuardian = new TameableGuardian(world);
						spawnTGuardian.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTGuardian);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityRabbit && !(entityTarget instanceof TameableRabbit)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_carrot) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityRabbit) entityTarget).isChild()) {

						TameableRabbit spawnTRabbit = new TameableRabbit(world);
						spawnTRabbit.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTRabbit);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityMooshroom && !(entityTarget instanceof TameableMooshroom)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == TMItems.taming_wheat) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityMooshroom) entityTarget).isChild()) {

						TameableMooshroom spawnTMooshroom = new TameableMooshroom(world);
						spawnTMooshroom.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTMooshroom);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityZombie && !(entityTarget instanceof TameableZombie)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.ROTTEN_FLESH) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityZombie) entityTarget).isChild()) {

						TameableZombie spawnTZombie = new TameableZombie(world);
						spawnTZombie.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTZombie);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityGiantZombie && !(entityTarget instanceof TameableGiantZombie)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.ROTTEN_FLESH) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityGiantZombie) entityTarget).isChild()) {

						TameableGiantZombie spawnTGiantZombie = new TameableGiantZombie(world);
						spawnTGiantZombie.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTGiantZombie);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntitySilverfish && !(entityTarget instanceof TameableSilverfish)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.IRON_INGOT) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntitySilverfish) entityTarget).isChild()) {

						TameableSilverfish spawnTSilverfish = new TameableSilverfish(world);
						spawnTSilverfish.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTSilverfish);

						entityTarget.setDead();

					}
				}
			}
		}
		if (entityTarget instanceof EntityIronGolem && !(entityTarget instanceof TameableIronGolem)) {
			ItemStack correspondingItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (correspondingItem != null) {
				if (correspondingItem.getItem() == Items.IRON_INGOT) {
					EntityPlayer player = event.getEntityPlayer();

					if (!((EntityIronGolem) entityTarget).isChild()) {

						TameableIronGolem spawnTIronGolem = new TameableIronGolem(world);
						spawnTIronGolem.setLocationAndAngles(entityTarget.posX, entityTarget.posY, entityTarget.posZ,
								MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(spawnTIronGolem);

						entityTarget.setDead();

					}
				}
		}
		}

	}
}
