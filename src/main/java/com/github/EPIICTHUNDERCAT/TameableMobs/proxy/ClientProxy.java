package com.github.EPIICTHUNDERCAT.TameableMobs.proxy;

import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableBat;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableBlaze;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableCaveSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableChicken;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableCow;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableCreeper;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableEnderman;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableEndermite;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameablePig;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameablePigZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameablePolarBear;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableSheep;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableBat;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableBlaze;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCaveSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableChicken;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCow;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCreeper;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEnderman;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEndermite;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePig;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePigZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePolarBear;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSheep;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableSheep2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
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
		
		
		
		  

	}

	@Override
	public void registerRenders(FMLInitializationEvent event) {
		TMItems.registerRender(event);
		// NGBlocks.registerRender(event);
	}

}
