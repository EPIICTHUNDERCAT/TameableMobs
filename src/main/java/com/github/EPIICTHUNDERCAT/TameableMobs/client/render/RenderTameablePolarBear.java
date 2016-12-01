package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePolarBear;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameablePolarBear;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class RenderTameablePolarBear extends RenderLiving<TameablePolarBear>{

	private static final ResourceLocation POLARBEAR_TEXTURE = new ResourceLocation(Reference.ID, "textures/entity/tameablebear/tameablepolarbear.png");
	
	
	private final ModelTameablePolarBear polarbearModel;
	
	
	public RenderTameablePolarBear(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelTameablePolarBear(), 0.7F);
		polarbearModel = (ModelTameablePolarBear) super.mainModel;
	}
	@Override
	public void doRender(TameablePolarBear entity, double x, double y, double z, float entityYaw, float partialTicks) {
		

		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	@Override
	protected ResourceLocation getEntityTexture(TameablePolarBear entity) {
		return POLARBEAR_TEXTURE;
	}
	   protected void preRenderCallback(TameablePolarBear entitylivingbaseIn, float partialTickTime)
	    {
	        GlStateManager.scale(1.2F, 1.2F, 1.2F);
	        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	    }

}
