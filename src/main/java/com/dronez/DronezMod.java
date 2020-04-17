package com.dronez;

import com.dronez.block.charger.ChargerBlock;
import com.dronez.block.charger.ChargerBlockTileEntity;
import com.dronez.block.workshop.WorkshopBlock;
import com.dronez.block.workshop.WorkshopContainer;
import com.dronez.block.workshop.WorkshopScreen;
import com.dronez.block.workshop.WorkshopTileEntity;
import com.dronez.entities.AttackDrone;
import com.dronez.entities.Drone;
import com.dronez.entities.RenderDroneFactory;
import com.dronez.entities.storage.StorageContainer;
import com.dronez.entities.storage.StorageDrone;
import com.dronez.entities.storage.StorageScreen;
import com.dronez.items.DronePackageItem;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DronezMod.MODID)
public class DronezMod {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "dronez";

    public static Item ironDroneBlade;
    public static Item ironDroneShell;
    public static Item ironDroneCore;
    public static Item goldDroneBlade;
    public static Item goldDroneShell;
    public static Item goldDroneCore;
    public static Item diamondDroneBlade;
    public static Item diamondDroneShell;
    public static Item diamondDroneCore;
    public static DronePackageItem dronePackageItem;
    static ChargerBlock chargerBlock;
    static BlockItem chargerBlockItem;
    static WorkshopBlock workshopBlock;
    static BlockItem workshopBlockItem;

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

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("Dronez: Preinit");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        OBJLoader.INSTANCE.addDomain(MODID);
        RenderingRegistry.registerEntityRenderingHandler(Drone.class, RenderDroneFactory.INSTANCE);
        ScreenManager.registerFactory(WorkshopContainer.TYPE, WorkshopScreen::new);
        ScreenManager.registerFactory(StorageContainer.TYPE, StorageScreen::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo(MODID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
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
        public static EntityType<Drone> attack = (EntityType<Drone>) EntityType.Builder.<Drone>create((AttackDrone::new), EntityClassification.CREATURE).build("drone").setRegistryName("dronez:attack_drone");
        public static EntityType<Drone> storage = (EntityType<Drone>) EntityType.Builder.<Drone>create((StorageDrone::new), EntityClassification.CREATURE).build("drone").setRegistryName("dronez:storage_drone");

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            LOGGER.debug("Dronez: Registering Blocks...");
            chargerBlock = (ChargerBlock)ChargerBlock.CHARGER_BLOCK.setRegistryName(MODID, ChargerBlock.REGISTRY_NAME);
            workshopBlock = (WorkshopBlock)WorkshopBlock.WORKSHOP_BLOCK.setRegistryName(MODID, WorkshopBlock.REGISTRY_NAME);
            blockRegistryEvent.getRegistry().registerAll(chargerBlock, workshopBlock);
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            LOGGER.debug("Dronez: Registering Items...");
            ironDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_blade");
            ironDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_shell");
            ironDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:iron_drone_core");
            goldDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_blade");
            goldDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_shell");
            goldDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:gold_drone_core");
            diamondDroneBlade = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_blade");
            diamondDroneShell = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_shell");
            diamondDroneCore = new Item(new Item.Properties().group(dronezGroup)).setRegistryName("dronez:diamond_drone_core");
            chargerBlockItem = (BlockItem)new BlockItem(chargerBlock, new Item.Properties().group(dronezGroup)).setRegistryName(MODID, "charger_block_item");
            workshopBlockItem = (BlockItem)new BlockItem(workshopBlock, new Item.Properties().group(dronezGroup)).setRegistryName(MODID, "workshop_block_item");
            dronePackageItem = (DronePackageItem)new DronePackageItem().setRegistryName(MODID, DronePackageItem.DRONE_PACKAGE_ITEM_ID);

            event.getRegistry().registerAll(
                    ironDroneBlade, ironDroneShell, ironDroneCore,
                    goldDroneBlade, goldDroneShell, goldDroneCore,
                    diamondDroneBlade, diamondDroneCore, diamondDroneShell,
                    chargerBlockItem, workshopBlockItem, dronePackageItem
            );
        }

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.debug("Dronez: Registering Entities...");
            event.getRegistry().registerAll(drone, attack, storage);
        }

        @SubscribeEvent
        public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.debug("Dronez: Registering Tile Entities...");
            event.getRegistry().registerAll(ChargerBlockTileEntity.TYPE, WorkshopTileEntity.TYPE);
        }

        @SubscribeEvent
        public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
            LOGGER.debug("Dronez: Registering Containers...");
            event.getRegistry().registerAll(WorkshopContainer.TYPE, StorageContainer.TYPE);
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeRegistryEvents {
        @SubscribeEvent
        public static void registerCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
            TileEntity obj = event.getObject();

            if (obj instanceof ChargerBlockTileEntity) {
                LOGGER.debug("Dronez: Registering Charger Capabilities...");
                ChargerBlockTileEntity entity = (ChargerBlockTileEntity)obj;
                event.addCapability(ChargerBlock.IDENTIFIER, entity);
            } else if (obj instanceof WorkshopTileEntity) {
                LOGGER.debug("Dronez: Registering Workshop Capabilities...");
                WorkshopTileEntity entity = (WorkshopTileEntity)obj;
                event.addCapability(WorkshopBlock.IDENTIFIER, entity);
            }
        }
    }
}
