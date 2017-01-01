package com.github.EPIICTHUNDERCAT.TameableMobs.mobs;

/*
 * Thank you TheRealP455w0rd for your help with the biome iterator code!
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.EPIICTHUNDERCAT.TameableMobs.TameableMobs;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.itementities.EntityBatPee;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.itementities.EntityWitchProjectile;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
	public static void init() {
		int tameablepolarbearColor = new Color(254, 85, 176).getRGB();
		int tameablechickenColor = new Color(254, 85, 176).getRGB();
		int tameableblazeColor = new Color(254, 85, 176).getRGB();
		int tameablebatColor = new Color(254, 85, 176).getRGB();
		int tameablesheepColor = new Color(254, 85, 176).getRGB();
		int tameablepigColor = new Color(254, 85, 176).getRGB();
		int tameablecowColor = new Color(254, 85, 176).getRGB();
		int tameablecreeperColor = new Color(254, 85, 176).getRGB();
		int tameablezombieColor = new Color(254, 85, 176).getRGB();
		int tameableendermiteColor = new Color(254, 85, 176).getRGB();
		int tameableendermanColor = new Color(254, 85, 176).getRGB();
		int tameablevillagerColor = new Color(254, 85, 176).getRGB();
		int tameablesnowgolemColor = new Color(254, 85, 176).getRGB();
		int tameableirongolemColor = new Color(254, 85, 176).getRGB();
		int tameablemooshroomColor = new Color(254, 85, 176).getRGB();
		int tameablesquidColor = new Color(254, 85, 176).getRGB();
		int tameablespiderColor = new Color(254, 85, 176).getRGB();
		int tameablecavespiderColor = new Color(254, 85, 176).getRGB();
		int tameableghastColor = new Color(254, 85, 176).getRGB();
		int tameablegiantzombieColor = new Color(254, 85, 176).getRGB();
		int tameableguardianColor = new Color(254, 85, 176).getRGB();
		int tameablemagmacubeColor = new Color(254, 85, 176).getRGB();
		int tameablepigzombieColor = new Color(254, 85, 176).getRGB();
		int tameableshulkerColor = new Color(254, 85, 176).getRGB();
		int tameablesilverfishColor = new Color(254, 85, 176).getRGB();
		int tameableskeletonColor = new Color(254, 85, 176).getRGB();
		int tameableslimeColor = new Color(254, 85, 176).getRGB();
		int tameablewitchColor = new Color(254, 85, 176).getRGB();

		// Every entity in our mod has an ID (local to this mod)

		// TameablePolarBear
		EntityRegistry.registerModEntity(TameablePolarBear.class, "TameablePolarBear", 0, TameableMobs.instance, 80, 3,
				false, 0, tameablepolarbearColor);

		// TameableChicken
		EntityRegistry.registerModEntity(TameableChicken.class, "TameableChicken", 1, TameableMobs.instance, 80, 3,
				false, 0, tameablechickenColor);

		// TameableBlaze
		EntityRegistry.registerModEntity(TameableBlaze.class, "TameableBlaze", 2, TameableMobs.instance, 80, 3, false,
				0, tameableblazeColor);
		// TameableBat
		EntityRegistry.registerModEntity(TameableBat.class, "TameableBat", 3, TameableMobs.instance, 80, 3, false, 0,
				tameablebatColor);

		// TameableSheep
		EntityRegistry.registerModEntity(TameableSheep.class, "TameableSheep", 4, TameableMobs.instance, 80, 3, false,
				0, tameablesheepColor);
		// TameablePig
		EntityRegistry.registerModEntity(TameablePig.class, "TameablePig", 5, TameableMobs.instance, 80, 3, false, 0,
				tameablepigColor);
		// TameableCow
		EntityRegistry.registerModEntity(TameableCow.class, "TameableCow", 6, TameableMobs.instance, 80, 3, false, 0,
				tameablecowColor);
		// TameableCreeper
		EntityRegistry.registerModEntity(TameableCreeper.class, "TameableCreeper", 7, TameableMobs.instance, 80, 3,
				false, 0, tameablecreeperColor);
		// TameableZombie
		// EntityRegistry.registerModEntity(TameableZombie.class,
		// "TameableZombie", 8, TameableMobs.instance, 80, 3, false, 0,
		// tameablezombieColor);
		// TameableEndermite
		EntityRegistry.registerModEntity(TameableEndermite.class, "TameableEndermite", 9, TameableMobs.instance, 80, 3,
				false, 0, tameableendermiteColor);
		// TameableEnderman
		EntityRegistry.registerModEntity(TameableEnderman.class, "TameableEnderman", 10, TameableMobs.instance, 80, 3,
				false, 0, tameableendermanColor);
		// TameableVillager
		// EntityRegistry.registerModEntity(TameableVillager.class,
		// "TameableVillager", 11, TameableMobs.instance, 80, 3, false, 0,
		// tameablevillagerColor);
		// TameableSnowGolem
		// EntityRegistry.registerModEntity(TameableSnowGolem.class,
		// "TameableSnowGolem", 12, TameableMobs.instance, 80, 3, false, 0,
		// tameablesnowgolemColor);
		// TameableIronGolem
		// EntityRegistry.registerModEntity(TameableIronGolem.class,
		// "TameableIronGolem", 13, TameableMobs.instance, 80, 3, false, 0,
		// tameableirongolemColor);
		// TameableMooshroom
		// EntityRegistry.registerModEntity(TameableMooshroom.class,
		// "TameableMooshroom", 14, TameableMobs.instance, 80, 3, false, 0,
		// tameablemooshroomColor);
		// TameableSquid
		EntityRegistry.registerModEntity(TameableSquid.class, "TameableSquid", 15, TameableMobs.instance, 80, 3, false,
				0, tameablesquidColor);

		// TameableSpider
		EntityRegistry.registerModEntity(TameableSpider.class, "TameableSpider", 16, TameableMobs.instance, 80, 3,
				false, 0, tameablespiderColor);

		// TameableCaveSpider
		EntityRegistry.registerModEntity(TameableCaveSpider.class, "TameableCaveSpider", 17, TameableMobs.instance, 80,
				3, false, 0, tameablecavespiderColor);
		// TameableGhast
		EntityRegistry.registerModEntity(TameableGhast.class, "TameableGhast", 18, TameableMobs.instance, 80, 3, false,
				0, tameableghastColor);
		// TameableGiantZombie
		// EntityRegistry.registerModEntity(TameableGiantZombie.class,
		// "TameableGiantZombie", 19, TameableMobs.instance, 80, 3, false, 0,
		// tameablegiantzombieColor);
		// TameableGuardian
		// EntityRegistry.registerModEntity(TameableGuardian.class,
		// "TameableGuardian", 20, TameableMobs.instance, 80, 3, false, 0,
		// tameableguardianColor);
		// TameableMagmaCube
		EntityRegistry.registerModEntity(TameableMagmaCube.class, "TameableMagmaCube", 21, TameableMobs.instance, 80, 3,
				false, 0, tameablemagmacubeColor);
		// TameablePigZombie
		EntityRegistry.registerModEntity(TameablePigZombie.class, "TameablePigZombie", 23, TameableMobs.instance, 80, 3,
				false, 0, tameablepigzombieColor);
		// TameableShulker
		EntityRegistry.registerModEntity(TameableShulker.class, "TameableShulker", 24, TameableMobs.instance, 80, 3,
				false, 0, tameableshulkerColor);
		// TameableSilverfish
		// EntityRegistry.registerModEntity(TameableSilverfish.class,
		// "TameableSilverfish", 25, TameableMobs.instance, 80, 3, false, 0,
		// tameablesilverfishColor);
		// TameableSkeleton
		// EntityRegistry.registerModEntity(TameableSkeleton.class,
		// "TameableSkeleton", 26, TameableMobs.instance, 80, 3, false, 0,
		// tameableskeletonColor);
		// TameableSlime
		EntityRegistry.registerModEntity(TameableSlime.class, "TameableSlime", 27, TameableMobs.instance, 80, 3, false,
				0, tameableslimeColor);
		// TameableWitch
		EntityRegistry.registerModEntity(TameableWitch.class, "TameableWitch", 28, TameableMobs.instance, 80, 3, false,
				0, tameablewitchColor);
		// WitchProjectile
		EntityRegistry.registerModEntity(EntityWitchProjectile.class, "WitchProjectile", 29, TameableMobs.instance, 64,
				2, true);
		// BatPee
		EntityRegistry.registerModEntity(EntityBatPee.class, "BatPee", 100, TameableMobs.instance, 64, 2, true);

		/*
		 * We want our mob to spawn in Plains and ice plains biomes. If you
		 * don't add this then it will not spawn automatically but you can of
		 * course still make it spawn manually
		 */

		// TameablePolarBear
		EntityRegistry.addSpawn(TameablePolarBear.class, 100, 3, 8, EnumCreatureType.CREATURE, Biomes.ICE_PLAINS,
				Biomes.ICE_MOUNTAINS, Biomes.MUTATED_ICE_FLATS);
		// TameableChicken
		EntityRegistry.addSpawn(TameableChicken.class, 10, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());

		// TameableBlaze
		EntityRegistry.addSpawn(TameableBlaze.class, 100, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableBat
		EntityRegistry.addSpawn(TameableBat.class, 10, 8, 8, EnumCreatureType.AMBIENT, getPassiveBiomeList());

		// TameableSheep
		EntityRegistry.addSpawn(TameableSheep.class, 12, 5, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());

		// TameablePig
		EntityRegistry.addSpawn(TameablePig.class, 10, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());
		// TameablePigZombie
		EntityRegistry.addSpawn(TameablePigZombie.class, 100, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableCreeper
		EntityRegistry.addSpawn(TameableCreeper.class, 100, 4, 8, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableCow
		EntityRegistry.addSpawn(TameableCow.class, 8, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());
		// TameableEndermite
		EntityRegistry.addSpawn(TameableEndermite.class, 100, 3, 8, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableEnderman
		EntityRegistry.addSpawn(TameableEnderman.class, 10, 1, 5, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableSpider
		EntityRegistry.addSpawn(TameableSpider.class, 100, 4, 4, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableCaveSpider
		EntityRegistry.addSpawn(TameableCaveSpider.class, 100, 2, 4, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableWitch
		EntityRegistry.addSpawn(TameableWitch.class, 5, 1, 2, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableSquid
		EntityRegistry.addSpawn(TameableSquid.class, 10, 4, 4, EnumCreatureType.WATER_CREATURE, Biomes.OCEAN,
				Biomes.DEEP_OCEAN, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
		// TameableGhast
		EntityRegistry.addSpawn(TameableGhast.class, 10, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableShulker
		EntityRegistry.addSpawn(TameableShulker.class, 10, 1, 4, EnumCreatureType.MONSTER, Biomes.SKY);
		// TameableMagmaCube
		EntityRegistry.addSpawn(TameableMagmaCube.class, 100, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableSlime
		EntityRegistry.addSpawn(TameableSlime.class, 100, 3, 8, EnumCreatureType.MONSTER, getSlimeBiomeList());
		/*
		 * // TameableZombie EntityRegistry.addSpawn(TameableZombie.class, 100,
		 * 3, 8, EnumCreatureType.MONSTER, getBiomeList()); // TameableRabbit
		 * EntityRegistry.addSpawn(TameableRabbit.class, 100, 3, 8,
		 * EnumCreatureType.CREATURE, getBiomeList()); // TameableVillager
		 * EntityRegistry.addSpawn(TameableVillager.class, 100, 3, 8,
		 * EnumCreatureType.CREATURE, getBiomeList()); // TameableSnowGolem
		 * EntityRegistry.addSpawn(TameableSnowGolem.class, 100, 3, 8,
		 * EnumCreatureType.MONSTER, getBiomeList()); // TameableIronGolem
		 * EntityRegistry.addSpawn(TameableIronGolem.class, 100, 3, 8,
		 * EnumCreatureType.MONSTER, getBiomeList()); // TameableMooshroom
		 * EntityRegistry.addSpawn(TameableMooshroom.class, 100, 3, 8,
		 * EnumCreatureType.CREATURE, getBiomeList()); // TameableGiantZombie
		 * EntityRegistry.addSpawn(TameableGiantZombie.class, 100, 3, 8,
		 * EnumCreatureType.MONSTER, getBiomeList()); // TameableGuardian
		 * EntityRegistry.addSpawn(TameableGuardian.class, 100, 3, 8,
		 * EnumCreatureType.MONSTER, getBiomeList());
		 * 
		 * 
		 * TameableSilverfish EntityRegistry.addSpawn(TameableSilverfish.class,
		 * 100, 3, 8, EnumCreatureType.MONSTER, getBiomeList()); //
		 * TameableSkeleton EntityRegistry.addSpawn(TameableSkeleton.class, 100,
		 * 3, 8, EnumCreatureType.MONSTER, getBiomeList());
		 */

		/*
		 * Mob Placement
		 */

		// TameableChicken
		EntitySpawnPlacementRegistry.setPlacementType(TameableChicken.class, SpawnPlacementType.ON_GROUND);

		// TameableBlaze
		EntitySpawnPlacementRegistry.setPlacementType(TameableBlaze.class, SpawnPlacementType.ON_GROUND);
		// TameableBat
		EntitySpawnPlacementRegistry.setPlacementType(TameableBat.class, SpawnPlacementType.ON_GROUND);

		// TameablePolarBear
		EntitySpawnPlacementRegistry.setPlacementType(TameablePolarBear.class, SpawnPlacementType.ON_GROUND);

		// TameableSheep
		EntitySpawnPlacementRegistry.setPlacementType(TameableSheep.class, SpawnPlacementType.ON_GROUND);

		// TameablePig
		EntitySpawnPlacementRegistry.setPlacementType(TameablePig.class, SpawnPlacementType.ON_GROUND);

		// TameableCow
		EntitySpawnPlacementRegistry.setPlacementType(TameableCow.class, SpawnPlacementType.ON_GROUND);

		// TameableCreeper
		EntitySpawnPlacementRegistry.setPlacementType(TameableCreeper.class, SpawnPlacementType.ON_GROUND);

		// TameableZombie
		// EntitySpawnPlacementRegistry.setPlacementType(TameableZombie.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableRabbit
		// EntitySpawnPlacementRegistry.setPlacementType(TameableRabbit.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableEndermite
		EntitySpawnPlacementRegistry.setPlacementType(TameableEndermite.class, SpawnPlacementType.ON_GROUND);

		// TameableEnderman
		EntitySpawnPlacementRegistry.setPlacementType(TameableEnderman.class, SpawnPlacementType.ON_GROUND);

		// TameableVillager
		// EntitySpawnPlacementRegistry.setPlacementType(TameableVillager.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableSnowGolem
		// EntitySpawnPlacementRegistry.setPlacementType(TameableSnowGolem.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableIronGolem
		// EntitySpawnPlacementRegistry.setPlacementType(TameableIronGolem.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableMooshroom
		// EntitySpawnPlacementRegistry.setPlacementType(TameableMooshroom.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableSquid
		EntitySpawnPlacementRegistry.setPlacementType(TameableSquid.class, SpawnPlacementType.IN_WATER);

		// TameableSpider
		EntitySpawnPlacementRegistry.setPlacementType(TameableSpider.class, SpawnPlacementType.ON_GROUND);

		// TameableCaveSpider
		EntitySpawnPlacementRegistry.setPlacementType(TameableCaveSpider.class, SpawnPlacementType.ON_GROUND);

		// TameableGhast
		EntitySpawnPlacementRegistry.setPlacementType(TameableGhast.class, SpawnPlacementType.ON_GROUND);

		// TameableGiantZombie
		// EntitySpawnPlacementRegistry.setPlacementType(TameableGiantZombie.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableGuardian
		// EntitySpawnPlacementRegistry.setPlacementType(TameableGuardian.class,
		// SpawnPlacementType.IN_WATER);

		// TameableMagmaCube
		EntitySpawnPlacementRegistry.setPlacementType(TameableMagmaCube.class, SpawnPlacementType.ON_GROUND);

		// TameablePigZombie
		EntitySpawnPlacementRegistry.setPlacementType(TameablePigZombie.class, SpawnPlacementType.ON_GROUND);

		// TameableShulker
		EntitySpawnPlacementRegistry.setPlacementType(TameableShulker.class, SpawnPlacementType.ON_GROUND);

		// TameableSilverfish
		// EntitySpawnPlacementRegistry.setPlacementType(TameableSilverfish.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableSkeleton
		// EntitySpawnPlacementRegistry.setPlacementType(TameableSkeleton.class,
		// SpawnPlacementType.ON_GROUND);

		// TameableSlime
		EntitySpawnPlacementRegistry.setPlacementType(TameableSlime.class, SpawnPlacementType.ON_GROUND);

		// TameableWitch
		EntitySpawnPlacementRegistry.setPlacementType(TameableWitch.class, SpawnPlacementType.ON_GROUND);

		/*
		 * This is the loot table for our mob
		 */

		// TameablePolarBear
		LootTableList.register(TameablePolarBear.LOOT_POLARBEAR);

		// TameableChicken
		// LootTableList.register(TameableChicken.LOOT_CHICKEN);

	}

	private static Biome[] getMobBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass.isAssignableFrom(EntityMob.class)) {
					if (!biomes.contains(currentBiome)) {
						biomes.add(currentBiome);
					}
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getPassiveBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass.isAssignableFrom(EntityAnimal.class)) {
					if (!biomes.contains(currentBiome)) {
						biomes.add(currentBiome);
					}
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getWaterBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.WATER_CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {

			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getAmbientBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.AMBIENT);
			for (SpawnListEntry spawnEntry : spawnList) {

			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getSlimeBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.MONSTER);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntitySlime.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}
}
