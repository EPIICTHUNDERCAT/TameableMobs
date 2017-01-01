package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableMagmaCube;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableMagmaCube;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableMagmaCube extends RenderLiving<TameableMagmaCube>
{
    private static final ResourceLocation TAMEABLEMAGMA_CUBE_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableslime/tameablemagmacube.png");

    public RenderTameableMagmaCube(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableMagmaCube(), 0.25F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableMagmaCube entity)
    {
        return TAMEABLEMAGMA_CUBE_TEXTURES;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableMagmaCube entitylivingbaseIn, float partialTickTime)
    {
        int i = entitylivingbaseIn.getSlimeSize();
        float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / ((float)i * 0.5F + 1.0F);
        float f1 = 1.0F / (f + 1.0F);
        GlStateManager.scale(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
    }
}