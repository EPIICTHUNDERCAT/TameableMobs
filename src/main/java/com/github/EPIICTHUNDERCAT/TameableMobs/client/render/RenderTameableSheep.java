package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSheep;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedSheepWool;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableSheep extends RenderLiving<TameableSheep> {

	private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation(Reference.ID,
			"textures/entity/tameablesheep/tameablesheep.png");

	public RenderTameableSheep(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerTamedSheepWool(this));
	}

	/*
	 * private final ModelTameableSheep1 sheepModel;
	 * 
	 * public RenderTameableSheep(RenderManager renderManagerIn) {
	 * super(renderManagerIn, new ModelTameableSheep1(), 0.2F); sheepModel =
	 * (ModelTameableSheep1) super.mainModel; addLayer(new
	 * LayerTamedSheepWool(this)); }
	 */
	@Override
	public void doRender(TameableSheep entity, double x, double y, double z, float entityYaw, float partialTicks) {

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(TameableSheep entity) {
		return SHEARED_SHEEP_TEXTURES;
	}

}
