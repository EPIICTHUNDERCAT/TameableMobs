package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableGiantZombie;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableZombie;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedBipedArmor;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedHeldItem;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableGiantZombie extends RenderLiving<TameableGiantZombie>
{
    private static final ResourceLocation TAMEABLEZOMBIE_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie/tameablezombie.png");
    /** Scale of the model to use */
    private final float scale;

    public RenderTameableGiantZombie(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn, float scaleIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn * scaleIn);
        this.scale = scaleIn;
        this.addLayer(new LayerTamedHeldItem(this));
        this.addLayer(new LayerTamedBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelTameableZombie(0.5F, true);
                this.modelArmor = new ModelTameableZombie(1.0F, true);
            }
        });
    }

    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableGiantZombie entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(this.scale, this.scale, this.scale);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableGiantZombie entity)
    {
        return TAMEABLEZOMBIE_TEXTURES;
    }
}