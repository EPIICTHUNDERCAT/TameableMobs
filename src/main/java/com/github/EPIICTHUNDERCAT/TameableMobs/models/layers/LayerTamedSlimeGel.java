package com.github.EPIICTHUNDERCAT.TameableMobs.models.layers;

import com.github.EPIICTHUNDERCAT.TameableMobs.client.render.RenderTameableSlime;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableSlime;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableSlime;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerTamedSlimeGel implements LayerRenderer<TameableSlime>
{
    private final RenderTameableSlime slimeRenderer;
    private final ModelBase slimeModel = new ModelTameableSlime(0);

    public LayerTamedSlimeGel(RenderTameableSlime slimeRendererIn)
    {
        this.slimeRenderer = slimeRendererIn;
    }

    public void doRenderLayer(TameableSlime entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entitylivingbaseIn.isInvisible())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}