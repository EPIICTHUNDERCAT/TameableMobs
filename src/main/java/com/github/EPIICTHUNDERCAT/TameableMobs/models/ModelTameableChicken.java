package com.github.epiicthundercat.tameablemobs.models;

import com.github.epiicthundercat.tameablemobs.mobs.TameableChicken;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTameableChicken extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer rightLeg;
	public ModelRenderer leftLeg;
	public ModelRenderer rightWing;
	public ModelRenderer leftWing;
	public ModelRenderer bill;
	public ModelRenderer chin;

	public ModelTameableChicken() {
		int i = 16;
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.head.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.bill = new ModelRenderer(this, 14, 0);
		this.bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.bill.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.chin = new ModelRenderer(this, 14, 4);
		this.chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.chin.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.body = new ModelRenderer(this, 0, 9);
		this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.rightLeg = new ModelRenderer(this, 26, 0);
		this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.rightLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
		this.leftLeg = new ModelRenderer(this, 26, 0);
		this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.leftLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
		this.rightWing = new ModelRenderer(this, 24, 13);
		this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.rightWing.setRotationPoint(-4.0F, 13.0F, 0.0F);
		this.leftWing = new ModelRenderer(this, 24, 13);
		this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.leftWing.setRotationPoint(4.0F, 13.0F, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
			this.head.render(scale);
			this.bill.render(scale);
			this.chin.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.body.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.rightWing.render(scale);
			this.leftWing.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.head.render(scale);
			this.bill.render(scale);
			this.chin.render(scale);
			this.body.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.rightWing.render(scale);
			this.leftWing.render(scale);
		}
	}

	
	
	
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwingAmount, float ageInTicks,
			float partialTickTime) {
		TameableChicken tameableChicken = (TameableChicken) entitylivingbaseIn;

		if (tameableChicken.isSitting()) {
			
			head.offsetY = 0.09f;
			rightWing.offsetY = 0.09f;
			leftWing.offsetY = 0.09f;
			bill.offsetY = 0.09f;
			chin.offsetY = 0.09f;
			body.setRotationPoint(0.0F, 16.0F, 0.0F);
			body.offsetY = 0.09f;
			rightLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
			rightLeg.rotationPointX = -2f;
			rightLeg.rotateAngleY = 1f;
			rightLeg.rotateAngleZ = 1f;
			rightLeg.offsetY = 0.09f;
			rightLeg.rotateAngleX = 1f;
			leftLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
			leftLeg.rotateAngleY = -1f;
			leftLeg.rotateAngleZ = -1f;
			leftLeg.offsetY = 0.099f;
			
			
		} else {
			
			head.offsetY = 0.0f;
			rightWing.offsetY = 0.0f;
			leftWing.offsetY = 0.0f;
			bill.offsetY = 0.0f;
			chin.offsetY = 0.0f;
			body.setRotationPoint(0.0F, 16.0F, 0.0F);
			body.offsetY = 0.0f;
			body.rotateAngleX = ((float) Math.PI / 2F);
			rightLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
			rightLeg.rotationPointX = -2f;
			rightLeg.rotateAngleY = 0f;
			rightLeg.rotateAngleZ = 0f;
			rightLeg.offsetY = 0.0f;
			leftLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
			leftLeg.offsetY = 0.0f;
			leftLeg.rotateAngleY = 0f;
			leftLeg.rotateAngleZ = 0f;
			//rightLeg.rotateAngleX = MathHelper.cos(limbSwingAmount * 0.6662F) * 1.4F * ageInTicks;
			//leftLeg.rotateAngleX = MathHelper.cos(limbSwingAmount * 0.6662F + (float) Math.PI) * 1.4F * ageInTicks;
			
		}

	}
	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are
	 * used for animating the movement of arms and legs, where par1 represents
	 * the time(so that arms and legs swing back and forth) and par2 represents
	 * how "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.bill.rotateAngleX = this.head.rotateAngleX;
		this.bill.rotateAngleY = this.head.rotateAngleY;
		this.chin.rotateAngleX = this.head.rotateAngleX;
		this.chin.rotateAngleY = this.head.rotateAngleY;
		this.body.rotateAngleX = ((float) Math.PI / 2F);
		this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.rightWing.rotateAngleZ = ageInTicks;
		this.leftWing.rotateAngleZ = -ageInTicks;
	}
}
