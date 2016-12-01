package com.github.EPIICTHUNDERCAT.TameableMobs.models;

import net.minecraft.client.model.ModelQuadruped;

public class ModelTameablePig extends ModelQuadruped
{
    public ModelTameablePig()
    {
        this(0.0F);
    }

    public ModelTameablePig(float scale)
    {
        super(6, scale);
        this.head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, scale);
        this.childYOffset = 4.0F;
    }
}