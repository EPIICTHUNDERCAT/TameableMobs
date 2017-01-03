package com.github.epiicthundercat.tameablemobs.models;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import com.github.epiicthundercat.tameablemobs.mobs.TMBug;
import com.github.epiicthundercat.tameablemobs.mobs.TameableBat;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSpider;

/**
 * ModelTmModelTMBug by Unknown
 */
@SideOnly(Side.CLIENT)
public class ModelTMBug extends ModelBase {
    public ModelRenderer Body;
    public ModelRenderer Head;
    public ModelRenderer Antenna1;
    public ModelRenderer AntennaTop;
    public ModelRenderer Antenna2;
    public ModelRenderer AntennaTop2;
    public ModelRenderer WingLeft;
    public ModelRenderer WingRight;
    public ModelRenderer Leg1;
    public ModelRenderer Leg1Bottom;
    public ModelRenderer Leg2;
    public ModelRenderer Leg2Bottom;
    public ModelRenderer Leg4;
    public ModelRenderer Leg4Bottom;
    public ModelRenderer Leg5;
    public ModelRenderer Leg5Bottom;
    public ModelRenderer Leg5Bottom2;
    public ModelRenderer Leg6;
    public ModelRenderer Leg6Bottom;
    public ModelRenderer Leg6Bottom2;
    public ModelRenderer Leg3;
    public ModelRenderer Leg3Bottom;

    public ModelTMBug() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.Body = new ModelRenderer(this, 42, 0);
        this.Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Body.addBox(0.0F, 0.0F, -2.0F, 7, 4, 4);
        this.Head = new ModelRenderer(this, 52, 12);
        this.Head.setRotationPoint(0.5F, 1.0F, 0.1F);
        this.Head.addBox(-2.0F, -0.5F, -1.6F, 3, 3, 3);
        this.Body.addChild(this.Head);
        this.Antenna1 = new ModelRenderer(this, 0, 29);
        this.Antenna1.setRotationPoint(-1.5F, -0.4F, 0.7F);
        this.Antenna1.addBox(-0.3F, -1.6F, -0.5F, 1, 2, 1);
        this.setRotationAngles(this.Antenna1, -0.5094616179782085F, 0.0F, 0.0F);
        this.Head.addChild(this.Antenna1);
        this.AntennaTop = new ModelRenderer(this, 5, 28);
        this.AntennaTop.setRotationPoint(0.0F, -1.2F, 0.0F);
        this.AntennaTop.addBox(-0.3F, -2.6F, -0.5F, 1, 3, 1);
        this.setRotationAngles(this.AntennaTop, 0.0F, 0.0F, -1.018923235956417F);
        this.Antenna1.addChild(this.AntennaTop);
        this.Antenna2 = new ModelRenderer(this, 0, 29);
        this.Antenna2.setRotationPoint(-1.5F, -0.4F, -0.7F);
        this.Antenna2.addBox(-0.3F, -1.7F, -0.5F, 1, 2, 1);
        this.setRotationAngles(this.Antenna2, 0.5094616179782085F, 0.0F, 0.0F);
        this.Head.addChild(this.Antenna2);
        this.AntennaTop2 = new ModelRenderer(this, 5, 28);
        this.AntennaTop2.setRotationPoint(0.0F, -1.4F, 0.0F);
        this.AntennaTop2.addBox(-0.3F, -2.6F, -0.5F, 1, 3, 1);
        this.setRotationAngles(this.AntennaTop2, 0.0F, 0.0F, -0.7641051252178287F);
        this.Antenna2.addChild(this.AntennaTop2);
        this.WingLeft = new ModelRenderer(this, 42, 28);
        this.WingLeft.setRotationPoint(1.5F, -0.1F, 1.0F);
        this.WingLeft.addBox(-0.5F, 0.0F, -1.5F, 8, 1, 3);
        this.setRotationAngles(this.WingLeft, 0.0F, -0.5942845969882637F, -0.5942845969882637F);
        this.Body.addChild(this.WingLeft);
        this.WingRight = new ModelRenderer(this, 42, 28);
        this.WingRight.setRotationPoint(1.5F, 0.1F, -1.0F);
        this.WingRight.addBox(0.0F, 0.0F, -1.4F, 8, 1, 3);
        this.WingRight.mirror = true;
        this.setRotationAngles(this.WingRight, 0.0F, 0.5942845969882637F, -0.5942845969882637F);
        this.Body.addChild(this.WingRight);
        this.Leg1 = new ModelRenderer(this, 22, 30);
        this.Leg1.setRotationPoint(1.0F, 3.6F, 0.4F);
        this.Leg1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Body.addChild(this.Leg1);
        this.Leg1Bottom = new ModelRenderer(this, 17, 29);
        this.Leg1Bottom.setRotationPoint(0.4F, 1.1F, 0.0F);
        this.Leg1Bottom.addBox(-0.4F, -0.1F, 0.0F, 1, 2, 1);
        this.Leg1Bottom.mirror = true;
        this.setRotationAngles(this.Leg1Bottom, 0.2972295835988592F, -0.04241150198859518F, -0.5942845969882637F);
        this.Leg1.addChild(this.Leg1Bottom);
        this.Leg2 = new ModelRenderer(this, 22, 30);
        this.Leg2.setRotationPoint(1.0F, 3.6F, -1.4F);
        this.Leg2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Leg2.mirror = true;
        this.Body.addChild(this.Leg2);
        this.Leg2Bottom = new ModelRenderer(this, 17, 29);
        this.Leg2Bottom.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.Leg2Bottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
        this.Leg2Bottom.mirror = true;
        this.setRotationAngles(this.Leg2Bottom, -0.08482300397719036F, -0.04241150198859518F, -0.38205256260891435F);
        this.Leg2.addChild(this.Leg2Bottom);
        this.Leg4 = new ModelRenderer(this, 22, 30);
        this.Leg4.setRotationPoint(3.0F, 3.6F, -1.4F);
        this.Leg4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Leg4.mirror = true;
        this.Body.addChild(this.Leg4);
        this.Leg4Bottom = new ModelRenderer(this, 17, 29);
        this.Leg4Bottom.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.Leg4Bottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
        this.Leg4Bottom.mirror = true;
        this.setRotationAngles(this.Leg4Bottom, 0.2546435405291338F, 0.2546435405291338F, -0.2546435405291338F);
        this.Leg4.addChild(this.Leg4Bottom);
        this.Leg5 = new ModelRenderer(this, 22, 30);
        this.Leg5.setRotationPoint(5.0F, 3.6F, 0.4F);
        this.Leg5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Leg5.mirror = true;
        this.Body.addChild(this.Leg5);
        this.Leg5Bottom = new ModelRenderer(this, 17, 29);
        this.Leg5Bottom.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.Leg5Bottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
        this.Leg5Bottom.mirror = true;
        this.setRotationAngles(this.Leg5Bottom, -0.12740903872453743F, -0.21223203437934937F, -0.38205256260891435F);
        this.Leg5.addChild(this.Leg5Bottom);
        this.Leg5Bottom2 = new ModelRenderer(this, 12, 28);
        this.Leg5Bottom2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Leg5Bottom2.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
        this.Leg5Bottom2.mirror = true;
        this.setRotationAngles(this.Leg5Bottom2, 0.0F, -0.21223203437934937F, -0.67928211291826F);
        this.Leg5Bottom.addChild(this.Leg5Bottom2);
        this.Leg6 = new ModelRenderer(this, 22, 30);
        this.Leg6.setRotationPoint(5.0F, 3.4F, -1.4F);
        this.Leg6.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Leg6.mirror = true;
        this.Body.addChild(this.Leg6);
        this.Leg6Bottom = new ModelRenderer(this, 17, 29);
        this.Leg6Bottom.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.Leg6Bottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
        this.Leg6Bottom.mirror = true;
        this.setRotationAngles(this.Leg6Bottom, 0.04241150198859518F, -0.08482300397719036F, -0.08482300397719036F);
        this.Leg6.addChild(this.Leg6Bottom);
        this.Leg6Bottom2 = new ModelRenderer(this, 12, 28);
        this.Leg6Bottom2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Leg6Bottom2.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
        this.setRotationAngles(this.Leg6Bottom2, -0.21223203437934937F, 0.169820528229565F, -0.67928211291826F);
        this.Leg6Bottom.addChild(this.Leg6Bottom2);
        this.Leg3 = new ModelRenderer(this, 22, 30);
        this.Leg3.setRotationPoint(3.0F, 3.6F, 0.4F);
        this.Leg3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.Leg3.mirror = true;
        this.Body.addChild(this.Leg3);
        this.Leg3Bottom = new ModelRenderer(this, 17, 29);
        this.Leg3Bottom.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.Leg3Bottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
        this.Leg3Bottom.mirror = true;
        this.setRotationAngles(this.Leg3Bottom, -0.2972295835988592F, -0.169820528229565F, -0.5518731241279929F);
        this.Leg3.addChild(this.Leg3Bottom);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
    	if (this.isChild) {
			float f = 2.0F;
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translate(0.0F, f * scale, f * scale);
			this.Body.render(scale);
			GlStateManager.popMatrix();
			
		} else {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translate(0F, 1.6f, -0.29f);
			//GlStateManager.rotate(limbSwing, 0.0f, 0.1f, 0.0f);
			this.Body.render(scale);
			GlStateManager.popMatrix();
		
		}
        
  
    }

    public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
    	
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        if (((TMBug)entityIn).getIsBugOnGround())
        {
        	this.Head.rotateAngleX = 0;
            this.Head.rotateAngleY = 0;
            this.Head.rotateAngleZ = 0;
            this.Head.setRotationPoint(0.5F, 1.0F, 0.1F);
            this.WingRight.setRotationPoint(1.5F, 0.1F, -1.0F);
            this.WingLeft.setRotationPoint(1.5F, -0.1F, 1.0F);
            this.Body.rotateAngleY = -1.5f;
            this.Body.rotateAngleX = 0;
            this.WingRight.rotateAngleX = 0;
            this.WingRight.rotateAngleY = 0.34F;
           // this.batOuterRightWing.rotateAngleY = -1.7278761F;
            this.WingLeft.rotateAngleX = 0;
           this.WingLeft.rotateAngleY = -0.34F;
           // this.batOuterLeftWing.rotateAngleY = -this.batOuterRightWing.rotateAngleY;
        }
        else
        {
            this.Head.rotateAngleX = headPitch * 0.017453292F;
            this.Head.rotateAngleY = netHeadYaw * 0.017453292F;
            this.Head.rotateAngleZ = 0.0F;
            this.Head.setRotationPoint(0.5F, 1.0F, 0.1F);
            this.WingRight.setRotationPoint(1.5F, 0.1F, -1.0F);
            this.WingLeft.setRotationPoint(1.5F, -0.1F, 1.0F);
            this.Body.rotateAngleX = 0;
            this.Body.rotateAngleY = 0.0F;
            this.WingRight.rotateAngleY = MathHelper.cos(ageInTicks * 1.3F) * (float)Math.PI * 0.25F;
            this.WingLeft.rotateAngleY = -this.WingRight.rotateAngleY;
          //  this.batOuterRightWing.rotateAngleY = this.WingRight.rotateAngleY * 0.5F;
          //  this.batOuterLeftWing.rotateAngleY = -this.WingRight.rotateAngleY * 0.5F;
        }
    }
}
