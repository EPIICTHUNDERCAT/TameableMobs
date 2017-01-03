package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import java.util.List;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableZombie;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.ModelTameableZombieVillager;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedBipedArmor;
import com.github.EPIICTHUNDERCAT.TameableMobs.models.layers.LayerTamedHeldItem;
import com.google.common.collect.Lists;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableZombie extends RenderBiped<TameableZombie>
{
    private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/tameablezombie_villager.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_FARMER_LOCATION = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/tameablezombie_farmer.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_LIBRARIAN_LOC = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/tameablezombie_librarian.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_PRIEST_LOCATION = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/tameablezombie_priest.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_SMITH_LOCATION = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/tameablezombie_smith.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_BUTCHER_LOCATION = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie_villager/ztameableombie_butcher.png");
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie/tameablezombie.png");
    private static final ResourceLocation HUSK_ZOMBIE_TEXTURES = new ResourceLocation(Reference.ID, "textures/entity/tameablezombie/tameablehusk.png");
    private final ModelBiped defaultModel;
    private final ModelTameableZombieVillager zombieVillagerModel;
    private final List<LayerRenderer<TameableZombie>> villagerLayers;
    private final List<LayerRenderer<TameableZombie>> defaultLayers;

    public RenderTameableZombie(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelTameableZombie(), 0.5F, 1.0F);
        LayerRenderer<?> layerrenderer = (LayerRenderer)this.layerRenderers.get(0);
        this.defaultModel = this.modelBipedMain;
        this.zombieVillagerModel = new ModelTameableZombieVillager();
        this.addLayer(new LayerTamedHeldItem(this));
        LayerTamedBipedArmor layerbipedarmor = new LayerTamedBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelTameableZombie(0.5F, true);
                this.modelArmor = new ModelTameableZombie(1.0F, true);
            }
        };
        this.addLayer(layerbipedarmor);
        this.defaultLayers = Lists.newArrayList(this.layerRenderers);

        if (layerrenderer instanceof LayerCustomHead)
        {
            this.removeLayer(layerrenderer);
            this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
        }

        this.removeLayer(layerbipedarmor);
        this.addLayer(new LayerVillagerArmor(this));
        this.villagerLayers = Lists.newArrayList(this.layerRenderers);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableZombie entitylivingbaseIn, float partialTickTime)
    {
        if (entitylivingbaseIn.getZombieType() == ZombieType.HUSK)
        {
            float f = 1.0625F;
            GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
        }

        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(TameableZombie entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.swapArmor(entity);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableZombie entity)
    {
        if (entity.isVillager())
        {
            return entity.getVillagerTypeForge().getZombieSkin();
        }
        else
        {
            return entity.getZombieType() == ZombieType.HUSK ? HUSK_ZOMBIE_TEXTURES : ZOMBIE_TEXTURES;
        }
    }

    private void swapArmor(TameableZombie zombie)
    {
        if (zombie.isVillager())
        {
            this.mainModel = this.zombieVillagerModel;
            this.layerRenderers = this.villagerLayers;
        }
        else
        {
            this.mainModel = this.defaultModel;
            this.layerRenderers = this.defaultLayers;
        }

        this.modelBipedMain = (ModelBiped)this.mainModel;
    }

    protected void rotateCorpse(TameableZombie entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        if (entityLiving.isConverting())
        {
            p_77043_3_ += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * Math.PI * 0.25D);
        }

        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }
}