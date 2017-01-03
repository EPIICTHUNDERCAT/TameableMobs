package com.github.epiicthundercat.tameablemobs.models;

import com.github.epiicthundercat.tameablemobs.mobs.TameablePig;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ModelTameablePig extends ModelQuadruped
{
    public ModelTameablePig()
    {
        this(0.0F);
    }

    public ModelTameablePig(float scale)
    {
        super(6, scale);
        this.head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, scale);
        this.childYOffset = 4.0F;
   
    }
    
    
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwingAmount, float ageInTicks, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwingAmount, ageInTicks, partialTickTime);
       
        TameablePig TameablePig = (TameablePig) entitylivingbaseIn;
        int height = 0;
		if (TameablePig.isSitting()) {


			
			head.setRotationPoint(0.0F, 9.0F, -6.0F);
			head.offsetY = 0.199f;
			body.setRotationPoint(0.0F, 8.0F, 2.0F);
			body.offsetY = 0.199f;
			body.rotateAngleY = 0f;
			body.rotateAngleZ = 0f;
			body.rotateAngleX = 0f;
			leg1.setRotationPoint(-2.0F, 14.0F, 7.0F);
			
			leg1.rotateAngleY = 1f;
			leg1.rotateAngleZ = 1f;
			leg1.offsetY = 0.299f;
			leg1.rotateAngleX = 1f;
			leg2.setRotationPoint(2.0F, 14.0F, 7.0F);
			leg2.rotateAngleY = -1f;
			leg2.rotateAngleZ = -1f;
			leg2.offsetY = 0.299f;
			leg3.setRotationPoint(-2.0F, 14.0F, -5.0F);
			leg3.rotateAngleY = 1f;
			leg3.rotateAngleZ = 1f;
			leg3.offsetY = 0.299f;
			
			leg4.setRotationPoint(2.0F, 14.0F, -5.0F);
			
			leg4.rotateAngleY = -1f;
			leg4.rotateAngleZ = -1f;
			leg4.rotateAngleX = -1f;
			leg4.offsetY = 0.299f;
			
			
		} else {
			
			head.offsetY = 0.f;
			head.setRotationPoint(0.0F, 12.0F, -6.0F);
			body.setRotationPoint(0.0F, 11.0F, 2.0F);
			body.offsetY = 0f;
			
			leg1.setRotationPoint(-3.0F, 18.0F, 7.0F);
			
			leg1.rotateAngleY = 0f;
			leg1.rotateAngleZ = 0f;
			leg1.offsetY = 0.0f;
			leg2.setRotationPoint(3.0F, 18.0F, 7.0F);
			leg2.offsetY = 0.0f;
			leg2.rotateAngleY = 0f;
			leg2.rotateAngleZ = 0f;
			leg3.setRotationPoint(-3.0F, 18.0F, -5.0F);
			leg3.rotateAngleY = 0f;
			leg3.rotateAngleZ = 0f;
			leg3.offsetY = 0.f;
			
			leg4.setRotationPoint(3.0F, 18.0F, -5.0F);
			leg4.rotateAngleY = 0f;
			leg4.rotateAngleZ = 0f;
			leg4.offsetY = 0.0f;
		}
        
        
        
        
        
        
        
        
    }
    
    
    
    
    
    
}
