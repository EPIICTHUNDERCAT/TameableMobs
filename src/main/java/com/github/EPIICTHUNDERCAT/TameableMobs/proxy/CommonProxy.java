package com.github.EPIICTHUNDERCAT.TameableMobs.proxy;

import com.github.EPIICTHUNDERCAT.TameableMobs.init.TMItems;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.ModEntities;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {


	public void preInit(FMLPreInitializationEvent preEvent) {
		register(preEvent);
		//NGConfig.config(preEvent);

	}

	public void init(FMLInitializationEvent event) {
		registerRenders(event);
		ModEntities.init();
	
	}

	private void register(FMLPreInitializationEvent preEvent) {
		TMItems.register(preEvent);
		//NGBlocks.register(preEvent);
		

	}

	public void registerRenders(FMLInitializationEvent event) {

	}

	

}