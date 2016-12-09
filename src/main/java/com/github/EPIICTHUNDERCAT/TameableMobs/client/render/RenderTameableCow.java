package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableCow;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableCow;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderTameableCow extends RenderLiving<TameableCow>
{
    private static final ResourceLocation TAMEABLECOW_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablecow/tameablecow.png");

    
    private final ModelTameableCow cowModel;
	
    
    public RenderTameableCow(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelTameableCow(), 0.7F);
		cowModel = (ModelTameableCow) super.mainModel;
	}


    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableCow entity)
    {
        return TAMEABLECOW_TEXTURES;
    }
}