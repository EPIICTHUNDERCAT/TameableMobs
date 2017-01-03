package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import java.util.Random;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableEnderman;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableEnderman;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedEndermanEye;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedEndermanHeldBlock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableEnderman extends RenderLiving<TameableEnderman>
{

	 private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableenderman/tameableenderman.png");
	    /** The model of the enderman */
	    private final ModelTameableEnderman endermanModel;
	    private final Random rnd = new Random();

	    public RenderTameableEnderman(RenderManager renderManagerIn)
	    {
	        super(renderManagerIn, new ModelTameableEnderman(0.0F), 0.5F);
	        this.endermanModel = (ModelTameableEnderman)super.mainModel;
	        this.addLayer(new LayerTamedEndermanEye(this));
	        this.addLayer(new LayerTamedEndermanHeldBlock(this));
	    }

	    /**
	     * Renders the desired {@code T} type Entity.
	     */
	    public void doRender(TameableEnderman entity, double x, double y, double z, float entityYaw, float partialTicks)
	    {
	        IBlockState iblockstate = entity.getHeldBlockState();
	        this.endermanModel.isCarrying = iblockstate != null;
	        this.endermanModel.isAttacking = entity.isScreaming();

	        if (entity.isScreaming())
	        {
	            double d0 = 0.02D;
	            x += this.rnd.nextGaussian() * 0.02D;
	            z += this.rnd.nextGaussian() * 0.02D;
	        }

	        super.doRender(entity, x, y, z, entityYaw, partialTicks);
	    }

	    /**
	     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	     */
	    protected ResourceLocation getEntityTexture(TameableEnderman entity)
	    {
	        return ENDERMAN_TEXTURES;
	    }
	
	
	
	
	
	
	
}
