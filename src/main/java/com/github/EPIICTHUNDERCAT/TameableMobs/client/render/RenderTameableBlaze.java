package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableBlaze;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableBlaze;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableBlaze  extends RenderLiving<TameableBlaze>{

	private static final ResourceLocation BLAZE_TEXTURE = new ResourceLocation(Reference.ID, "textures/entity/tameableblaze.png");
	
	
	private final ModelTameableBlaze blazeModel;
	
	
	public RenderTameableBlaze(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelTameableBlaze(), 0.7F);
		blazeModel = (ModelTameableBlaze) super.mainModel;
	}
	@Override
	public void doRender(TameableBlaze entity, double x, double y, double z, float entityYaw, float partialTicks) {
		

		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	@Override
	protected ResourceLocation getEntityTexture(TameableBlaze entity) {
		return BLAZE_TEXTURE;
	}
	   

}
