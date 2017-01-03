package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSilverfish;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableSilverfish;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableSilverfish extends RenderLiving<TameableSilverfish>
{
    private static final ResourceLocation TAMEABLESILVERFISH_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablesilverfish.png");

    public RenderTameableSilverfish(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableSilverfish(), 0.3F);
    }

    protected float getDeathMaxRotation(TameableSilverfish entityLivingBaseIn)
    {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableSilverfish entity)
    {
        return TAMEABLESILVERFISH_TEXTURES;
    }
}