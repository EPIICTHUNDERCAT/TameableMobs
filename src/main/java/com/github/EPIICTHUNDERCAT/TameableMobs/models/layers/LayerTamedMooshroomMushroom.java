package com.github.epiicthundercat.tameablemobs.models.layers;

import com.github.epiicthundercat.tameablemobs.client.render.RenderTameableMooshroom;
import com.github.epiicthundercat.tameablemobs.mobs.TameableMooshroom;
import com.github.epiicthundercat.tameablemobs.mobs.TameableMooshroom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerTamedMooshroomMushroom implements LayerRenderer<TameableMooshroom>
{
    private final RenderTameableMooshroom mooshroomRenderer;

    public LayerTamedMooshroomMushroom(RenderTameableMooshroom mooshroomRendererIn)
    {
        this.mooshroomRenderer = mooshroomRendererIn;
    }

    public void doRenderLayer(TameableMooshroom entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            this.mooshroomRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.translate(0.2F, 0.35F, 0.5F);
            GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.1F, 0.0F, -0.6F);
            GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            ((ModelQuadruped)this.mooshroomRenderer.getMainModel()).head.postRender(0.0625F);
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.translate(0.0F, 0.7F, -0.2F);
            GlStateManager.rotate(12.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.disableCull();
        }
TameableMooshroom TameableMooshroom = (TameableMooshroom) entitylivingbaseIn;
		
		if (TameableMooshroom.isSitting()) {
			
			 GlStateManager.pushMatrix();
			 GlStateManager.translate(-0.5F, -0.5F, 0.5F);
	           this.mooshroomRenderer.getMainModel();
	         GlStateManager.popMatrix();
			
			
		} else {
			
		
			 GlStateManager.pushMatrix();
			 GlStateManager.translate(0.5F, 0.5F, 0.5F);
			 this.mooshroomRenderer.getMainModel();
	            
	      
	         GlStateManager.popMatrix();
		}
        
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}