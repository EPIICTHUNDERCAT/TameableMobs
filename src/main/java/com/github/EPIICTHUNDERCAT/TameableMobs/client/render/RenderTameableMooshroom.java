package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableMooshroom;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedMooshroomMushroom;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableMooshroom extends RenderLiving<TameableMooshroom>
{
    private static final ResourceLocation MOOSHROOM_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablecow/tameablemooshroom.png");

    public RenderTameableMooshroom(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        this.addLayer(new LayerTamedMooshroomMushroom(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableMooshroom entity)
    {
        return MOOSHROOM_TEXTURES;
    }
}