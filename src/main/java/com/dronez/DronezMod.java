package com.dronez;

import com.dronez.Items.DroneSpawnEggItem;
import com.dronez.entities.AttackDrone;
import com.dronez.block.charger.ChargerBlock;
import com.dronez.block.charger.ChargerBlockTileEntity;
import com.dronez.block.workshop.WorkshopBlock;
import com.dronez.block.workshop.WorkshopContainer;
import com.dronez.block.workshop.WorkshopScreen;
import com.dronez.block.workshop.WorkshopTileEntity;
import com.dronez.entities.Drone;
import com.dronez.entities.RenderDroneFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
import net.minecraftforge.fml.network.IContainerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dronez")
public class DronezMod {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    static Item ironDroneBlade;
    static Item ironDroneShell;
    static Item ironDroneCore;
    static Item goldDroneBlade;
    static Item goldDroneShell;
    static Item goldDroneCore;
    static Item diamondDroneBlade;
    static Item diamondDroneShell;
    static Item diamondDroneCore;
    static ChargerBlock chargingBlock;
    static BlockItem chargingBlockItem;
    static WorkshopBlock workshopBlock;
    static BlockItem workshopBlockItem;
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

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        OBJLoader.INSTANCE.addDomain("dronez");
        RenderingRegistry.registerEntityRenderingHandler(Drone.class, RenderDroneFactory.INSTANCE);
        ScreenManager.registerFactory(WorkshopContainer.TYPE, WorkshopScreen::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("dronez", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
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

        private static void generateEntityTypes() {
            LOGGER.debug("Dronez: Creating EntityTypes...");
            /*drone = EntityType.Builder
                    .create(, EntityClassification.CREATURE)
                    .size(1F, 0.5F)
                    .build("drone")
                    .setRegistryName("dronez:drone);*/
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            chargingBlock = (ChargerBlock)ChargerBlock.CHARGER_BLOCK.setRegistryName("dronez", "charging_block");
            workshopBlock = (WorkshopBlock)WorkshopBlock.WORKSHOP_BLOCK.setRegistryName("dronez", "workshop_block");
            blockRegistryEvent.getRegistry().registerAll(chargingBlock, workshopBlock);
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
            chargingBlockItem = (BlockItem)new BlockItem(chargingBlock, new Item.Properties().group(dronezGroup)).setRegistryName("dronez", "charging_block_item");
            workshopBlockItem = (BlockItem)new BlockItem(workshopBlock, new Item.Properties().group(dronezGroup)).setRegistryName("dronez", "workshop_block_item");

            event.getRegistry().registerAll(
                    ironDroneBlade,ironDroneShell,ironDroneCore,
                    goldDroneBlade, goldDroneShell, goldDroneCore,
                    diamondDroneBlade,diamondDroneCore,diamondDroneShell,
                    chargingBlockItem, workshopBlockItem
            );
            DroneSpawnEggItem.registerEggs();
        }

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.debug("Dronez: Registering Entities...");
            event.getRegistry().registerAll(
                    drone, attack
            );
        }

        @SubscribeEvent
        public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().registerAll(ChargerBlockTileEntity.TYPE, WorkshopTileEntity.TYPE);
        }

        @SubscribeEvent
        public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(WorkshopContainer.TYPE);
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeRegistryEvents {
        @SubscribeEvent
        public static void registerCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
            TileEntity obj = event.getObject();

            if (obj instanceof ChargerBlockTileEntity) {
                ChargerBlockTileEntity entity = (ChargerBlockTileEntity)obj;
                event.addCapability(ChargerBlock.IDENTIFIER, entity);
            }
        }
    }
}
