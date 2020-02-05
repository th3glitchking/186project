package com.dronez;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class RenderDroneFactory implements IRenderFactory<Drone> {
    public static final RenderDroneFactory INSTANCE = new RenderDroneFactory();

    @Override
    public EntityRenderer<? super Drone> createRenderFor(EntityRendererManager manager) {
        if (FMLEnvironment.dist.isDedicatedServer())
            throw new IllegalStateException("Only run this on client!");

        return new DroneRenderer(manager);
    }
}