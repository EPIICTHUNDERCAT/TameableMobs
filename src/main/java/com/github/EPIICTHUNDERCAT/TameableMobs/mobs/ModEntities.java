package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.epiicthundercat.tameablemobs.TameableMobs;
import com.github.epiicthundercat.tameablemobs.mobs.itementities.EntityBatPee;
import com.github.epiicthundercat.tameablemobs.mobs.itementities.EntityWitchProjectile;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
	public static void init() {
		

		// Every entity in our mod has an ID (local to this mod)

		// TameablePolarBear
		EntityRegistry.registerModEntity(TameablePolarBear.class, "TameablePolarBear", 0, TameableMobs.instance, 80, 3,
				false,  0xffffff, 0xd9d9d9);

		// TameableChicken
		EntityRegistry.registerModEntity(TameableChicken.class, "TameableChicken", 1, TameableMobs.instance, 80, 3,
				false, 0xcccccc, 0xff3300);

		// TameableBlaze
		EntityRegistry.registerModEntity(TameableBlaze.class, "TameableBlaze", 2, TameableMobs.instance, 80, 3, false,
				0xffc100, 0xd9d926);
		// TameableBat
		EntityRegistry.registerModEntity(TameableBat.class, "TameableBat", 3, TameableMobs.instance, 80, 3, false, 0x996633 ,0x000000);

		// TameableSheep
		EntityRegistry.registerModEntity(TameableSheep.class, "TameableSheep", 4, TameableMobs.instance, 80, 3, false,
				0xffffff, 0xffe6ff);
		// TameablePig
		EntityRegistry.registerModEntity(TameablePig.class, "TameablePig", 5, TameableMobs.instance, 80, 3, false, 
				0xff9999, 0xff8080);
		// TameableCow
		EntityRegistry.registerModEntity(TameableCow.class, "TameableCow", 6, TameableMobs.instance, 80, 3, false, 0x4d3319, 0x4d4d4d);
		// TameableCreeper
		EntityRegistry.registerModEntity(TameableCreeper.class, "TameableCreeper", 7, TameableMobs.instance, 80, 3,
				false, 0x19e619, 0x000000);
		// TameableZombie
		EntityRegistry.registerModEntity(TameableZombie.class, "TameableZombie", 8, TameableMobs.instance, 80, 3, false,
				0x00b3b3, 0x009900);
		// TameableEndermite
		EntityRegistry.registerModEntity(TameableEndermite.class, "TameableEndermite", 9, TameableMobs.instance, 80, 3,
				false, 0x000000, 0x595959);
		// TameableEnderman
		EntityRegistry.registerModEntity(TameableEnderman.class, "TameableEnderman", 10, TameableMobs.instance, 80, 3,
				false, 0x000000, 0x000000);

		// TameableVillager
		EntityRegistry.registerModEntity(TameableVillager.class, "TameableVillager", 11, TameableMobs.instance, 80, 3,
				false, 0x996633, 0xB28C66);

		// TameableSnowman
		EntityRegistry.registerModEntity(TameableSnowman.class, "TameableSnowman", 12, TameableMobs.instance, 80, 3,
				false, 0xFFFFFF, 0xFF9900);

		// TameableIronGolem
		EntityRegistry.registerModEntity(TameableIronGolem.class, "TameableIronGolem", 13, TameableMobs.instance, 80, 3,
				false, 0xFFFFFF, 0xB4B4B4);

		// TameableMooshroom
		EntityRegistry.registerModEntity(TameableMooshroom.class, "TameableMooshroom", 14, TameableMobs.instance, 80, 3,
				false, 0xFF3300, 0xd9d9d9);
		// TameableSquid
		EntityRegistry.registerModEntity(TameableSquid.class, "TameableSquid", 15, TameableMobs.instance, 80, 3, false,
				0x000066, 0x9999C2);

		// TameableSpider
		EntityRegistry.registerModEntity(TameableSpider.class, "TameableSpider", 16, TameableMobs.instance, 80, 3,
				false, 0x663300, 0xFF3300);

		// TameableCaveSpider
		EntityRegistry.registerModEntity(TameableCaveSpider.class, "TameableCaveSpider", 17, TameableMobs.instance, 80,
				3, false, 0x0099CC, 0xF2380D);
		// TameableGhast
		EntityRegistry.registerModEntity(TameableGhast.class, "TameableGhast", 18, TameableMobs.instance, 80, 3, false,
				0xB4B4B4, 0xd9d9d9);
		// TameableGiantZombie
		EntityRegistry.registerModEntity(TameableGiantZombie.class, "TameableGiantZombie", 19, TameableMobs.instance,
				80, 3, false, 0x00b3b3, 0x009900);
		// TameableGuardian
		EntityRegistry.registerModEntity(TameableGuardian.class, "TameableGuardian", 20, TameableMobs.instance, 80, 3,
				false, 0x599985, 0xFF9900);
		// TameableMagmaCube
		EntityRegistry.registerModEntity(TameableMagmaCube.class, "TameableMagmaCube", 21, TameableMobs.instance, 80, 3,
				false, 0x663300, 0xE6D90D);
		// TameablePigZombie
		EntityRegistry.registerModEntity(TameablePigZombie.class, "TameablePigZombie", 23, TameableMobs.instance, 80, 3,
				false, 0xff9999, 0x009900);
		// TameableShulker
		EntityRegistry.registerModEntity(TameableShulker.class, "TameableShulker", 24, TameableMobs.instance, 80, 3,
				false, 0xB24CD9, 0x660066);
		// TameableSilverfish
		EntityRegistry.registerModEntity(TameableSilverfish.class, "TameableSilverfish", 25, TameableMobs.instance, 80,
				3, false, 0x999999 , 0x333333);
		// TameableSkeleton
		EntityRegistry.registerModEntity(TameableSkeleton.class, "TameableSkeleton", 26, TameableMobs.instance, 80, 3,
				false, 0xf2f2f2  , 0x333333);
		// TameableSlime
		EntityRegistry.registerModEntity(TameableSlime.class, "TameableSlime", 27, TameableMobs.instance, 80, 3, false,
				0x2eb82e , 0x4dff4d);
		// TameableWitch
		EntityRegistry.registerModEntity(TameableWitch.class, "TameableWitch", 28, TameableMobs.instance, 80, 3, false,
				0x155115,	0x33cc33);
		// TameableRabbit
		EntityRegistry.registerModEntity(TameableRabbit.class, "TameableRabbit", 30, TameableMobs.instance, 80, 3,
				false, 0xac7339, 0x604020);
		// Bug
		EntityRegistry.registerModEntity(TMBug.class, "Bug", 31, TameableMobs.instance, 80, 3, false, 0xff3300, 0x404040);

		// WitchProjectile
		EntityRegistry.registerModEntity(EntityWitchProjectile.class, "WitchProjectile", 29, TameableMobs.instance, 64,
				2, true);
		// BatPee
		EntityRegistry.registerModEntity(EntityBatPee.class, "BatPee", 1, TameableMobs.instance, 64, 2, true);

		/*
		 * We want our mob to spawn in Plains and ice plains biomes. If you
		 * don't add this then it will not spawn automatically but you can of
		 * course still make it spawn manually
		 */

		// TameablePolarBear
		EntityRegistry.addSpawn(TameablePolarBear.class, 2, 3, 8, EnumCreatureType.CREATURE, Biomes.ICE_PLAINS,
				Biomes.ICE_MOUNTAINS, Biomes.MUTATED_ICE_FLATS);
		// TameableChicken
		EntityRegistry.addSpawn(TameableChicken.class, 2, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());

		// TameableBlaze
		EntityRegistry.addSpawn(TameableBlaze.class, 1, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableBat
		EntityRegistry.addSpawn(TameableBat.class, 1, 8, 8, EnumCreatureType.AMBIENT, getPassiveBiomeList());

		// TameableSheep
		EntityRegistry.addSpawn(TameableSheep.class, 2, 5, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());

		// TameablePig
		EntityRegistry.addSpawn(TameablePig.class, 2, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());
		// TameablePigZombie
		EntityRegistry.addSpawn(TameablePigZombie.class, 1, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableCreeper
		EntityRegistry.addSpawn(TameableCreeper.class, 1, 2, 4, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableCow
		EntityRegistry.addSpawn(TameableCow.class, 1, 4, 8, EnumCreatureType.CREATURE, getPassiveBiomeList());
		// TameableEndermite
		EntityRegistry.addSpawn(TameableEndermite.class, 1, 1, 1, EnumCreatureType.MONSTER);
		// TameableEnderman
		EntityRegistry.addSpawn(TameableEnderman.class, 1, 1, 5, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableSpider
		EntityRegistry.addSpawn(TameableSpider.class, 1, 4, 4, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableCaveSpider
		EntityRegistry.addSpawn(TameableCaveSpider.class, 1, 2, 4, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableWitch
		EntityRegistry.addSpawn(TameableWitch.class, 1, 1, 1, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableSquid
		EntityRegistry.addSpawn(TameableSquid.class, 1, 4, 4, EnumCreatureType.WATER_CREATURE, Biomes.OCEAN,
				Biomes.DEEP_OCEAN, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
		// TameableGhast
		EntityRegistry.addSpawn(TameableGhast.class, 1, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableShulker
		EntityRegistry.addSpawn(TameableShulker.class, 1, 1, 4, EnumCreatureType.MONSTER, Biomes.SKY);
		// TameableMagmaCube
		EntityRegistry.addSpawn(TameableMagmaCube.class, 1, 3, 8, EnumCreatureType.MONSTER, Biomes.HELL);
		// TameableSlime
		EntityRegistry.addSpawn(TameableSlime.class, 1, 3, 8, EnumCreatureType.MONSTER, getSlimeBiomeList());
		// TameableMooshroom
		EntityRegistry.addSpawn(TameableMooshroom.class, 2, 3, 8, EnumCreatureType.CREATURE, getMooshroomBiomeList());
		// TameableRabbit
		EntityRegistry.addSpawn(TameableRabbit.class, 1, 3, 8, EnumCreatureType.CREATURE, getRabbitBiomeList());
		// TameableGuardian
		EntityRegistry.addSpawn(TameableGuardian.class, 1, 3, 8, EnumCreatureType.MONSTER, getGuardianBiomeList());
		// TameableSilverfish
		EntityRegistry.addSpawn(TameableSilverfish.class, 1, 3, 8, EnumCreatureType.MONSTER, getSilverfishBiomeList());
		// TameableGiantZombie
		EntityRegistry.addSpawn(TameableGiantZombie.class, 0, 1, 1, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableZombie
		EntityRegistry.addSpawn(TameableZombie.class, 1, 1, 1, EnumCreatureType.MONSTER, getMobBiomeList());

		// TameableIronGolem
		EntityRegistry.addSpawn(TameableIronGolem.class, 1, 3, 8, EnumCreatureType.MONSTER, getIronGolemBiomeList());
		// Bug
		EntityRegistry.addSpawn(TMBug.class, 2, 5, 10, EnumCreatureType.AMBIENT, getBugBiomeList());
		// TameableSkeleton
		EntityRegistry.addSpawn(TameableSkeleton.class, 1, 3, 8, EnumCreatureType.MONSTER, getMobBiomeList());
		// TameableVillager
		EntityRegistry.addSpawn(TameableVillager.class, 1, 3, 8, EnumCreatureType.CREATURE, getVillagerBiomeList());

		// TameableSnowman
		EntityRegistry.addSpawn(TameableSnowman.class, 1, 3, 8, EnumCreatureType.MONSTER, Biomes.ICE_MOUNTAINS,
				Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS,
				Biomes.MUTATED_TAIGA_COLD);

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
		EntitySpawnPlacementRegistry.setPlacementType(TameableZombie.class, SpawnPlacementType.ON_GROUND);

		// TameableRabbit
		EntitySpawnPlacementRegistry.setPlacementType(TameableRabbit.class, SpawnPlacementType.ON_GROUND);

		// TameableEndermite
		EntitySpawnPlacementRegistry.setPlacementType(TameableEndermite.class, SpawnPlacementType.ON_GROUND);

		// TameableEnderman
		EntitySpawnPlacementRegistry.setPlacementType(TameableEnderman.class, SpawnPlacementType.ON_GROUND);

		// TameableVillager
		EntitySpawnPlacementRegistry.setPlacementType(TameableVillager.class, SpawnPlacementType.ON_GROUND);

		// TameableSnowman
		EntitySpawnPlacementRegistry.setPlacementType(TameableSnowman.class, SpawnPlacementType.ON_GROUND);

		// TameableIronGolem
		EntitySpawnPlacementRegistry.setPlacementType(TameableIronGolem.class, SpawnPlacementType.ON_GROUND);

		// TameableMooshroom
		EntitySpawnPlacementRegistry.setPlacementType(TameableMooshroom.class, SpawnPlacementType.ON_GROUND);

		// TameableSquid
		EntitySpawnPlacementRegistry.setPlacementType(TameableSquid.class, SpawnPlacementType.IN_WATER);

		// TameableSpider
		EntitySpawnPlacementRegistry.setPlacementType(TameableSpider.class, SpawnPlacementType.ON_GROUND);

		// TameableCaveSpider
		EntitySpawnPlacementRegistry.setPlacementType(TameableCaveSpider.class, SpawnPlacementType.ON_GROUND);

		// TameableGhast
		EntitySpawnPlacementRegistry.setPlacementType(TameableGhast.class, SpawnPlacementType.ON_GROUND);

		// TameableGiantZombie
		EntitySpawnPlacementRegistry.setPlacementType(TameableGiantZombie.class, SpawnPlacementType.ON_GROUND);

		// TameableGuardian
		EntitySpawnPlacementRegistry.setPlacementType(TameableGuardian.class, SpawnPlacementType.IN_WATER);

		// TameableMagmaCube
		EntitySpawnPlacementRegistry.setPlacementType(TameableMagmaCube.class, SpawnPlacementType.ON_GROUND);

		// TameablePigZombie
		EntitySpawnPlacementRegistry.setPlacementType(TameablePigZombie.class, SpawnPlacementType.ON_GROUND);

		// TameableShulker
		EntitySpawnPlacementRegistry.setPlacementType(TameableShulker.class, SpawnPlacementType.ON_GROUND);

		// TameableSilverfish
		EntitySpawnPlacementRegistry.setPlacementType(TameableSilverfish.class, SpawnPlacementType.ON_GROUND);

		// TameableSkeleton
		EntitySpawnPlacementRegistry.setPlacementType(TameableSkeleton.class, SpawnPlacementType.ON_GROUND);

		// TameableSlime
		EntitySpawnPlacementRegistry.setPlacementType(TameableSlime.class, SpawnPlacementType.ON_GROUND);

		// TameableWitch
		EntitySpawnPlacementRegistry.setPlacementType(TameableWitch.class, SpawnPlacementType.ON_GROUND);

		// Bug
		EntitySpawnPlacementRegistry.setPlacementType(TMBug.class, SpawnPlacementType.ON_GROUND);

		/*
		 * This is the loot table for our mob
		 */

		// TameablePolarBear
		LootTableList.register(TameablePolarBear.LOOT_POLARBEAR);

		// BUG
		LootTableList.register(TMBug.LOOT_BUG);

	}

	private static Biome[] getPassiveBiomeList() {
	
			List<Biome> biomes = new ArrayList<Biome>();
			Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
			while (biomeList.hasNext()) {
				Biome currentBiome = biomeList.next();
				List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
				for (SpawnListEntry spawnEntry : spawnList) {
					if (spawnEntry.entityClass == EntityCow.class) {
						biomes.add(currentBiome);
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

	private static Biome[] getMooshroomBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityMooshroom.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);

	}

	private static Biome[] getGuardianBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.MONSTER);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityGuardian.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getRabbitBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityRabbit.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getSilverfishBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.MONSTER);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntitySilverfish.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getMobBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.MONSTER);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityZombie.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getIronGolemBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityIronGolem.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getBugBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.AMBIENT);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityChicken.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

	private static Biome[] getVillagerBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		Iterator<Biome> biomeList = Biome.REGISTRY.iterator();
		while (biomeList.hasNext()) {
			Biome currentBiome = biomeList.next();
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.CREATURE);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityVillager.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}
}
