package com.dronez.entities;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DroneRenderer extends BipedRenderer<Drone, DroneModel<Drone>> {
    private static final ResourceLocation IRON = new ResourceLocation("dronez:textures/iron_drone.png");
    private static final ResourceLocation GOLD = new ResourceLocation("dronez:textures/gold_drone.png");
    private static final ResourceLocation DIAMOND = new ResourceLocation("dronez:textures/diamond_drone.png");

    public DroneRenderer(EntityRendererManager renderManager) {
        //   (renderManager,               model,     shadowSize);
        super(renderManager, new DroneModel<>(),0.6F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Drone entity) {
        switch (entity.getShell()) {
            case 1:
                return IRON;
            case 2:
                return GOLD;
            case 3:
                return DIAMOND;
            default:
                return null;
        }
    }

    @Override
    protected void preRenderCallback(Drone entity, float partialTickTime) {
        //Make larger
        GL11.glScalef(2F, 2F, 2F);
    }
}