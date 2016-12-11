package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSpider;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.LayerTamedSpiderEyes;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableSpider;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableSpider<T extends TameableSpider> extends RenderLiving<T>
{
    private static final ResourceLocation TAMEABLESPIDER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablespider/tameablespider.png");

    public RenderTameableSpider(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableSpider(), 1.0F);
        this.addLayer(new LayerTamedSpiderEyes(this));
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn)
    {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity)
    {
        return TAMEABLESPIDER_TEXTURES;
    }
}