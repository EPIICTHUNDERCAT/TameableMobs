package com.github.EPIICTHUNDERCAT.TameableMobs.models.layers;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameablePig;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePig;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameablePig;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class LayerTamedPigSaddle implements LayerRenderer<TameablePig>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.ID, "textures/entity/tameablepig/tameablepig_saddle.png");
    private final RenderTameablePig pigRenderer;
    private final ModelTameablePig pigModel = new ModelTameablePig(0.5F);

    public LayerTamedPigSaddle(RenderTameablePig pigRendererIn)
    {
        this.pigRenderer = pigRendererIn;
    }

    public void doRenderLayer(TameablePig entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entitylivingbaseIn.getSaddled())
        {
            this.pigRenderer.bindTexture(TEXTURE);
            this.pigModel.setModelAttributes(this.pigRenderer.getMainModel());
            this.pigModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}