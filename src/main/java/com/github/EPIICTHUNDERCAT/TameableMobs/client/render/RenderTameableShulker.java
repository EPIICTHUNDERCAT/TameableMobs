package com.github.epiicthundercat.tameablemobs.client.render;

import com.github.epiicthundercat.tameablemobs.Reference;
import com.github.epiicthundercat.tameablemobs.mobs.TameableShulker;
import com.github.epiicthundercat.tameablemobs.models.ModelTameableShulker;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableShulker extends RenderLiving<TameableShulker>
{
    private static final ResourceLocation TAMEABLESHULKER_ENDERGOLEM_TEXTURE = new ResourceLocation(Reference.ID, "textures/entity/tameableshulker/tameableendergolem.png");
    private int modelVersion;

    public RenderTameableShulker(RenderManager manager)
    {
        super(manager, new ModelTameableShulker(), 0.0F);
        addLayer(new RenderTameableShulker.HeadLayer());
        modelVersion = 28;
        shadowSize = 0.0F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(TameableShulker entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (this.modelVersion != ((ModelTameableShulker)this.mainModel).getModelVersion())
        {
            this.mainModel = new ModelTameableShulker();
            this.modelVersion = ((ModelTameableShulker)this.mainModel).getModelVersion();
        }

        int i = entity.getClientTeleportInterp();

        if (i > 0 && entity.isAttachedToBlock())
        {
            BlockPos blockpos = entity.getAttachmentPos();
            BlockPos blockpos1 = entity.getOldAttachPos();
            double d0 = (double)((float)i - partialTicks) / 6.0D;
            d0 = d0 * d0;
            double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
            double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
            double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
            super.doRender(entity, x - d1, y - d2, z - d3, entityYaw, partialTicks);
        }
        else
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    public boolean shouldRender(TameableShulker livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
        {
            return true;
        }
        else
        {
            if (livingEntity.getClientTeleportInterp() > 0 && livingEntity.isAttachedToBlock())
            {
                BlockPos blockpos = livingEntity.getOldAttachPos();
                BlockPos blockpos1 = livingEntity.getAttachmentPos();
                Vec3d vec3d = new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
                Vec3d vec3d1 = new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());

                if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, vec3d.xCoord, vec3d.yCoord, vec3d.zCoord)))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableShulker entity)
    {
        return TAMEABLESHULKER_ENDERGOLEM_TEXTURE;
    }

    protected void rotateCorpse(TameableShulker entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);

        switch (entityLiving.getAttachmentFacing())
        {
            case DOWN:
            default:
                break;
            case EAST:
                GlStateManager.translate(0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case WEST:
                GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case NORTH:
                GlStateManager.translate(0.0F, 0.5F, -0.5F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.translate(0.0F, 0.5F, 0.5F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                break;
            case UP:
                GlStateManager.translate(0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(TameableShulker entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
    }

    @SideOnly(Side.CLIENT)
    class HeadLayer implements LayerRenderer<TameableShulker>
    {
    	public boolean isChild = true;
    	
        private HeadLayer()
        {
        }
        public void setModelAttributes(ModelBase model)
        {
           
            this.isChild = model.isChild;
        }
        public void doRenderLayer(TameableShulker entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
        {
            GlStateManager.pushMatrix();

            switch (entitylivingbaseIn.getAttachmentFacing())
            {
                case DOWN:
                default:
                    break;
                case EAST:
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(1.0F, -1.0F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case WEST:
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(-1.0F, -1.0F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case NORTH:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(0.0F, -1.0F, -1.0F);
                    break;
                case SOUTH:
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(0.0F, -1.0F, 1.0F);
                    break;
                case UP:
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.translate(0.0F, -2.0F, 0.0F);
            }

            ModelRenderer modelrenderer = ((ModelTameableShulker)RenderTameableShulker.this.getMainModel()).head;
            modelrenderer.rotateAngleY = netHeadYaw * 0.017453292F;
            modelrenderer.rotateAngleX = headPitch * 0.017453292F;
            
           RenderTameableShulker.this.bindTexture(RenderTameableShulker.TAMEABLESHULKER_ENDERGOLEM_TEXTURE);
       	if (this.isChild) {
    		float f = 2.0F;
    		GlStateManager.pushMatrix();
    		GlStateManager.translate(0.0F, 10.0F * scale, 2.0F * scale);
    		GlStateManager.scale(0.6F, 0.6F, 0.6F);
    		modelrenderer.render(scale);
    	     
    		GlStateManager.popMatrix();
    		
    	} else {
    		
    		modelrenderer.render(scale);
    	}
            
            GlStateManager.popMatrix();
        }

        public boolean shouldCombineTextures()
        {
            return false;
        }
    }
}