package com.github.EPIICTHUNDERCAT.TameableMobs.client.render;

import com.github.EPIICTHUNDERCAT.TameableMobs.Reference;
import com.github.EPIICTHUNDERCAT.TameableMobs.mobs.TameablePigZombie;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTameablePigZombie extends RenderBiped<TameablePigZombie> {
	private static final ResourceLocation TAMEABLEZOMBIE_PIGMAN_TEXTURE = new ResourceLocation(Reference.ID,
			"textures/entity/tameablezombie_pigman.png");

	public RenderTameablePigZombie(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelZombie(), 0.5F, 1.0F);
		addLayer(new LayerHeldItem(this));
		addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				modelLeggings = new ModelZombie(0.5F, true);
				modelArmor = new ModelZombie(1.0F, true);
			}
		});
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(TameablePigZombie entity) {
		return TAMEABLEZOMBIE_PIGMAN_TEXTURE;
	}
}