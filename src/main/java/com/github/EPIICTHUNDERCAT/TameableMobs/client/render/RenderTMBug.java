package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TMBug;
import com.github.epiicthundercat.tameablemobs.models.ModelTMBug;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTMBug extends RenderLiving<TMBug> {


    private static final ResourceLocation BUG_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/bug.png");

    public RenderTMBug(RenderManager renderManagerIn)
    {
    	
        super(renderManagerIn, new ModelTMBug(), 0.1F);
        
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TMBug entity)
    {
        return BUG_TEXTURES;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TMBug entitylivingbaseIn, float partialTickTime)
    {
    	
      //  GlStateManager.scale(0.35F, 0.35F, 0.35F);
     
    }

    protected void rotateCorpse(TMBug entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
       

        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }
}