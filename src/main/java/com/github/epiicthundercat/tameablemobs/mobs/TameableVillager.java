package com.github.epiicthundercat.tameablemobs.mobs;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.epiicthundercat.tameablemobs.mobs.villager.TamedVillagerRegistry;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableVillager extends EntityAgeable implements IEntityOwnable, IMerchant, INpc {

	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager
			.<Float>createKey(TameableVillager.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(TameableVillager.class,
			DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(TameableVillager.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager
			.<Optional<UUID>>createKey(TameableVillager.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private static final DataParameter<Integer> PROFESSION = EntityDataManager
			.<Integer>createKey(TameableVillager.class, DataSerializers.VARINT);
	private static final DataParameter<String> PROFESSION_STR = EntityDataManager
			.<String>createKey(TameableVillager.class, DataSerializers.STRING);
	private int randomTickDivider;
	private boolean isMating;
	private boolean isPlaying;
	Village villageObj;
	/** This villager's current customer. */
	private EntityPlayer buyingPlayer;
	/** Initialises the MerchantRecipeList.java */
	private MerchantRecipeList buyingList;
	private int timeUntilReset;
	/** addDefaultEquipmentAndRecipies is called if this is true */
	private boolean needsInitilization;
	private boolean isWillingToMate;
	private int wealth;
	/** Last player to trade with this villager, used for aggressivity. */
	private String lastBuyingPlayer;
	private int careerId;
	/** This is the TameableVillager's career level value */
	private int careerLevel;
	private boolean isLookingForHome;
	private boolean areAdditionalTasksSet;
	private final InventoryBasic villagerInventory;
	protected EntityAISit aiSit;
	/**
	 * A multi-dimensional array mapping the various professions, careers and
	 * career levels that a Villager may offer
	 */
	@Deprecated // Use TamedVillagerRegistry
	private static final TameableVillager.ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new TameableVillager.ITradeList[][][][] {
			{ { { new TameableVillager.EmeraldForItems(Items.WHEAT, new TameableVillager.PriceInfo(18, 22)),
					new TameableVillager.EmeraldForItems(Items.POTATO, new TameableVillager.PriceInfo(15, 19)),
					new TameableVillager.EmeraldForItems(Items.CARROT, new TameableVillager.PriceInfo(15, 19)),
					new TameableVillager.ListItemForEmeralds(Items.BREAD, new TameableVillager.PriceInfo(-4, -2)) },
					{
							new TameableVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.PUMPKIN),
									new TameableVillager.PriceInfo(8, 13)),
							new TameableVillager.ListItemForEmeralds(Items.PUMPKIN_PIE,
									new TameableVillager.PriceInfo(-3, -2)) },
					{ new TameableVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.MELON_BLOCK),
							new TameableVillager.PriceInfo(7, 12)),
							new TameableVillager.ListItemForEmeralds(Items.APPLE,
									new TameableVillager.PriceInfo(-5, -7)) },
					{ new TameableVillager.ListItemForEmeralds(Items.COOKIE, new TameableVillager.PriceInfo(-6, -10)),
							new TameableVillager.ListItemForEmeralds(Items.CAKE,
									new TameableVillager.PriceInfo(1, 1)) } },
					{ { new TameableVillager.EmeraldForItems(Items.STRING, new TameableVillager.PriceInfo(15, 20)),
							new TameableVillager.EmeraldForItems(Items.COAL, new TameableVillager.PriceInfo(16, 24)),
							new TameableVillager.ItemAndEmeraldToItem(Items.FISH, new TameableVillager.PriceInfo(6, 6),
									Items.COOKED_FISH, new TameableVillager.PriceInfo(6, 6)) },
							{ new TameableVillager.ListEnchantedItemForEmeralds(Items.FISHING_ROD,
									new TameableVillager.PriceInfo(7, 8)) } },
					{ { new TameableVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.WOOL),
							new TameableVillager.PriceInfo(16, 22)),
							new TameableVillager.ListItemForEmeralds(Items.SHEARS,
									new TameableVillager.PriceInfo(3, 4)) },
							{ new TameableVillager.ListItemForEmeralds(
									new ItemStack(Item.getItemFromBlock(Blocks.WOOL)),
									new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 1),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 2),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 3),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 4),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 5),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 6),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 7),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 8),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 9),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 10),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 11),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 12),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 13),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 14),
											new TameableVillager.PriceInfo(1, 2)),
									new TameableVillager.ListItemForEmeralds(
											new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 15),
											new TameableVillager.PriceInfo(1, 2)) } },
					{ { new TameableVillager.EmeraldForItems(Items.STRING, new TameableVillager.PriceInfo(15, 20)),
							new TameableVillager.ListItemForEmeralds(Items.ARROW,
									new TameableVillager.PriceInfo(-12, -8)) },
							{ new TameableVillager.ListItemForEmeralds(Items.BOW, new TameableVillager.PriceInfo(2, 3)),
									new TameableVillager.ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.GRAVEL),
											new TameableVillager.PriceInfo(10, 10), Items.FLINT,
											new TameableVillager.PriceInfo(6, 10)) } } },
			{ { { new TameableVillager.EmeraldForItems(Items.PAPER, new TameableVillager.PriceInfo(24, 36)),
					new TameableVillager.ListEnchantedBookForEmeralds() },
					{ new TameableVillager.EmeraldForItems(Items.BOOK, new TameableVillager.PriceInfo(8, 10)),
							new TameableVillager.ListItemForEmeralds(Items.COMPASS,
									new TameableVillager.PriceInfo(10, 12)),
							new TameableVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.BOOKSHELF),
									new TameableVillager.PriceInfo(3, 4)) },
					{ new TameableVillager.EmeraldForItems(Items.WRITTEN_BOOK, new TameableVillager.PriceInfo(2, 2)),
							new TameableVillager.ListItemForEmeralds(Items.CLOCK,
									new TameableVillager.PriceInfo(10, 12)),
							new TameableVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.GLASS),
									new TameableVillager.PriceInfo(-5, -3)) },
					{ new TameableVillager.ListEnchantedBookForEmeralds() },
					{ new TameableVillager.ListEnchantedBookForEmeralds() },
					{ new TameableVillager.ListItemForEmeralds(Items.NAME_TAG,
							new TameableVillager.PriceInfo(20, 22)) } } },
			{ { { new TameableVillager.EmeraldForItems(Items.ROTTEN_FLESH, new TameableVillager.PriceInfo(36, 40)),
					new TameableVillager.EmeraldForItems(Items.GOLD_INGOT, new TameableVillager.PriceInfo(8, 10)) },
					{ new TameableVillager.ListItemForEmeralds(Items.REDSTONE, new TameableVillager.PriceInfo(-4, -1)),
							new TameableVillager.ListItemForEmeralds(
									new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
									new TameableVillager.PriceInfo(-2, -1)) },
					{ new TameableVillager.ListItemForEmeralds(Items.ENDER_PEARL, new TameableVillager.PriceInfo(4, 7)),
							new TameableVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.GLOWSTONE),
									new TameableVillager.PriceInfo(-3, -1)) },
					{ new TameableVillager.ListItemForEmeralds(Items.EXPERIENCE_BOTTLE,
							new TameableVillager.PriceInfo(3, 11)) } } },
			{ { { new TameableVillager.EmeraldForItems(Items.COAL, new TameableVillager.PriceInfo(16, 24)),
					new TameableVillager.ListItemForEmeralds(Items.IRON_HELMET, new TameableVillager.PriceInfo(4, 6)) },
					{ new TameableVillager.EmeraldForItems(Items.IRON_INGOT, new TameableVillager.PriceInfo(7, 9)),
							new TameableVillager.ListItemForEmeralds(Items.IRON_CHESTPLATE,
									new TameableVillager.PriceInfo(10, 14)) },
					{ new TameableVillager.EmeraldForItems(Items.DIAMOND, new TameableVillager.PriceInfo(3, 4)),
							new TameableVillager.ListEnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE,
									new TameableVillager.PriceInfo(16, 19)) },
					{ new TameableVillager.ListItemForEmeralds(Items.CHAINMAIL_BOOTS,
							new TameableVillager.PriceInfo(5, 7)),
							new TameableVillager.ListItemForEmeralds(Items.CHAINMAIL_LEGGINGS,
									new TameableVillager.PriceInfo(9, 11)),
							new TameableVillager.ListItemForEmeralds(Items.CHAINMAIL_HELMET,
									new TameableVillager.PriceInfo(5, 7)),
							new TameableVillager.ListItemForEmeralds(Items.CHAINMAIL_CHESTPLATE,
									new TameableVillager.PriceInfo(11, 15)) } },
					{ { new TameableVillager.EmeraldForItems(Items.COAL, new TameableVillager.PriceInfo(16, 24)),
							new TameableVillager.ListItemForEmeralds(Items.IRON_AXE,
									new TameableVillager.PriceInfo(6, 8)) },
							{ new TameableVillager.EmeraldForItems(Items.IRON_INGOT,
									new TameableVillager.PriceInfo(7, 9)),
									new TameableVillager.ListEnchantedItemForEmeralds(Items.IRON_SWORD,
											new TameableVillager.PriceInfo(9, 10)) },
							{ new TameableVillager.EmeraldForItems(Items.DIAMOND, new TameableVillager.PriceInfo(3, 4)),
									new TameableVillager.ListEnchantedItemForEmeralds(Items.DIAMOND_SWORD,
											new TameableVillager.PriceInfo(12, 15)),
									new TameableVillager.ListEnchantedItemForEmeralds(Items.DIAMOND_AXE,
											new TameableVillager.PriceInfo(9, 12)) } },
					{ { new TameableVillager.EmeraldForItems(Items.COAL, new TameableVillager.PriceInfo(16, 24)),
							new TameableVillager.ListEnchantedItemForEmeralds(Items.IRON_SHOVEL,
									new TameableVillager.PriceInfo(5, 7)) },
							{ new TameableVillager.EmeraldForItems(Items.IRON_INGOT,
									new TameableVillager.PriceInfo(7, 9)),
									new TameableVillager.ListEnchantedItemForEmeralds(Items.IRON_PICKAXE,
											new TameableVillager.PriceInfo(9, 11)) },
							{ new TameableVillager.EmeraldForItems(Items.DIAMOND, new TameableVillager.PriceInfo(3, 4)),
									new TameableVillager.ListEnchantedItemForEmeralds(Items.DIAMOND_PICKAXE,
											new TameableVillager.PriceInfo(12, 15)) } } },
			{ { { new TameableVillager.EmeraldForItems(Items.PORKCHOP, new TameableVillager.PriceInfo(14, 18)),
					new TameableVillager.EmeraldForItems(Items.CHICKEN, new TameableVillager.PriceInfo(14, 18)) },
					{ new TameableVillager.EmeraldForItems(Items.COAL, new TameableVillager.PriceInfo(16, 24)),
							new TameableVillager.ListItemForEmeralds(Items.COOKED_PORKCHOP,
									new TameableVillager.PriceInfo(-7, -5)),
							new TameableVillager.ListItemForEmeralds(Items.COOKED_CHICKEN,
									new TameableVillager.PriceInfo(-8, -6)) } },
					{ { new TameableVillager.EmeraldForItems(Items.LEATHER, new TameableVillager.PriceInfo(9, 12)),
							new TameableVillager.ListItemForEmeralds(Items.LEATHER_LEGGINGS,
									new TameableVillager.PriceInfo(2, 4)) },
							{ new TameableVillager.ListEnchantedItemForEmeralds(Items.LEATHER_CHESTPLATE,
									new TameableVillager.PriceInfo(7, 12)) },
							{ new TameableVillager.ListItemForEmeralds(Items.SADDLE,
									new TameableVillager.PriceInfo(8, 10)) } } } };

	public TameableVillager(World worldIn) {
		this(worldIn, 0);
	}

	public TameableVillager(World worldIn, int professionId) {
		super(worldIn);
		setTamed(false);
		this.villagerInventory = new InventoryBasic("Items", false, 8);
		this.setProfession(professionId);
		this.setSize(0.6F, 1.95F);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
		tasks.addTask(1, new EntityAITradePlayer(this));
		tasks.addTask(1, new EntityAILookAtTradePlayer(this));
		tasks.addTask(2, new EntityAIMoveIndoors(this));
		tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
		tasks.addTask(6, new EntityAIVillagerMate(this));
		tasks.addTask(7, new EntityAIFollowGolem(this));
		tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(9, new EntityAIVillagerInteract(this));
		tasks.addTask(9, new EntityAIWander(this, 0.6D));
		tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		aiSit = new TameableVillager.EntityAISit(this);
		tasks.addTask(1, aiSit);
		tasks.addTask(5, new EntityAIFollowOwner(this, 2.0D, 5.0F, 2.0F));
		tasks.addTask(2, new TameableVillager.AIMeleeAttack(this, 1.0D, false));

		tasks.addTask(7, new EntityAIWander(this, 1.0D));

		tasks.addTask(8, new TameableVillager.EntityAIBeg(this, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(3, new TameableVillager.AIFindPlayer(this));
		targetTasks.addTask(4, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
			getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		} else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		}

		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(PROFESSION, Integer.valueOf(0));
		dataManager.register(PROFESSION_STR, "minecraft:farmer");
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
		compound.setInteger("Profession", this.getProfession());
		compound.setString("ProfessionName", this.getProfessionForge().getRegistryName().toString());
		compound.setInteger("Riches", this.wealth);
		compound.setInteger("Career", this.careerId);
		compound.setInteger("CareerLevel", this.careerLevel);
		compound.setBoolean("Willing", this.isWillingToMate);

		if (this.buyingList != null) {
			compound.setTag("Offers", this.buyingList.getRecipiesAsTags());
		}

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

			if (itemstack != null) {
				nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
			}
		}

		compound.setTag("Inventory", nbttaglist);
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
		this.setProfession(compound.getInteger("Profession"));
		if (compound.hasKey("ProfessionName")) {
			TamedVillagerRegistry.TamedVillagerProfession p = TamedVillagerRegistry.instance().getRegistry()
					.getValue(new net.minecraft.util.ResourceLocation(compound.getString("ProfessionName")));
			if (p == null)
				p = TamedVillagerRegistry.instance().getRegistry()
						.getValue(new net.minecraft.util.ResourceLocation("minecraft:farmer"));
			this.setProfession(p);
		}
		this.wealth = compound.getInteger("Riches");
		this.careerId = compound.getInteger("Career");
		this.careerLevel = compound.getInteger("CareerLevel");
		this.isWillingToMate = compound.getBoolean("Willing");

		if (compound.hasKey("Offers", 10)) {
			NBTTagCompound nbttagcompound = compound.getCompoundTag("Offers");
			this.buyingList = new MerchantRecipeList(nbttagcompound);
		}

		NBTTagList nbttaglist = compound.getTagList("Inventory", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i));

			if (itemstack != null) {
				this.villagerInventory.addItem(itemstack);
			}
		}

		this.setCanPickUpLoot(true);
		this.setAdditionalAItasks();
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
		return stack == null ? false : stack.getItem() == Items.EMERALD;
	}

	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		boolean flag = stack != null && stack.getItem() == Items.SPAWN_EGG;

		if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
			if (!this.worldObj.isRemote && (this.buyingList == null || !this.buyingList.isEmpty())) {
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
			}

			player.addStat(StatList.TALKED_TO_VILLAGER);
			return true;
		} else {
		
		if (isTamed()) {
			if (stack != null) {
				if (stack.getItem() == Items.EMERALD) {
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
		} else if (stack != null && stack.getItem() == Items.EMERALD) {
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
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
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
		} else if (id == 12) {
			this.spawnParticles(EnumParticleTypes.HEART);
		} else if (id == 13) {
			this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
		} else if (id == 14) {
			this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
		} else {
			super.handleStatusUpdate(id);
		}
	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof TameableVillager) {
				TameableVillager entityChicken = (TameableVillager) p_142018_1_;

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
		private final TameableVillager theBat;
		private EntityPlayer thePlayer;
		private final World worldObject;
		private final float minPlayerDistance;
		private int timeoutCounter;

		public EntityAIBeg(TameableVillager blaze, float minDistance) {
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
		private final TameableVillager theEntity;

		/** If the EntityTameable is sitting. */

		private boolean isSitting;

		public EntityAISit(TameableVillager entityIn) {
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
	public TameableVillager createChild(EntityAgeable ageable) {
		TameableVillager entityTameableVillager = new TameableVillager(this.worldObj);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entityTameableVillager.setOwnerId(uuid);
			entityTameableVillager.setTamed(true);
		}

		return entityTameableVillager;
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final TameableVillager thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(TameableVillager thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
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
		TameableVillager theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(TameableVillager theDefendingTameableIn) {
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
		private final TameableVillager TameableVillager;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(TameableVillager p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			TameableVillager = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = TameableVillager.worldObj.getNearestAttackablePlayer(TameableVillager.posX, TameableVillager.posY,
					TameableVillager.posZ, d0, d0, (Function) null, (@Nullable EntityPlayer player) -> (player != null)
							&& (TameableVillager.shouldAttackPlayer(player)));
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
				if (!TameableVillager.shouldAttackPlayer(player)) {
					return false;
				}
				TameableVillager.faceEntity(player, 10.0F, 10.0F);
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
					if (TameableVillager.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSqToEntity(TameableVillager) < 16.0D) {
						}
						teleportTime = 0;
					} else if ((targetEntity.getDistanceSqToEntity(TameableVillager) > 256.0D) && (teleportTime++ >= 30)
							&& (TameableVillager.teleportToEntity(targetEntity))) {
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

	private void setAdditionalAItasks() {
		if (!this.areAdditionalTasksSet) {
			this.areAdditionalTasksSet = true;

			if (this.isChild()) {
				this.tasks.addTask(8, new EntityAIPlay(this, 0.32D));
			} else if (this.getProfession() == 0) {
				this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6D));
			}
		}
	}

	/**
	 * This is called when Entity's growing age timer reaches 0 (negative values
	 * are considered as a child, positive as an adult)
	 */
	protected void onGrowingAdult() {
		if (this.getProfession() == 0) {
			this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6D));
		}

		super.onGrowingAdult();
	}

	protected void updateAITasks() {
		if (--this.randomTickDivider <= 0) {
			BlockPos blockpos = new BlockPos(this);
			this.worldObj.getVillageCollection().addToVillagerPositionList(blockpos);
			this.randomTickDivider = 70 + this.rand.nextInt(50);
			this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(blockpos, 32);

			if (this.villageObj == null) {
				this.detachHome();
			} else {
				BlockPos blockpos1 = this.villageObj.getCenter();
				this.setHomePosAndDistance(blockpos1, this.villageObj.getVillageRadius());

				if (this.isLookingForHome) {
					this.isLookingForHome = false;
					this.villageObj.setDefaultPlayerReputation(5);
				}
			}
		}

		if (!this.isTrading() && this.timeUntilReset > 0) {
			--this.timeUntilReset;

			if (this.timeUntilReset <= 0) {
				if (this.needsInitilization) {
					for (MerchantRecipe merchantrecipe : this.buyingList) {
						if (merchantrecipe.isRecipeDisabled()) {
							merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
						}
					}

					this.populateBuyingList();
					this.needsInitilization = false;

					if (this.villageObj != null && this.lastBuyingPlayer != null) {
						this.worldObj.setEntityState(this, (byte) 14);
						this.villageObj.modifyPlayerReputation(this.lastBuyingPlayer, 1);
					}
				}

				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
			}
		}

		super.updateAITasks();
	}

	

	public static void registerFixesTameableVillager(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, "TameableVillager");
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists("TameableVillager", new String[] { "Inventory" }));
		fixer.registerWalker(FixTypes.ENTITY, new IDataWalker() {
			public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
				if ("Villager".equals(compound.getString("id")) && compound.hasKey("Offers", 10)) {
					NBTTagCompound nbttagcompound = compound.getCompoundTag("Offers");

					if (nbttagcompound.hasKey("Recipes", 9)) {
						NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 10);

						for (int i = 0; i < nbttaglist.tagCount(); ++i) {
							NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
							DataFixesManager.processItemStack(fixer, nbttagcompound1, versionIn, "buy");
							DataFixesManager.processItemStack(fixer, nbttagcompound1, versionIn, "buyB");
							DataFixesManager.processItemStack(fixer, nbttagcompound1, versionIn, "sell");
							nbttaglist.set(i, nbttagcompound1);
						}
					}
				}

				return compound;
			}
		});
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn() {
		return false;
	}

	protected SoundEvent getAmbientSound() {
		return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADING : SoundEvents.ENTITY_VILLAGER_AMBIENT;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_DEATH;
	}

	public void setProfession(int professionId) {
		this.dataManager.set(PROFESSION, Integer.valueOf(professionId));
		TamedVillagerRegistry.onSetProfession(this, professionId);
	}

	@Deprecated // Use Forge Variant below
	public int getProfession() {
		return Math.max(((Integer) this.dataManager.get(PROFESSION)).intValue() % 5, 0);
	}

	private TamedVillagerRegistry.TamedVillagerProfession prof;

	public void setProfession(TamedVillagerRegistry.TamedVillagerProfession prof) {
		this.dataManager.set(PROFESSION_STR, prof.getRegistryName().toString());
		this.prof = prof;
		TamedVillagerRegistry.onSetProfession(this, prof);
	}

	public TamedVillagerRegistry.TamedVillagerProfession getProfessionForge() {
		if (this.prof == null) {
			String p = this.dataManager.get(PROFESSION_STR);
			net.minecraft.util.ResourceLocation res = new net.minecraft.util.ResourceLocation(
					p == null ? "minecraft:farmer" : p);
			this.prof = TamedVillagerRegistry.instance().getRegistry().getValue(res);
			if (this.prof == null)
				return TamedVillagerRegistry.instance().getRegistry()
						.getValue(new net.minecraft.util.ResourceLocation("minecraft:farmer"));
		}
		return this.prof;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (key.equals(PROFESSION_STR)) {
			String p = this.dataManager.get(PROFESSION_STR);
			net.minecraft.util.ResourceLocation res = new net.minecraft.util.ResourceLocation(
					p == null ? "minecraft:farmer" : p);
			this.prof = TamedVillagerRegistry.instance().getRegistry().getValue(res);
		} else if (key.equals(PROFESSION)) {
			TamedVillagerRegistry.onSetProfession(this, this.dataManager.get(PROFESSION));
		}
	}

	public boolean isMating() {
		return this.isMating;
	}

	public void setMating(boolean mating) {
		this.isMating = mating;
	}

	public void setPlaying(boolean playing) {
		this.isPlaying = playing;
	}

	public boolean isPlaying() {
		return this.isPlaying;
	}

	public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
		super.setRevengeTarget(livingBase);

		if (this.villageObj != null && livingBase != null) {
			this.villageObj.addOrRenewAgressor(livingBase);

			if (livingBase instanceof EntityPlayer) {
				int i = -1;

				if (this.isChild()) {
					i = -3;
				}

				this.villageObj.modifyPlayerReputation(livingBase.getName(), i);

				if (this.isEntityAlive()) {
					this.worldObj.setEntityState(this, (byte) 13);
				}
			}
		}
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource cause) {
		if (this.villageObj != null) {
			Entity entity = cause.getEntity();

			if (entity != null) {
				if (entity instanceof EntityPlayer) {
					this.villageObj.modifyPlayerReputation(entity.getName(), -2);
				} else if (entity instanceof IMob) {
					this.villageObj.endMatingSeason();
				}
			} else {
				EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);

				if (entityplayer != null) {
					this.villageObj.endMatingSeason();
				}
			}
		}

		super.onDeath(cause);
	}

	public void setCustomer(EntityPlayer player) {
		this.buyingPlayer = player;
	}

	public EntityPlayer getCustomer() {
		return this.buyingPlayer;
	}

	public boolean isTrading() {
		return this.buyingPlayer != null;
	}

	/**
	 * Returns current or updated value of {@link #isWillingToMate}
	 */
	public boolean getIsWillingToMate(boolean updateFirst) {
		if (!this.isWillingToMate && updateFirst && this.hasEnoughFoodToBreed()) {
			boolean flag = false;

			for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
				ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

				if (itemstack != null) {
					if (itemstack.getItem() == Items.BREAD && itemstack.stackSize >= 3) {
						flag = true;
						this.villagerInventory.decrStackSize(i, 3);
					} else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT)
							&& itemstack.stackSize >= 12) {
						flag = true;
						this.villagerInventory.decrStackSize(i, 12);
					}
				}

				if (flag) {
					this.worldObj.setEntityState(this, (byte) 18);
					this.isWillingToMate = true;
					break;
				}
			}
		}

		return this.isWillingToMate;
	}

	public void setIsWillingToMate(boolean willingToTrade) {
		this.isWillingToMate = willingToTrade;
	}

	public void useRecipe(MerchantRecipe recipe) {
		recipe.incrementToolUses();
		this.livingSoundTime = -this.getTalkInterval();
		this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
		int i = 3 + this.rand.nextInt(4);

		if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
			this.timeUntilReset = 40;
			this.needsInitilization = true;
			this.isWillingToMate = true;

			if (this.buyingPlayer != null) {
				this.lastBuyingPlayer = this.buyingPlayer.getName();
			} else {
				this.lastBuyingPlayer = null;
			}

			i += 5;
		}

		if (recipe.getItemToBuy().getItem() == Items.EMERALD) {
			this.wealth += recipe.getItemToBuy().stackSize;
		}

		if (recipe.getRewardsExp()) {
			this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5D, this.posZ, i));
		}
	}

	/**
	 * Notifies the merchant of a possible merchantrecipe being fulfilled or
	 * not. Usually, this is just a sound byte being played depending if the
	 * suggested itemstack is not null.
	 */
	public void verifySellingItem(ItemStack stack) {
		if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
			this.livingSoundTime = -this.getTalkInterval();

			if (stack != null) {
				this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
			} else {
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
			}
		}
	}

	public MerchantRecipeList getRecipes(EntityPlayer player) {
		if (this.buyingList == null) {
			this.populateBuyingList();
		}

		return this.buyingList;
	}

	private void populateBuyingList() {
		if (this.careerId != 0 && this.careerLevel != 0) {
			++this.careerLevel;
		} else {
			this.careerId = this.getProfessionForge().getRandomCareer(this.rand) + 1;
			this.careerLevel = 1;
		}

		if (this.buyingList == null) {
			this.buyingList = new MerchantRecipeList();
		}

		int i = this.careerId - 1;
		int j = this.careerLevel - 1;
		java.util.List<TameableVillager.ITradeList> trades = this.getProfessionForge().getCareer(i).getTrades(j);

		if (trades != null) {
			for (TameableVillager.ITradeList TameableVillager$itradelist : trades) {
				TameableVillager$itradelist.modifyMerchantRecipeList(this.buyingList, this.rand);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void setRecipes(MerchantRecipeList recipeList) {
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's
	 * username in chat
	 */
	public ITextComponent getDisplayName() {
		Team team = this.getTeam();
		String s = this.getCustomNameTag();

		if (s != null && !s.isEmpty()) {
			TextComponentString textcomponentstring = new TextComponentString(
					ScorePlayerTeam.formatPlayerName(team, s));
			textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
			textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
			return textcomponentstring;
		} else {
			if (this.buyingList == null) {
				this.populateBuyingList();
			}

			String s1 = null;

			switch (this.getProfession()) {
			case 0:

				if (this.careerId == 1) {
					s1 = "farmer";
				} else if (this.careerId == 2) {
					s1 = "fisherman";
				} else if (this.careerId == 3) {
					s1 = "shepherd";
				} else if (this.careerId == 4) {
					s1 = "fletcher";
				}

				break;
			case 1:
				s1 = "librarian";
				break;
			case 2:
				s1 = "cleric";
				break;
			case 3:

				if (this.careerId == 1) {
					s1 = "armor";
				} else if (this.careerId == 2) {
					s1 = "weapon";
				} else if (this.careerId == 3) {
					s1 = "tool";
				}

				break;
			case 4:

				if (this.careerId == 1) {
					s1 = "butcher";
				} else if (this.careerId == 2) {
					s1 = "leather";
				}
			}

			s1 = "entity.Villager." + this.getProfessionForge().getCareer(this.careerId - 1).getName();
			{
				TextComponentTranslation textcomponenttranslation = new TextComponentTranslation(s1, new Object[0]);
				textcomponenttranslation.getStyle().setHoverEvent(this.getHoverEvent());
				textcomponenttranslation.getStyle().setInsertion(this.getCachedUniqueIdString());

				if (team != null) {
					textcomponenttranslation.getStyle().setColor(team.getChatFormat());
				}

				return textcomponenttranslation;
			}
		}
	}

	public float getEyeHeight() {
		return this.isChild() ? 0.81F : 1.62F;
	}

	

	@SideOnly(Side.CLIENT)
	private void spawnParticles(EnumParticleTypes particleType) {
		for (int i = 0; i < 5; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(particleType,
					this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
					this.posY + 1.0D + (double) (this.rand.nextFloat() * this.height),
					this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2,
					new int[0]);
		}
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		TamedVillagerRegistry.setRandomProfession(this, this.worldObj.rand);
		this.setAdditionalAItasks();
		return livingdata;
	}

	public void setLookingForHome() {
		this.isLookingForHome = true;
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		if (!this.worldObj.isRemote && !this.isDead) {
			EntityWitch entitywitch = new EntityWitch(this.worldObj);
			entitywitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			entitywitch.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entitywitch)),
					(IEntityLivingData) null);
			entitywitch.setNoAI(this.isAIDisabled());

			if (this.hasCustomName()) {
				entitywitch.setCustomNameTag(this.getCustomNameTag());
				entitywitch.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
			}

			this.worldObj.spawnEntityInWorld(entitywitch);
			this.setDead();
		}
	}

	public InventoryBasic getVillagerInventory() {
		return this.villagerInventory;
	}

	/**
	 * Tests if this entity should pickup a weapon or an armor. Entity drops
	 * current weapon or armor if the new one is better.
	 */
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
		ItemStack itemstack = itemEntity.getEntityItem();
		Item item = itemstack.getItem();

		if (this.canVillagerPickupItem(item)) {
			ItemStack itemstack1 = this.villagerInventory.addItem(itemstack);

			if (itemstack1 == null) {
				itemEntity.setDead();
			} else {
				itemstack.stackSize = itemstack1.stackSize;
			}
		}
	}

	private boolean canVillagerPickupItem(Item itemIn) {
		return itemIn == Items.BREAD || itemIn == Items.POTATO || itemIn == Items.CARROT || itemIn == Items.WHEAT
				|| itemIn == Items.WHEAT_SEEDS || itemIn == Items.BEETROOT || itemIn == Items.BEETROOT_SEEDS;
	}

	public boolean hasEnoughFoodToBreed() {
		return this.hasEnoughItems(1);
	}

	/**
	 * Used by {@link net.minecraft.entity.ai.EntityAIVillagerInteract
	 * EntityAIVillagerInteract} to check if the villager can give some items
	 * from an inventory to another villager.
	 */
	public boolean canAbondonItems() {
		return this.hasEnoughItems(2);
	}

	public boolean wantsMoreFood() {
		boolean flag = this.getProfession() == 0;
		return flag ? !this.hasEnoughItems(5) : !this.hasEnoughItems(1);
	}

	/**
	 * Returns true if villager has enough items in inventory
	 */
	private boolean hasEnoughItems(int multiplier) {
		boolean flag = this.getProfession() == 0;

		for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

			if (itemstack != null) {
				if (itemstack.getItem() == Items.BREAD && itemstack.stackSize >= 3 * multiplier
						|| itemstack.getItem() == Items.POTATO && itemstack.stackSize >= 12 * multiplier
						|| itemstack.getItem() == Items.CARROT && itemstack.stackSize >= 12 * multiplier
						|| itemstack.getItem() == Items.BEETROOT && itemstack.stackSize >= 12 * multiplier) {
					return true;
				}

				if (flag && itemstack.getItem() == Items.WHEAT && itemstack.stackSize >= 9 * multiplier) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if villager has seeds, potatoes or carrots in inventory
	 */
	public boolean isFarmItemInInventory() {
		for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

			if (itemstack != null && (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.POTATO
					|| itemstack.getItem() == Items.CARROT || itemstack.getItem() == Items.BEETROOT_SEEDS)) {
				return true;
			}
		}

		return false;
	}

	public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
		if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
			return true;
		} else {
			int i = inventorySlot - 300;

			if (i >= 0 && i < this.villagerInventory.getSizeInventory()) {
				this.villagerInventory.setInventorySlotContents(i, itemStackIn);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class EmeraldForItems implements TameableVillager.ITradeList {
		public Item buyingItem;
		public TameableVillager.PriceInfo price;

		public EmeraldForItems(Item itemIn, TameableVillager.PriceInfo priceIn) {
			this.buyingItem = itemIn;
			this.price = priceIn;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int i = 1;

			if (this.price != null) {
				i = this.price.getPrice(random);
			}

			recipeList.add(new MerchantRecipe(new ItemStack(this.buyingItem, i, 0), Items.EMERALD));
		}
	}

	public interface ITradeList {
		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random);
	}

	public static class ItemAndEmeraldToItem implements TameableVillager.ITradeList {
		/**
		 * The itemstack to buy with an emerald. The Item and damage value is
		 * used only, any tag data is not retained.
		 */
		public ItemStack buyingItemStack;
		/**
		 * The price info defining the amount of the buying item required with 1
		 * emerald to match the selling item.
		 */
		public TameableVillager.PriceInfo buyingPriceInfo;
		/**
		 * The itemstack to sell. The item and damage value are used only, any
		 * tag data is not retained.
		 */
		public ItemStack sellingItemstack;
		public TameableVillager.PriceInfo sellingPriceInfo;

		public ItemAndEmeraldToItem(Item p_i45813_1_, TameableVillager.PriceInfo p_i45813_2_, Item p_i45813_3_,
				TameableVillager.PriceInfo p_i45813_4_) {
			this.buyingItemStack = new ItemStack(p_i45813_1_);
			this.buyingPriceInfo = p_i45813_2_;
			this.sellingItemstack = new ItemStack(p_i45813_3_);
			this.sellingPriceInfo = p_i45813_4_;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int i = 1;

			if (this.buyingPriceInfo != null) {
				i = this.buyingPriceInfo.getPrice(random);
			}

			int j = 1;

			if (this.sellingPriceInfo != null) {
				j = this.sellingPriceInfo.getPrice(random);
			}

			recipeList.add(new MerchantRecipe(
					new ItemStack(this.buyingItemStack.getItem(), i, this.buyingItemStack.getMetadata()),
					new ItemStack(Items.EMERALD),
					new ItemStack(this.sellingItemstack.getItem(), j, this.sellingItemstack.getMetadata())));
		}
	}

	public static class ListEnchantedBookForEmeralds implements TameableVillager.ITradeList {
		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			Enchantment enchantment = (Enchantment) Enchantment.REGISTRY.getRandomObject(random);
			int i = MathHelper.getRandomIntegerInRange(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
			ItemStack itemstack = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(enchantment, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

			if (enchantment.isTreasureEnchantment()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			recipeList.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
		}
	}

	public static class ListEnchantedItemForEmeralds implements TameableVillager.ITradeList {
		/** The enchanted item stack to sell */
		public ItemStack enchantedItemStack;
		/**
		 * The price info determining the amount of emeralds to trade in for the
		 * enchanted item
		 */
		public TameableVillager.PriceInfo priceInfo;

		public ListEnchantedItemForEmeralds(Item p_i45814_1_, TameableVillager.PriceInfo p_i45814_2_) {
			this.enchantedItemStack = new ItemStack(p_i45814_1_);
			this.priceInfo = p_i45814_2_;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int i = 1;

			if (this.priceInfo != null) {
				i = this.priceInfo.getPrice(random);
			}

			ItemStack itemstack = new ItemStack(Items.EMERALD, i, 0);
			ItemStack itemstack1 = new ItemStack(this.enchantedItemStack.getItem(), 1,
					this.enchantedItemStack.getMetadata());
			itemstack1 = EnchantmentHelper.addRandomEnchantment(random, itemstack1, 5 + random.nextInt(15), false);
			recipeList.add(new MerchantRecipe(itemstack, itemstack1));
		}
	}

	public static class ListItemForEmeralds implements TameableVillager.ITradeList {
		/** The item that is being bought for emeralds */
		public ItemStack itemToBuy;
		/**
		 * The price info for the amount of emeralds to sell for, or if
		 * negative, the amount of the item to buy for an emerald.
		 */
		public TameableVillager.PriceInfo priceInfo;

		public ListItemForEmeralds(Item par1Item, TameableVillager.PriceInfo priceInfo) {
			this.itemToBuy = new ItemStack(par1Item);
			this.priceInfo = priceInfo;
		}

		public ListItemForEmeralds(ItemStack stack, TameableVillager.PriceInfo priceInfo) {
			this.itemToBuy = stack;
			this.priceInfo = priceInfo;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove
		 * MerchantRecipes.
		 */
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int i = 1;

			if (this.priceInfo != null) {
				i = this.priceInfo.getPrice(random);
			}

			ItemStack itemstack;
			ItemStack itemstack1;

			if (i < 0) {
				itemstack = new ItemStack(Items.EMERALD);
				itemstack1 = new ItemStack(this.itemToBuy.getItem(), -i, this.itemToBuy.getMetadata());
			} else {
				itemstack = new ItemStack(Items.EMERALD, i, 0);
				itemstack1 = new ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.getMetadata());
			}

			recipeList.add(new MerchantRecipe(itemstack, itemstack1));
		}
	}

	static class EntityAITradePlayer extends EntityAIBase {
		private final TameableVillager villager;

		public EntityAITradePlayer(TameableVillager villagerIn) {
			this.villager = villagerIn;
			this.setMutexBits(5);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (!this.villager.isEntityAlive()) {
				return false;
			} else if (this.villager.isInWater()) {
				return false;
			} else if (!this.villager.onGround) {
				return false;
			} else if (this.villager.velocityChanged) {
				return false;
			} else {
				EntityPlayer entityplayer = this.villager.getCustomer();
				return entityplayer == null ? false
						: (this.villager.getDistanceSqToEntity(entityplayer) > 16.0D ? false
								: entityplayer.openContainer instanceof Container);
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.villager.getNavigator().clearPathEntity();
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			this.villager.setCustomer((EntityPlayer) null);
		}
	}

	public static class PriceInfo extends Tuple<Integer, Integer> {
		public PriceInfo(int p_i45810_1_, int p_i45810_2_) {
			super(Integer.valueOf(p_i45810_1_), Integer.valueOf(p_i45810_2_));
		}

		public int getPrice(Random rand) {
			return ((Integer) this.getFirst()).intValue() >= ((Integer) this.getSecond()).intValue()
					? ((Integer) this.getFirst()).intValue()
					: ((Integer) this.getFirst()).intValue() + rand.nextInt(
							((Integer) this.getSecond()).intValue() - ((Integer) this.getFirst()).intValue() + 1);
		}
	}

	static class EntityAILookAtTradePlayer extends EntityAIWatchClosest {
		private final TameableVillager theMerchant;

		public EntityAILookAtTradePlayer(TameableVillager theMerchantIn) {
			super(theMerchantIn, EntityPlayer.class, 8.0F);
			this.theMerchant = theMerchantIn;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (this.theMerchant.isTrading()) {
				this.closestEntity = this.theMerchant.getCustomer();
				return true;
			} else {
				return false;
			}
		}
	}

	public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
		/** Villager that is harvesting */
		private final TameableVillager theVillager;
		private boolean hasFarmItem;
		private boolean wantsToReapStuff;
		private int currentTask;

		public EntityAIHarvestFarmland(TameableVillager theVillagerIn, double speedIn) {
			super(theVillagerIn, speedIn, 16);
			this.theVillager = theVillagerIn;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (this.runDelay <= 0) {
				if (!this.theVillager.worldObj.getGameRules().getBoolean("mobGriefing")) {
					return false;
				}

				this.currentTask = -1;
				this.hasFarmItem = this.theVillager.isFarmItemInInventory();
				this.wantsToReapStuff = this.theVillager.wantsMoreFood();
			}

			return super.shouldExecute();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return this.currentTask >= 0 && super.continueExecuting();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			super.resetTask();
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			super.updateTask();
			this.theVillager.getLookHelper().setLookPosition((double) this.destinationBlock.getX() + 0.5D,
					(double) (this.destinationBlock.getY() + 1), (double) this.destinationBlock.getZ() + 0.5D, 10.0F,
					(float) this.theVillager.getVerticalFaceSpeed());

			if (this.getIsAboveDestination()) {
				World world = this.theVillager.worldObj;
				BlockPos blockpos = this.destinationBlock.up();
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (this.currentTask == 0 && block instanceof BlockCrops
						&& ((BlockCrops) block).isMaxAge(iblockstate)) {
					world.destroyBlock(blockpos, true);
				} else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR) {
					InventoryBasic inventorybasic = this.theVillager.getVillagerInventory();

					for (int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
						ItemStack itemstack = inventorybasic.getStackInSlot(i);
						boolean flag = false;

						if (itemstack != null) {
							if (itemstack.getItem() == Items.WHEAT_SEEDS) {
								world.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
								flag = true;
							} else if (itemstack.getItem() == Items.POTATO) {
								world.setBlockState(blockpos, Blocks.POTATOES.getDefaultState(), 3);
								flag = true;
							} else if (itemstack.getItem() == Items.CARROT) {
								world.setBlockState(blockpos, Blocks.CARROTS.getDefaultState(), 3);
								flag = true;
							} else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
								world.setBlockState(blockpos, Blocks.BEETROOTS.getDefaultState(), 3);
								flag = true;
							}
						}

						if (flag) {
							--itemstack.stackSize;

							if (itemstack.stackSize <= 0) {
								inventorybasic.setInventorySlotContents(i, (ItemStack) null);
							}

							break;
						}
					}
				}

				this.currentTask = -1;
				this.runDelay = 10;
			}
		}

		/**
		 * Return true to set given position as destination
		 */
		protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
			Block block = worldIn.getBlockState(pos).getBlock();

			if (block == Blocks.FARMLAND) {
				pos = pos.up();
				IBlockState iblockstate = worldIn.getBlockState(pos);
				block = iblockstate.getBlock();

				if (block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(iblockstate) && this.wantsToReapStuff
						&& (this.currentTask == 0 || this.currentTask < 0)) {
					this.currentTask = 0;
					return true;
				}

				if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem
						&& (this.currentTask == 1 || this.currentTask < 0)) {
					this.currentTask = 1;
					return true;
				}
			}

			return false;
		}
	}

	public class EntityAIVillagerMate extends EntityAIBase {
		private final TameableVillager villagerObj;
		private TameableVillager mate;
		private final World worldObj;
		private int matingTimeout;
		Village villageObj;

		public EntityAIVillagerMate(TameableVillager villagerIn) {
			this.villagerObj = villagerIn;
			this.worldObj = villagerIn.worldObj;
			this.setMutexBits(3);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (this.villagerObj.getGrowingAge() != 0) {
				return false;
			} else if (this.villagerObj.getRNG().nextInt(500) != 0) {
				return false;
			} else {
				this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.villagerObj),
						0);

				if (this.villageObj == null) {
					return false;
				} else if (this.checkSufficientDoorsPresentForNewVillager()
						&& this.villagerObj.getIsWillingToMate(true)) {
					Entity entity = this.worldObj.findNearestEntityWithinAABB(TameableVillager.class,
							this.villagerObj.getEntityBoundingBox().expand(8.0D, 3.0D, 8.0D), this.villagerObj);

					if (entity == null) {
						return false;
					} else {
						this.mate = (TameableVillager) entity;
						return this.mate.getGrowingAge() == 0 && this.mate.getIsWillingToMate(true);
					}
				} else {
					return false;
				}
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.matingTimeout = 300;
			this.villagerObj.setMating(true);
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			this.villageObj = null;
			this.mate = null;
			this.villagerObj.setMating(false);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return this.matingTimeout >= 0 && this.checkSufficientDoorsPresentForNewVillager()
					&& this.villagerObj.getGrowingAge() == 0 && this.villagerObj.getIsWillingToMate(false);
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			--this.matingTimeout;
			this.villagerObj.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);

			if (this.villagerObj.getDistanceSqToEntity(this.mate) > 2.25D) {
				this.villagerObj.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
			} else if (this.matingTimeout == 0 && this.mate.isMating()) {
				this.giveBirth();
			}

			if (this.villagerObj.getRNG().nextInt(35) == 0) {
				this.worldObj.setEntityState(this.villagerObj, (byte) 12);
			}
		}

		private boolean checkSufficientDoorsPresentForNewVillager() {
			if (!this.villageObj.isMatingSeason()) {
				return false;
			} else {
				int i = (int) ((double) ((float) this.villageObj.getNumVillageDoors()) * 0.35D);
				return this.villageObj.getNumVillagers() < i;
			}
		}

		private void giveBirth() {
			net.minecraft.entity.EntityAgeable tameableVillager = this.villagerObj.createChild(this.mate);
			this.mate.setGrowingAge(6000);
			this.villagerObj.setGrowingAge(6000);
			this.mate.setIsWillingToMate(false);
			this.villagerObj.setIsWillingToMate(false);

			final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(
					villagerObj, mate, tameableVillager);
			if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event) || event.getChild() == null) {
				return;
			}
			tameableVillager = event.getChild();
			tameableVillager.setGrowingAge(-24000);
			tameableVillager.setLocationAndAngles(this.villagerObj.posX, this.villagerObj.posY, this.villagerObj.posZ,
					0.0F, 0.0F);
			this.worldObj.spawnEntityInWorld(tameableVillager);
			this.worldObj.setEntityState(tameableVillager, (byte) 12);
		}
	}

	static class EntityAIPlay extends EntityAIBase {
		private final TameableVillager villagerObj;
		private EntityLivingBase targetVillager;
		private final double speed;
		private int playTime;

		public EntityAIPlay(TameableVillager villagerObjIn, double speedIn) {
			this.villagerObj = villagerObjIn;
			this.speed = speedIn;
			this.setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (this.villagerObj.getGrowingAge() >= 0) {
				return false;
			} else if (this.villagerObj.getRNG().nextInt(400) != 0) {
				return false;
			} else {
				List<TameableVillager> list = this.villagerObj.worldObj.<TameableVillager>getEntitiesWithinAABB(
						TameableVillager.class, this.villagerObj.getEntityBoundingBox().expand(6.0D, 3.0D, 6.0D));
				double d0 = Double.MAX_VALUE;

				for (TameableVillager TameableVillager : list) {
					if (TameableVillager != this.villagerObj && !TameableVillager.isPlaying()
							&& TameableVillager.getGrowingAge() < 0) {
						double d1 = TameableVillager.getDistanceSqToEntity(this.villagerObj);

						if (d1 <= d0) {
							d0 = d1;
							this.targetVillager = TameableVillager;
						}
					}
				}

				if (this.targetVillager == null) {
					Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

					if (vec3d == null) {
						return false;
					}
				}

				return true;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return this.playTime > 0;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			if (this.targetVillager != null) {
				this.villagerObj.setPlaying(true);
			}

			this.playTime = 1000;
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			this.villagerObj.setPlaying(false);
			this.targetVillager = null;
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			--this.playTime;

			if (this.targetVillager != null) {
				if (this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0D) {
					this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.speed);
				}
			} else if (this.villagerObj.getNavigator().noPath()) {
				Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

				if (vec3d == null) {
					return;
				}

				this.villagerObj.getNavigator().tryMoveToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, this.speed);
			}
		}
	}

	public class EntityAIFollowGolem extends EntityAIBase {
		private final TameableVillager theVillager;
		private EntityIronGolem theGolem;
		private int takeGolemRoseTick;
		private boolean tookGolemRose;

		public EntityAIFollowGolem(TameableVillager theVillagerIn) {
			this.theVillager = theVillagerIn;
			this.setMutexBits(3);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			if (this.theVillager.getGrowingAge() >= 0) {
				return false;
			} else if (!this.theVillager.worldObj.isDaytime()) {
				return false;
			} else {
				List<EntityIronGolem> list = this.theVillager.worldObj.<EntityIronGolem>getEntitiesWithinAABB(
						EntityIronGolem.class, this.theVillager.getEntityBoundingBox().expand(6.0D, 2.0D, 6.0D));

				if (list.isEmpty()) {
					return false;
				} else {
					for (EntityIronGolem entityirongolem : list) {
						if (entityirongolem.getHoldRoseTick() > 0) {
							this.theGolem = entityirongolem;
							break;
						}
					}

					return this.theGolem != null;
				}
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean continueExecuting() {
			return this.theGolem.getHoldRoseTick() > 0;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.takeGolemRoseTick = this.theVillager.getRNG().nextInt(320);
			this.tookGolemRose = false;
			this.theGolem.getNavigator().clearPathEntity();
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			this.theGolem = null;
			this.theVillager.getNavigator().clearPathEntity();
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			this.theVillager.getLookHelper().setLookPositionWithEntity(this.theGolem, 30.0F, 30.0F);

			if (this.theGolem.getHoldRoseTick() == this.takeGolemRoseTick) {
				this.theVillager.getNavigator().tryMoveToEntityLiving(this.theGolem, 0.5D);
				this.tookGolemRose = true;
			}

			if (this.tookGolemRose && this.theVillager.getDistanceSqToEntity(this.theGolem) < 4.0D) {
				this.theGolem.setHoldingRose(false);
				this.theVillager.getNavigator().clearPathEntity();
			}
		}
	}

	public class EntityAIVillagerInteract extends EntityAIWatchClosest2 {
		/** The delay before the villager throws an itemstack (in ticks) */
		private int interactionDelay;
		private final TameableVillager villager;

		public EntityAIVillagerInteract(TameableVillager villagerIn) {
			super(villagerIn, TameableVillager.class, 3.0F, 0.02F);
			this.villager = villagerIn;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();

			if (this.villager.canAbondonItems() && this.closestEntity instanceof TameableVillager
					&& ((TameableVillager) this.closestEntity).wantsMoreFood()) {
				this.interactionDelay = 10;
			} else {
				this.interactionDelay = 0;
			}
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			super.updateTask();

			if (this.interactionDelay > 0) {
				--this.interactionDelay;

				if (this.interactionDelay == 0) {
					InventoryBasic inventorybasic = this.villager.getVillagerInventory();

					for (int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
						ItemStack itemstack = inventorybasic.getStackInSlot(i);
						ItemStack itemstack1 = null;

						if (itemstack != null) {
							Item item = itemstack.getItem();

							if ((item == Items.BREAD || item == Items.POTATO || item == Items.CARROT
									|| item == Items.BEETROOT) && itemstack.stackSize > 3) {
								int l = itemstack.stackSize / 2;
								itemstack.stackSize -= l;
								itemstack1 = new ItemStack(item, l, itemstack.getMetadata());
							} else if (item == Items.WHEAT && itemstack.stackSize > 5) {
								int j = itemstack.stackSize / 2 / 3 * 3;
								int k = j / 3;
								itemstack.stackSize -= j;
								itemstack1 = new ItemStack(Items.BREAD, k, 0);
							}

							if (itemstack.stackSize <= 0) {
								inventorybasic.setInventorySlotContents(i, (ItemStack) null);
							}
						}

						if (itemstack1 != null) {
							double d0 = this.villager.posY - 0.30000001192092896D
									+ (double) this.villager.getEyeHeight();
							EntityItem entityitem = new EntityItem(this.villager.worldObj, this.villager.posX, d0,
									this.villager.posZ, itemstack1);
							float f = 0.3F;
							float f1 = this.villager.rotationYawHead;
							float f2 = this.villager.rotationPitch;
							entityitem.motionX = (double) (-MathHelper.sin(f1 * 0.017453292F)
									* MathHelper.cos(f2 * 0.017453292F) * 0.3F);
							entityitem.motionZ = (double) (MathHelper.cos(f1 * 0.017453292F)
									* MathHelper.cos(f2 * 0.017453292F) * 0.3F);
							entityitem.motionY = (double) (-MathHelper.sin(f2 * 0.017453292F) * 0.3F + 0.1F);
							entityitem.setDefaultPickupDelay();
							this.villager.worldObj.spawnEntityInWorld(entityitem);
							break;
						}
					}
				}
			}
		}
	}

	// MODDERS DO NOT USE OR EDIT THIS IN ANY WAY IT WILL HAVE NO EFFECT, THIS
	// IS JUST IN HERE TO ALLOW FORGE TO ACCESS IT
	public static ITradeList[][][][] GET_TRADES_DONT_USE() {
		return DEFAULT_TRADE_LIST_MAP;
	}
}
