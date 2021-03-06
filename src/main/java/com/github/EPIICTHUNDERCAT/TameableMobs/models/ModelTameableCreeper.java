package com.github.epiicthundercat.tameablemobs.models;

import com.github.epiicthundercat.tameablemobs.mobs.TameableCreeper;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTameableCreeper extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer creeperArmor;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelTameableCreeper()
    {
        this(0.0F);
    }

    public ModelTameableCreeper(float p_i46366_1_)
    {
        int i = 6;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_);
        this.head.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.creeperArmor = new ModelRenderer(this, 32, 0);
        this.creeperArmor.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_ + 0.5F);
        this.creeperArmor.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46366_1_);
        this.body.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.leg1 = new ModelRenderer(this, 0, 16);
        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg1.setRotationPoint(-2.0F, 18.0F, 4.0F);
        this.leg2 = new ModelRenderer(this, 0, 16);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg2.setRotationPoint(2.0F, 18.0F, 4.0F);
        this.leg3 = new ModelRenderer(this, 0, 16);
        this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg3.setRotationPoint(-2.0F, 18.0F, -4.0F);
        this.leg4 = new ModelRenderer(this, 0, 16);
        this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg4.setRotationPoint(2.0F, 18.0F, -4.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        if (this.isChild) {
    		float f = 2.0F;
    		GlStateManager.pushMatrix();
    		GlStateManager.translate(0.0F, 10.0F * scale, 2.0F * scale);
    		GlStateManager.scale(0.6F, 0.6F, 0.6F);
            this.head.render(scale);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
    		GlStateManager.popMatrix();
    	} else {
        this.head.render(scale);
        this.body.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.leg4.render(scale);
    }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
    
    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwingAmount, float ageInTicks, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwingAmount, ageInTicks, partialTickTime);
       
        TameableCreeper TameableCreeper = (TameableCreeper) entitylivingbaseIn;
        int height = 0;
		if (TameableCreeper.isSitting()) {


			
			head.setRotationPoint(0.0F, 6.0F, 0.0F);
			body.setRotationPoint(0.0F, 6.0F, 0.0F);
			body.offsetY = 0f;
			body.rotateAngleY = 0f;
			body.rotateAngleZ = 0f;
			body.rotateAngleX = 0f;
			leg1.setRotationPoint(-2.0F, 18.0F, 4.0F);
			leg1.rotationPointX = -2f;
			leg1.rotateAngleY = 1f;
			leg1.rotateAngleZ = 1f;
			leg1.offsetY = 0.09f;
			leg1.rotateAngleX = 1f;
			leg2.setRotationPoint(2.0F, 18.0F, 4.0F);
			leg2.rotateAngleY = -1f;
			leg2.rotateAngleZ = -1f;
			leg2.offsetY = 0.099f;
			leg3.setRotationPoint(-2.0F, 18.0F, -4.0F);
			leg3.rotateAngleY = 1f;
			leg3.rotateAngleZ = 1f;
			leg3.offsetY = 0.099f;
			
			leg4.setRotationPoint(2.0F, 18.0F, -4.0F);
			
			leg4.rotateAngleY = -1f;
			leg4.rotateAngleZ = -1f;
			leg4.rotateAngleX = -1f;
			leg4.offsetY = 0.099f;
			
			
		} else {
			
			head.setRotationPoint(0.0F, 6.0F, 0.0F);
			body.setRotationPoint(0.0F, 6.0F, 0.0F);
			
			
			leg1.setRotationPoint(-2.0F, 18.0F, 4.0F);
			leg1.rotationPointX = -2f;
			leg1.rotateAngleY = 0f;
			leg1.rotateAngleZ = 0f;
			leg1.offsetY = 0.0f;
			leg2.setRotationPoint(2.0F, 18.0F, 4.0F);
			leg2.offsetY = 0.0f;
			leg2.rotateAngleY = 0f;
			leg2.rotateAngleZ = 0f;
			leg3.setRotationPoint(-2.0F, 18.0F, -4.0F);
			leg3.rotateAngleY = 0f;
			leg3.rotateAngleZ = 0f;
			leg3.offsetY = 0.f;
			
			leg4.setRotationPoint(2.0F, 18.0F, -4.0F);
			leg4.rotateAngleY = 0f;
			leg4.rotateAngleZ = 0f;
			leg4.offsetY = 0.0f;
		}
        
        
        
        
        
        
        
        
    }
}