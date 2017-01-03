package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableChicken;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableChicken;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableChicken extends RenderLiving<TameableChicken>{

	private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(Reference.ID, "textures/entity/tameablechicken.png");
	
	
	private final ModelTameableChicken chickenModel;
	

	public RenderTameableChicken(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelTameableChicken(), 0.2F);
		chickenModel = (ModelTameableChicken) super.mainModel;
	}
	@Override
	public void doRender(TameableChicken entity, double x, double y, double z, float entityYaw, float partialTicks) {
		

		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	@Override
	protected ResourceLocation getEntityTexture(TameableChicken entity) {
		return CHICKEN_TEXTURE;
	}
	 protected float handleRotationFloat(TameableChicken livingBase, float partialTicks)
	    {
	        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
	        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
	        return (MathHelper.sin(f) + 1.0F) * f1;
	    }

}
