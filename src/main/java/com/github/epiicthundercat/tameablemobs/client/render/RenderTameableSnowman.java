package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSnowman;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableSnowman;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedSnowmanHead;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableSnowman extends RenderLiving<TameableSnowman>
{
    private static final ResourceLocation SNOW_MAN_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablesnowman.png");

    public RenderTameableSnowman(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableSnowman(), 0.5F);
        this.addLayer(new LayerTamedSnowmanHead(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableSnowman entity)
    {
        return SNOW_MAN_TEXTURES;
    }

    public ModelTameableSnowman getMainModel()
    {
        return (ModelTameableSnowman)super.getMainModel();
    }
}