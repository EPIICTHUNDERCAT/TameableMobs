package com.github.EPIICTHUNDERCAT.TameableMobs.models;



	import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

	@SideOnly(Side.CLIENT)
	public class ModelTameableEndermite extends ModelBase{
	
	    private static final int[][] BODY_SIZES = new int[][] {{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	    private static final int[][] BODY_TEXS = new int[][] {{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	    private static final int BODY_COUNT = BODY_SIZES.length;
	    private final ModelRenderer[] bodyParts;

	    public ModelTameableEndermite()
	    {
	        this.bodyParts = new ModelRenderer[BODY_COUNT];
	        float f = -3.5F;

	        for (int i = 0; i < this.bodyParts.length; ++i)
	        {
	            this.bodyParts[i] = new ModelRenderer(this, BODY_TEXS[i][0], BODY_TEXS[i][1]);
	            this.bodyParts[i].addBox((float)BODY_SIZES[i][0] * -0.5F, 0.0F, (float)BODY_SIZES[i][2] * -0.5F, BODY_SIZES[i][0], BODY_SIZES[i][1], BODY_SIZES[i][2]);
	            this.bodyParts[i].setRotationPoint(0.0F, (float)(24 - BODY_SIZES[i][1]), f);

	            if (i < this.bodyParts.length - 1)
	            {
	                f += (float)(BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5F;
	            }
	        }
	    }

	    /**
	     * Sets the models various rotation angles then renders the model.
	     */
	    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	    {
	        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

	        for (ModelRenderer modelrenderer : this.bodyParts)
	        {
	        	if (this.isChild) {
	        		float f = 2.0F;
	        		GlStateManager.pushMatrix();
	        		GlStateManager.translate(0.0F, 10.0F * scale, 0.0F * scale);
	        		GlStateManager.scale(0.6F, 0.6F, 0.6F);
	        		modelrenderer.render(scale);
	                
	        		GlStateManager.popMatrix();
	        	} else {
	            modelrenderer.render(scale);
	        	}
	        }
	    }

	    /**
	     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	     * "far" arms and legs can swing at most.
	     */
	    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	    {
	        for (int i = 0; i < this.bodyParts.length; ++i)
	        {
	            this.bodyParts[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.01F * (float)(1 + Math.abs(i - 2));
	            this.bodyParts[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.1F * (float)Math.abs(i - 2);
	        }
	    }
	}

