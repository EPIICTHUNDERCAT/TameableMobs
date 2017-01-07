package com.github.epiicthundercat.tameablemobs.models.layers;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableSkeleton;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableSkeleton;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerTamedSkeletonType implements LayerRenderer<TameableSkeleton>
{
    private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/skeleton/stray_overlay.png");
    private final RenderLivingBase<?> renderer;
    private ModelTameableSkeleton layerModel;

    public LayerTamedSkeletonType(RenderLivingBase<?> p_i47131_1_)
    {
        this.renderer = p_i47131_1_;
        this.layerModel = new ModelTameableSkeleton(0.25F, true);
    }

    public void doRenderLayer(TameableSkeleton entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entitylivingbaseIn.getSkeletonType() == SkeletonType.STRAY)
        {
            this.layerModel.setModelAttributes(this.renderer.getMainModel());
            this.layerModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
            this.layerModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}
