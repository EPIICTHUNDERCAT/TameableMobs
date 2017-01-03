package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameablePig;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableBlaze;
import com.github.epiicthundercat.tameablemobs.models.ModelTameablePig;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedPigSaddle;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class RenderTameablePig extends RenderLiving<TameablePig>
{
    private static final ResourceLocation TAMEABLEPIG_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablepig/tameablepig.png");

    
    private final ModelTameablePig pigModel;
    
    public RenderTameablePig(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameablePig(), 0.7F);
        pigModel = (ModelTameablePig) super.mainModel;
        this.addLayer(new LayerTamedPigSaddle(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameablePig entity)
    {
        return TAMEABLEPIG_TEXTURES;
    }
}