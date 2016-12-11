package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEndermite;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableEndermite;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderTameableEndermite extends RenderLiving<TameableEndermite>
{
	  private static final ResourceLocation TAMEABLEENDERMITE_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableendermite.png");

	    public RenderTameableEndermite(RenderManager rm)
	    {
	        super(rm, new ModelTameableEndermite(), 0.3F);
	    }

	   

		protected float getDeathMaxRotation(TameableEndermite entityLivingBaseIn)
	    {
	        return 180.0F;
	    }

	    /**
	     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	     */
	    protected ResourceLocation getEntityTexture(TameableEndermite entity)
	    {
	        return TAMEABLEENDERMITE_TEXTURES;
	    }
}
