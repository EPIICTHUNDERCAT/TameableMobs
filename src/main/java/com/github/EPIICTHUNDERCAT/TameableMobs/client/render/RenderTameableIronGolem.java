package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableIronGolem;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableIronGolem;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedIronGolemFlower;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableIronGolem extends RenderLiving<TameableIronGolem>
{
    private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameableiron_golem.png");

    public RenderTameableIronGolem(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableIronGolem(), 0.5F);
        this.addLayer(new LayerTamedIronGolemFlower(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableIronGolem entity)
    {
        return IRON_GOLEM_TEXTURES;
    }

    protected void rotateCorpse(TameableIronGolem entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D)
        {
            float f = 13.0F;
            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }
}