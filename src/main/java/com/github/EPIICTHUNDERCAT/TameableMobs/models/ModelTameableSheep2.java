package com.github.epiicthundercat.tameablemobs.models;


import com.github.epiicthundercat.tameablemobs.mobs.TameableSheep;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelTameableSheep2 extends ModelQuadruped
{
    private float headRotationAngleX;

    public ModelTameableSheep2()
    {
        super(12, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
        this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwingAmount, float ageInTicks, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwingAmount, ageInTicks, partialTickTime);
        this.head.rotationPointY = 6.0F + ((TameableSheep)entitylivingbaseIn).getHeadRotationPointY(partialTickTime) * 9.0F;
        this.headRotationAngleX = ((TameableSheep)entitylivingbaseIn).getHeadRotationAngleX(partialTickTime);
        
        TameableSheep tameableSheep = (TameableSheep) entitylivingbaseIn;
        int height = 0;
		if (tameableSheep.isSitting()) {


			
			head.setRotationPoint(0.0F, 9.0F, -8.0F);
			body.setRotationPoint(0.0F, 8.0F, 2.0F);
			body.offsetY = 0f;
			body.rotateAngleY = 0f;
			body.rotateAngleZ = 0f;
			body.rotateAngleX = 0f;
			leg1.setRotationPoint(-2.0F, 14.0F, 7.0F);
			leg1.rotationPointX = -2f;
			leg1.rotateAngleY = 1f;
			leg1.rotateAngleZ = 1f;
			leg1.offsetY = 0.09f;
			leg1.rotateAngleX = 1f;
			leg2.setRotationPoint(2.0F, 14.0F, 7.0F);
			leg2.rotateAngleY = -1f;
			leg2.rotateAngleZ = -1f;
			leg2.offsetY = 0.099f;
			leg3.setRotationPoint(-2.0F, 14.0F, -5.0F);
			leg3.rotateAngleY = 1f;
			leg3.rotateAngleZ = 1f;
			leg3.offsetY = 0.099f;
			
			leg4.setRotationPoint(2.0F, 14.0F, -5.0F);
			
			leg4.rotateAngleY = -1f;
			leg4.rotateAngleZ = -1f;
			leg4.rotateAngleX = -1f;
			leg4.offsetY = 0.099f;
			
			
		} else {
			
			head.offsetY = 0.f;
			body.setRotationPoint(0.0F, 5.0F, 2.0F);
			
			
			leg1.setRotationPoint(-3.0F, 12.0F, 7.0F);
			leg1.rotationPointX = -2f;
			leg1.rotateAngleY = 0f;
			leg1.rotateAngleZ = 0f;
			leg1.offsetY = 0.0f;
			leg2.setRotationPoint(3.0F, 12.0F, 7.0F);
			leg2.offsetY = 0.0f;
			leg2.rotateAngleY = 0f;
			leg2.rotateAngleZ = 0f;
			leg3.setRotationPoint(-3.0F, 12.0F, -5.0F);
			leg3.rotateAngleY = 0f;
			leg3.rotateAngleZ = 0f;
			leg3.offsetY = 0.f;
			
			leg4.setRotationPoint(3.0F, 12.0F, -5.0F);
			leg4.rotateAngleY = 0f;
			leg4.rotateAngleZ = 0f;
			leg4.offsetY = 0.0f;
		}
        
        
        
        
        
        
        
        
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.head.rotateAngleX = this.headRotationAngleX;
    }
}