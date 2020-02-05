package com.dronez;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DroneRenderer extends MobRenderer<Drone, DroneModel<Drone>> {
    private static final ResourceLocation SKIN = new ResourceLocation("wabbits:textures/entity/wabbit.png");

    public DroneRenderer(EntityRendererManager renderManager) {
        //   (renderManager,               model,     shadowSize);
        super(renderManager, new DroneModel<>(),0.6F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Drone entity) {
        return SKIN;
    }

    @Override
    protected void preRenderCallback(Drone wabbitEntity, float partialTickTime) {
        //Make larger
        GL11.glScalef(2F, 2F, 2F);
    }
}