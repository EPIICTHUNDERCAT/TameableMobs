package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableVillager;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableVillager;
import com.github.epiicthundercat.tameablemobs.models.layers.LayerTamedCustomHead;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableVillager extends RenderLiving<TameableVillager>
{
    private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/villager.png");
    private static final ResourceLocation FARMER_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/farmer.png");
    private static final ResourceLocation LIBRARIAN_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/librarian.png");
    private static final ResourceLocation PRIEST_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/priest.png");
    private static final ResourceLocation SMITH_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/smith.png");
    private static final ResourceLocation BUTCHER_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/villager/butcher.png");

    public RenderTameableVillager(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableVillager(0.0F), 0.5F);
        this.addLayer(new LayerTamedCustomHead(this.getMainModel().villagerHead));
    }

    public ModelTameableVillager getMainModel()
    {
        return (ModelTameableVillager)super.getMainModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableVillager entity)
    {
        return entity.getProfessionForge().getSkin();
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableVillager entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.9375F;

        if (entitylivingbaseIn.getGrowingAge() < 0)
        {
            f = (float)((double)f * 0.5D);
            this.shadowSize = 0.25F;
        }
        else
        {
            this.shadowSize = 0.5F;
        }

        GlStateManager.scale(f, f, f);
    }
}