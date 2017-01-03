package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameableRabbit;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameableRabbit extends RenderLiving<TameableRabbit>
{
    private static final ResourceLocation BROWN = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/brown.png");
    private static final ResourceLocation WHITE = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/white.png");
    private static final ResourceLocation BLACK = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/black.png");
    private static final ResourceLocation GOLD = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/gold.png");
    private static final ResourceLocation SALT = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/salt.png");
    private static final ResourceLocation WHITE_SPLOTCHED = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/white_splotched.png");
    private static final ResourceLocation TOAST = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/toast.png");
    private static final ResourceLocation CAERBANNOG = new ResourceLocation(Reference.ID, "textures/entity/tameablerabbit/caerbannog.png");

    public RenderTameableRabbit(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(TameableRabbit entity)
    {
        String s = TextFormatting.getTextWithoutFormattingCodes(entity.getName());

        if (s != null && "Toast".equals(s))
        {
            return TOAST;
        }
        else
        {
            switch (entity.getRabbitType())
            {
                case 0:
                default:
                    return BROWN;
                case 1:
                    return WHITE;
                case 2:
                    return BLACK;
                case 3:
                    return WHITE_SPLOTCHED;
                case 4:
                    return GOLD;
                case 5:
                    return SALT;
                case 99:
                    return CAERBANNOG;
            }
        }
    }
}