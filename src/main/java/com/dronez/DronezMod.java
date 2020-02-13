package com.dronez;

import com.dronez.Items.DroneSpawnEggItem;
import com.dronez.entities.Drone;
import com.dronez.entities.RenderDroneFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dronez")
public class DronezMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    static Item ironDroneBlade;
    static Item ironDroneShell;
    static Item ironDroneCore;
    static Item goldDroneBlade;
    static Item goldDroneShell;
    static Item goldDroneCore;
    static Item diamondDroneBlade;
    static Item diamondDroneShell;
    static Item diamondDroneCore;
    static SlabBlock chargingBlock;
    static BlockItem chargingBlockItem;
    static DroneSpawnEggItem ironDroneSpawnEgg;
    static DroneSpawnEggItem goldDroneSpawnEgg;
    static DroneSpawnEggItem diamondDroneSpawnEgg;

    public static final ItemGroup dronezGroup = new ItemGroup("dronez") {
        @Override
        public ItemStack createIcon() {
            return ironDroneCore.getDefaultInstance();
        }
    };

    public DronezMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        OBJLoader.INSTANCE.addDomain("dronez");
        RenderingRegistry.registerEntityRenderingHandler(Drone.class, RenderDroneFactory.INSTANCE);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("dronez", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {


        public static EntityType<Drone> drone = (EntityType<Drone>) EntityType.Builder.<Drone>create((Drone::new), EntityClassification.CREATURE).build("drone").setRegistryName("dronez:drone");

        private static void generateEntityTypes() {
            LOGGER.debug("Dronez: Creating EntityTypes...");
            /*drone = EntityType.Builder
                    .create(, EntityClassification.CREATURE)
                    .size(0.8F, 1F)
                    .build("drone")
                    .setRegistryName("dronez:drone);*/
        }




        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            chargingBlock = (SlabBlock)new SlabBlock(Block.Properties.create(Material.GLASS)).setRegistryName("dronez:charging_block");
            blockRegistryEvent.getRegistry().registerAll(chargingBlock);
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            ironDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_blade");
            ironDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_shell");
            ironDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_core");
            goldDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_blade");
            goldDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_shell");
            goldDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_core");
            diamondDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_blade");
            diamondDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_shell");
            diamondDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_core");
            chargingBlockItem = (BlockItem)new BlockItem(chargingBlock, new Item.Properties().group(dronezGroup)).setRegistryName("dronez:charging_block_item");




            event.getRegistry().registerAll(
                    ironDroneBlade,ironDroneShell,ironDroneCore,
                    goldDroneBlade, goldDroneShell, goldDroneCore,
                    diamondDroneBlade,diamondDroneCore,diamondDroneShell,
                    chargingBlockItem
            );
            DroneSpawnEggItem.registerEggs();
        }

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.debug("Dronez: Registering Entities...");
            event.getRegistry().registerAll(
                    drone
            );
        }
    }
}
