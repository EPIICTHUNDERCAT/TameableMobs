package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSkeleton;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableSkeleton;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedBipedArmor;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedHeldItem;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedSkeletonType;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableSkeleton extends RenderBiped<TameableSkeleton>
{
    private static final ResourceLocation TAMEABLESKELETON_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableskeleton/tameableskeleton.png");
    private static final ResourceLocation TAMEABLEWITHER_SKELETON_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableskeleton/tameablewither_skeleton.png");
    private static final ResourceLocation TAMEABLESTRAY_SKELETON_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableskeleton/tameablestray.png");

    public RenderTameableSkeleton(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableSkeleton(), 0.5F);
        this.addLayer(new LayerTamedHeldItem(this));
        this.addLayer(new LayerTamedBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelTameableSkeleton(0.5F, true);
                this.modelArmor = new ModelTameableSkeleton(1.0F, true);
            }
        });
        this.addLayer(new LayerTamedSkeletonType(this));
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableSkeleton entitylivingbaseIn, float partialTickTime)
    {
        if (entitylivingbaseIn.getSkeletonType() == SkeletonType.WITHER)
        {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }
    }

    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableSkeleton entity)
    {
        SkeletonType skeletontype = entity.getSkeletonType();
        return skeletontype == SkeletonType.WITHER ? TAMEABLEWITHER_SKELETON_TEXTURES : (skeletontype == SkeletonType.STRAY ? TAMEABLESTRAY_SKELETON_TEXTURES : TAMEABLESKELETON_TEXTURES);
    }
}