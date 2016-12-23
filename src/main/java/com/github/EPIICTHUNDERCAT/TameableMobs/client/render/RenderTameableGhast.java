package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableGhast;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableGhast;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableGhast extends RenderLiving<TameableGhast>
{
    private static final ResourceLocation TAMEABLEGHAST_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableghast/tameableghast.png");
    private static final ResourceLocation TAMEABLEGHAST_SHOOTING_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableghast/tameableghast_shooting.png");

    public RenderTameableGhast(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableGhast(), 0.5F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableGhast entity)
    {
        return entity.isAttacking() ? TAMEABLEGHAST_SHOOTING_TEXTURES : TAMEABLEGHAST_TEXTURES;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableGhast entitylivingbaseIn, float partialTickTime)
    {
        float f = 1.0F;
        float f1 = 4.5F;
        float f2 = 4.5F;
        GlStateManager.scale(4.5F, 4.5F, 4.5F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}