package com.github.EPIICTHUNDERCAT.TameableMobs.models;

import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCow;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTameableCow extends ModelQuadruped {
	public ModelTameableCow() {
		super(12, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.head.setTextureOffset(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.head.setTextureOffset(22, 0).addBox(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.body = new ModelRenderer(this, 18, 4);
		this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.body.setTextureOffset(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4, 6, 1);
		--this.leg1.rotationPointX;
		++this.leg2.rotationPointX;
		this.leg1.rotationPointZ += 0.0F;
		this.leg2.rotationPointZ += 0.0F;
		--this.leg3.rotationPointX;
		++this.leg4.rotationPointX;
		--this.leg3.rotationPointZ;
		--this.leg4.rotationPointZ;
		this.childZOffset += 2.0F;
	}

	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwingAmount, float ageInTicks,
			float partialTickTime) {
		super.setLivingAnimations(entitylivingbaseIn, limbSwingAmount, ageInTicks, partialTickTime);

		TameableCow TameableCow = (TameableCow) entitylivingbaseIn;
		int height = 0;
		if (TameableCow.isSitting()) {

			
			body.setRotationPoint(0.0F, 5.0F, 2.0F);
			body.offsetY = 0.2999f;
			head.setRotationPoint(0.0F, 4.0F, -8.0F);
			head.offsetY = 0.299f;
			leg1.setRotationPoint(-4.0F, 12.0F, 7.0F);
			
			leg1.rotateAngleY = 1f;
			leg1.rotateAngleZ = 1f;
			leg1.offsetY = 0.29f;
			leg1.rotateAngleX = 1f;
			leg2.setRotationPoint(4.0F, 12.0F, 7.0F);
			leg2.rotateAngleY = -1f;
			leg2.rotateAngleZ = -1f;
			leg2.offsetY = 0.29f;
			leg3.setRotationPoint(-4.0F, 12.0F, -6.0F);
			leg3.rotateAngleY = 1f;
			leg3.rotateAngleZ = 1f;
			leg3.offsetY = 0.29f;

			leg4.setRotationPoint(4.0F, 12.0F, -6.0F);

			leg4.rotateAngleY = -1f;
			leg4.rotateAngleZ = -1f;
			leg4.rotateAngleX = -1f;
			leg4.offsetY = 0.29f;

		} else {
			head.setRotationPoint(0.0F, 4.0F, -8.0F);
			head.offsetY = 0;
			body.setRotationPoint(0.0F, 5.0F, 2.0F);
			body.offsetY = 0.0f;
			leg1.setRotationPoint(-4.0F, 12.0F, 7.0F);
			
			leg1.rotateAngleY = 0f;
			leg1.rotateAngleZ = 0f;
			leg1.offsetY = 0.0f;
			leg2.setRotationPoint(4.0F, 12.0F, 7.0F);
			leg2.offsetY = 0.0f;
			leg2.rotateAngleY = 0f;
			leg2.rotateAngleZ = 0f;
			leg3.setRotationPoint(-4.0F, 12.0F, -6.0F);
			leg3.rotateAngleY = 0f;
			leg3.rotateAngleZ = 0f;
			leg3.offsetY = 0.f;
			
			leg4.setRotationPoint(4.0F, 12.0F, -6.0F);
			leg4.rotateAngleY = 0f;
			leg4.rotateAngleZ = 0f;
			leg4.offsetY = 0.0f;
			
		}

	}
}