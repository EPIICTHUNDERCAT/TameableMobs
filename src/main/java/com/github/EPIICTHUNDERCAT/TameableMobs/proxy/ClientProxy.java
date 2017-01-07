package com.github.epiicthundercat.tameablemobs.proxy;

import com.github.epiicthundercat.tameablemobs.client.render.RenderBatPee;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTMBug;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableBat;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableBlaze;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableCaveSpider;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableChicken;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableCow;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableCreeper;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableEnderman;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableEndermite;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableGhast;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableGiantZombie;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableGuardian;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableIronGolem;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableMagmaCube;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableMooshroom;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameablePig;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameablePigZombie;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameablePolarBear;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableRabbit;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSheep;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableShulker;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSilverfish;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSkeleton;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSlime;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSnowman;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSpider;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableSquid;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableVillager;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableWitch;
import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableZombie;
import com.github.epiicthundercat.tameablemobs.client.render.RenderWitchProjectile;
import com.github.epiicthundercat.tameablemobs.init.TMItems;
import com.github.epiicthundercat.tameablemobs.mobs.TMBug;
import com.github.epiicthundercat.tameablemobs.mobs.TameableBat;
import com.github.epiicthundercat.tameablemobs.mobs.TameableBlaze;
import com.github.epiicthundercat.tameablemobs.mobs.TameableCaveSpider;
import com.github.epiicthundercat.tameablemobs.mobs.TameableChicken;
import com.github.epiicthundercat.tameablemobs.mobs.TameableCow;
import com.github.epiicthundercat.tameablemobs.mobs.TameableCreeper;
import com.github.epiicthundercat.tameablemobs.mobs.TameableEnderman;
import com.github.epiicthundercat.tameablemobs.mobs.TameableEndermite;
import com.github.epiicthundercat.tameablemobs.mobs.TameableGhast;
import com.github.epiicthundercat.tameablemobs.mobs.TameableGiantZombie;
import com.github.epiicthundercat.tameablemobs.mobs.TameableGuardian;
import com.github.epiicthundercat.tameablemobs.mobs.TameableIronGolem;
import com.github.epiicthundercat.tameablemobs.mobs.TameableMagmaCube;
import com.github.epiicthundercat.tameablemobs.mobs.TameableMooshroom;
import com.github.epiicthundercat.tameablemobs.mobs.TameablePig;
import com.github.epiicthundercat.tameablemobs.mobs.TameablePigZombie;
import com.github.epiicthundercat.tameablemobs.mobs.TameablePolarBear;
import com.github.epiicthundercat.tameablemobs.mobs.TameableRabbit;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSheep;
import com.github.epiicthundercat.tameablemobs.mobs.TameableShulker;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSilverfish;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSkeleton;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSlime;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSnowman;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSpider;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSquid;
import com.github.epiicthundercat.tameablemobs.mobs.TameableVillager;
import com.github.epiicthundercat.tameablemobs.mobs.TameableWitch;
import com.github.epiicthundercat.tameablemobs.mobs.TameableZombie;
import com.github.epiicthundercat.tameablemobs.mobs.itementities.EntityBatPee;
import com.github.epiicthundercat.tameablemobs.mobs.itementities.EntityWitchProjectile;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableCow;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableRabbit;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableSheep2;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableSlime;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableZombie;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent preEvent) {
		super.preInit(preEvent);
		//ModEntities.initModels();
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		rm.entityRenderMap.put(TameablePolarBear.class, new RenderTameablePolarBear(rm));
		rm.entityRenderMap.put(TameableChicken.class, new RenderTameableChicken(rm));
		rm.entityRenderMap.put(TameableBlaze.class, new RenderTameableBlaze(rm));
		rm.entityRenderMap.put(TameableBat.class, new RenderTameableBat(rm));
		rm.entityRenderMap.put(TameableSheep.class, new RenderTameableSheep(rm, new ModelTameableSheep2(), 0.7F));
		rm.entityRenderMap.put(TameablePig.class, new RenderTameablePig(rm));
		rm.entityRenderMap.put(TameablePigZombie.class, new RenderTameablePigZombie(rm));
		rm.entityRenderMap.put(TameableCreeper.class, new RenderTameableCreeper(rm));
		rm.entityRenderMap.put(TameableCow.class, new RenderTameableCow(rm));
		rm.entityRenderMap.put(TameableEndermite.class, new RenderTameableEndermite(rm));
		rm.entityRenderMap.put(TameableEnderman.class, new RenderTameableEnderman(rm));
		rm.entityRenderMap.put(TameableSpider.class, new RenderTameableSpider(rm));
		rm.entityRenderMap.put(TameableCaveSpider.class, new RenderTameableCaveSpider(rm));
		rm.entityRenderMap.put(TameableWitch.class, new RenderTameableWitch(rm));
		rm.entityRenderMap.put(TameableSquid.class, new RenderTameableSquid(rm));
		rm.entityRenderMap.put(TameableGhast.class, new RenderTameableGhast(rm));
		rm.entityRenderMap.put(TameableShulker.class, new RenderTameableShulker(rm));
		rm.entityRenderMap.put(TameableSlime.class, new RenderTameableSlime(rm, new ModelTameableSlime(16), 0.25F));
		rm.entityRenderMap.put(TameableMagmaCube.class, new RenderTameableMagmaCube(rm));
		rm.entityRenderMap.put(TameableMooshroom.class, new RenderTameableMooshroom(rm, new ModelTameableCow(),  0.7F));
		rm.entityRenderMap.put(TameableRabbit.class, new RenderTameableRabbit(rm, new ModelTameableRabbit(), 0.3F));
		rm.entityRenderMap.put(TameableGuardian.class, new RenderTameableGuardian(rm));
		rm.entityRenderMap.put(TameableSilverfish.class, new RenderTameableSilverfish(rm));
		rm.entityRenderMap.put(TameableGiantZombie.class, new RenderTameableGiantZombie(rm, new ModelTameableZombie(), 0.5F, 6.0F));
		rm.entityRenderMap.put(TameableZombie.class, new RenderTameableZombie(rm));
		rm.entityRenderMap.put(TameableIronGolem.class, new RenderTameableIronGolem(rm));
		rm.entityRenderMap.put(TMBug.class, new RenderTMBug(rm));
		rm.entityRenderMap.put(TameableSkeleton.class, new RenderTameableSkeleton(rm));
		rm.entityRenderMap.put(TameableVillager.class, new RenderTameableVillager(rm));
		rm.entityRenderMap.put(TameableSnowman.class, new RenderTameableSnowman(rm));

	}
	
	
	@Override
	public void registerRenders(FMLInitializationEvent event) {
		TMItems.registerRender(event);
		// NGBlocks.registerRender(event);
	}
	
	@Override
	public void registerEntities(FMLPreInitializationEvent preEvent) {
		super.registerEntities(preEvent);

		RenderingRegistry.registerEntityRenderingHandler(EntityWitchProjectile.class,
				new IRenderFactory<EntityWitchProjectile>() {
					@Override
					public RenderWitchProjectile createRenderFor(RenderManager manager) {
						return new RenderWitchProjectile(manager, TMItems.witch_projectile);
					}
				});

		RenderingRegistry.registerEntityRenderingHandler(EntityBatPee.class,
				new IRenderFactory<EntityBatPee>() {
					@Override
					public RenderBatPee createRenderFor(RenderManager manager) {
						return new RenderBatPee(manager, TMItems.bat_pee);
					}
				});
	}
}
