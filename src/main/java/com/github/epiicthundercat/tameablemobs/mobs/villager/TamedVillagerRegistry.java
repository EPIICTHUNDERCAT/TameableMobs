package com.github.epiicthundercat.tameablemobs.mobs.villager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import com.github.epiicthundercat.tameablemobs.mobs.TameableVillager;
import com.github.epiicthundercat.tameablemobs.mobs.TameableVillager.ITradeList;
import com.github.epiicthundercat.tameablemobs.mobs.TameableZombie;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.monster.ZombieType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

public class TamedVillagerRegistry {
	
	    public static final ResourceLocation PROFESSIONS = new ResourceLocation("minecraft:TamedVillagerProfessions");
	    private static final TamedVillagerRegistry INSTANCE = new TamedVillagerRegistry();

	    private Map<Class<?>, IVillageCreationHandler> villageCreationHandlers = Maps.newHashMap();

	    private TamedVillagerRegistry()
	    {
	        init();
	    }

	    /**
	     * Allow access to the {@link net.minecraft.world.gen.structure.StructureVillagePieces} array controlling new village
	     * creation so you can insert your own new village pieces
	     *
	     * @author cpw
	     */
	    public interface IVillageCreationHandler
	    {
	        /**
	         * Called when {@link net.minecraft.world.gen.structure.MapGenVillage} is creating a new village
	         *
	         * @param random
	         * @param i
	         */
	        StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i);

	        /**
	         * The class of the root structure component to add to the village
	         */
	        Class<?> getComponentClass();


	        /**
	         * Build an instance of the village component {@link net.minecraft.world.gen.structure.StructureVillagePieces}
	         *
	         * @param villagePiece
	         * @param startPiece
	         * @param pieces
	         * @param random
	         * @param p1
	         * @param p2
	         * @param p3
	         * @param facing
	         * @param p5
	         */
	        Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1,
	                               int p2, int p3, EnumFacing facing, int p5);
	    }

	    public static TamedVillagerRegistry instance()
	    {
	        return INSTANCE;
	    }

	    /**
	     * Register a new village creation handler
	     *
	     * @param handler
	     */
	    public void registerVillageCreationHandler(IVillageCreationHandler handler)
	    {
	        villageCreationHandlers.put(handler.getComponentClass(), handler);
	    }

	    public static void addExtraVillageComponents(List<PieceWeight> list, Random random, int i)
	    {
	        List<StructureVillagePieces.PieceWeight> parts = list;
	        for (IVillageCreationHandler handler : instance().villageCreationHandlers.values())
	        {
	            parts.add(handler.getVillagePieceWeight(random, i));
	        }
	    }

	    public static Village getVillageComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random,
	                                              int p1, int p2, int p3, EnumFacing facing, int p5)
	    {
	        return instance().villageCreationHandlers.get(villagePiece.villagePieceClass).buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, facing, p5);
	    }

	    public void register(TamedVillagerProfession prof)
	    {
	        register(prof, -1);
	    }

	    private void register(TamedVillagerProfession prof, int id)
	    {
	        professions.register(id, prof.name, prof);
	    }

	    private boolean hasInit = false;
	    private FMLControlledNamespacedRegistry<TamedVillagerProfession> professions = PersistentRegistryManager.createRegistry(PROFESSIONS, TamedVillagerProfession.class, null, 0, 1024, true, null, null, null, null);
	    public IForgeRegistry<TamedVillagerProfession> getRegistry() { return this.professions; }


	    private void init()
	    {
	        if (hasInit)
	        {
	            return;
	        }

	        TamedVillagerProfession prof = new TamedVillagerProfession("minecraft:farmer",
	                "minecraft:textures/entity/villager/farmer.png",
	                "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
	        {
	            register(prof, 0);
	            (new TamedVillagerCareer(prof, "farmer")).init(VanillaTrades.trades[0][0]);
	            (new TamedVillagerCareer(prof, "fisherman")).init(VanillaTrades.trades[0][1]);
	            (new TamedVillagerCareer(prof, "shepherd")).init(VanillaTrades.trades[0][2]);
	            (new TamedVillagerCareer(prof, "fletcher")).init(VanillaTrades.trades[0][3]);
	        }
	        prof = new TamedVillagerProfession("minecraft:librarian",
	                "minecraft:textures/entity/villager/librarian.png",
	                "minecraft:textures/entity/zombie_villager/zombie_librarian.png");
	        {
	            register(prof, 1);
	            (new TamedVillagerCareer(prof, "librarian")).init(VanillaTrades.trades[1][0]);
	        }
	        prof = new TamedVillagerProfession("minecraft:priest",
	                "minecraft:textures/entity/villager/priest.png",
	                "minecraft:textures/entity/zombie_villager/zombie_priest.png");
	        {
	            register(prof, 2);
	            (new TamedVillagerCareer(prof, "cleric")).init(VanillaTrades.trades[2][0]);
	        }
	        prof = new TamedVillagerProfession("minecraft:smith",
	                "minecraft:textures/entity/villager/smith.png",
	                "minecraft:textures/entity/zombie_villager/zombie_smith.png");
	        {
	            register(prof, 3);
	            (new TamedVillagerCareer(prof, "armor")).init(VanillaTrades.trades[3][0]);
	            (new TamedVillagerCareer(prof, "weapon")).init(VanillaTrades.trades[3][1]);
	            (new TamedVillagerCareer(prof, "tool")).init(VanillaTrades.trades[3][2]);
	        }
	        prof = new TamedVillagerProfession("minecraft:butcher",
	                "minecraft:textures/entity/villager/butcher.png",
	                "minecraft:textures/entity/zombie_villager/zombie_butcher.png");
	        {
	            register(prof, 4);
	            (new TamedVillagerCareer(prof, "butcher")).init(VanillaTrades.trades[4][0]);
	            (new TamedVillagerCareer(prof, "leather")).init(VanillaTrades.trades[4][1]);
	        }
	    }

	    public static class TamedVillagerProfession extends IForgeRegistryEntry.Impl<TamedVillagerProfession>
	    {
	        private ResourceLocation name;
	        private ResourceLocation texture;
	        private ResourceLocation zombie;
	        private List<TamedVillagerCareer> careers = Lists.newArrayList();

	        @Deprecated //Use Zombie texture
	        public TamedVillagerProfession(String name, String texture)
	        {
	            this (name, texture, "minecraft:textures/entity/zombie_villager/zombie_villager.png");
	        }
	        public TamedVillagerProfession(String name, String texture, String zombie)
	        {
	            this.name = new ResourceLocation(name);
	            this.texture = new ResourceLocation(texture);
	            this.zombie = new ResourceLocation(zombie);
	            this.setRegistryName(this.name);
	        }

	        private void register(TamedVillagerCareer career)
	        {
	            Validate.isTrue(!careers.contains(career), "Attempted to register career that is already registered.");
	            Validate.isTrue(career.profession == this, "Attempted to register career for the wrong profession.");
	            career.id = careers.size();
	            careers.add(career);
	        }

	        public ResourceLocation getSkin() { return this.texture; }
	        public ResourceLocation getZombieSkin() { return this.zombie; }
	        public TamedVillagerCareer getCareer(int id)
	        {
	            for (TamedVillagerCareer car : this.careers)
	            {
	                if (car.id == id)
	                    return car;
	            }
	            return this.careers.get(0);
	        }

	        public int getRandomCareer(Random rand)
	        {
	            return this.careers.get(rand.nextInt(this.careers.size())).id;
	        }
	    }

	    public static class TamedVillagerCareer
	    {
	        private TamedVillagerProfession profession;
	        private String name;
	        private int id;
	        private List<List<ITradeList>> trades = Lists.newArrayList();

	        public TamedVillagerCareer(TamedVillagerProfession parent, String name)
	        {
	            this.profession = parent;
	            this.name = name;
	            parent.register(this);
	        }

	        public String getName()
	        {
	            return this.name;
	        }


	        public TamedVillagerCareer addTrade(int level, ITradeList... trades)
	        {
	            if (level <= 0)
	                throw new IllegalArgumentException("Levels start at 1");

	            List<ITradeList> levelTrades = level <= this.trades.size() ? this.trades.get(level - 1) : null;
	            if (levelTrades == null)
	            {
	                while (this.trades.size() < level)
	                {
	                    levelTrades = Lists.newArrayList();
	                    this.trades.add(levelTrades);
	                }
	            }
	            if (levelTrades == null) //Not sure how this could happen, but screw it
	            {
	                levelTrades = Lists.newArrayList();
	                this.trades.set(level - 1, levelTrades);
	            }
	            for (ITradeList t : trades)
	                levelTrades.add(t);
	            return this;
	        }


	        public List<ITradeList> getTrades(int level)
	        {
	            return level >= 0 && level < this.trades.size() ? Collections.unmodifiableList(this.trades.get(level)) : null;
	        }
	        private TamedVillagerCareer init(TameableVillager.ITradeList[][] trades)
	        {
	            for (int x = 0; x < trades.length; x++)
	                this.trades.add(Lists.newArrayList(trades[x]));
	            return this;
	        }

	        @Override
	        public boolean equals(Object o)
	        {
	            if (o == this)
	            {
	                return true;
	            }
	            if (!(o instanceof TamedVillagerCareer))
	            {
	                return false;
	            }
	            TamedVillagerCareer oc = (TamedVillagerCareer)o;
	            return name.equals(oc.name) && profession == oc.profession;
	        }
	    }

	    /**
	     * Hook called when spawning a Villager, sets it's profession to a random registered profession.
	     *
	     * @param entity The new entity
	     * @param rand   The world's RNG
	     */
	    public static void setRandomProfession(TameableVillager entity, Random rand)
	    {
	        List<TamedVillagerProfession> entries = INSTANCE.professions.getValues();
	        entity.setProfession(entries.get(rand.nextInt(entries.size())));
	    }
	    public static void setRandomProfession(TameableZombie entity, Random rand)
	    {
	        List<TamedVillagerProfession> entries = INSTANCE.professions.getValues();
	        entity.setTheVillagerType(entries.get(rand.nextInt(entries.size())));
	    }







	    //Below this is INTERNAL USE ONLY DO NOT USE MODDERS
	    public static void onSetProfession(TameableVillager entity, TamedVillagerProfession prof)
	    {
	        int network = INSTANCE.professions.getId(prof);
	        if (network == -1 || prof != INSTANCE.professions.getObjectById(network))
	        {
	            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
	        }

	        if (network != entity.getProfession())
	            entity.setProfession(network);
	    }
	    public static void onSetProfession(TameableVillager entity, int network)
	    {
	        TamedVillagerProfession prof = INSTANCE.professions.getObjectById(network);
	        if (prof == null || INSTANCE.professions.getId(prof) != network)
	        {
	            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
	        }

	        if (prof != entity.getProfessionForge())
	            entity.setProfession(prof);
	    }

	    @SuppressWarnings("deprecation")
	    public static void onSetProfession(TameableZombie entity, TamedVillagerProfession prof)
	    {
	        if (prof == null)
	        {
	            if (entity.getZombieType() != ZombieType.NORMAL && entity.getZombieType() != ZombieType.HUSK)
	                entity.setZombieType(ZombieType.NORMAL);
	            return;
	        }

	        int network = INSTANCE.professions.getId(prof);
	        if (network == -1 || prof != INSTANCE.professions.getObjectById(network))
	        {
	            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
	        }

	        if (network >= 0 && network < 5) // Vanilla
	        {
	            if (entity.getZombieType() == null || entity.getZombieType().getId() != network + 1)
	            {
	                entity.setZombieType(ZombieType.getVillagerByOrdinal(network));
	            }
	        }
	        else if (entity.getZombieType() != null)
	            entity.setZombieType(ZombieType.NORMAL);
	    }
	    public static void onSetProfession(TameableZombie entity, ZombieType type, int network)
	    {
	        if (type == ZombieType.NORMAL || type == ZombieType.HUSK)
	        {
	            if (entity.getTheVillagerTypeForge() != null)
	                entity.setTheVillagerType(null);
	            return;
	        }
	        int realID = network - 1;
	        if (type == null) //Forge type?
	            realID = network * -1; // Forge encoded as -ID
	        TamedVillagerProfession prof = INSTANCE.professions.getObjectById(realID);
	        if (prof == null && network != 0 || INSTANCE.professions.getId(prof) != realID)
	        {
	            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + realID + " " + prof);
	        }

	        if (prof != entity.getTheVillagerTypeForge())
	            entity.setTheVillagerType(prof);
	    }

	    @Deprecated public static TamedVillagerProfession getById(int network){ return INSTANCE.professions.getObjectById(network); }
	    @Deprecated public static int getId(TamedVillagerProfession prof){ return INSTANCE.professions.getId(prof); }

	    //TODO: Figure out a good generic system for this. Put on hold for Patches.

	    private static class VanillaTrades
	    {
	        //This field is moved from TameableVillager over to here.
	        //Moved to inner class to stop static initializer issues.
	        //It is nasty I know but it's vanilla.
	        private static final ITradeList[][][][] trades = TameableVillager.GET_TRADES_DONT_USE();
	    }
	}

