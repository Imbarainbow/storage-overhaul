package de.maxhenkel.storage;

import de.maxhenkel.storage.blocks.ModBlocks;
import de.maxhenkel.storage.blocks.tileentity.TileEntities;
import de.maxhenkel.storage.blocks.tileentity.render.StorageOverhaulChestRenderer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "storage_overhaul";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, event -> ModBlocks.registerItems((RegistryEvent.Register<Item>) event));
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, event -> ModBlocks.registerBlocks((RegistryEvent.Register<Block>) event));
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, event -> TileEntities.registerTileEntities((RegistryEvent.Register<TileEntityType<?>>) event));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            clientStart();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void clientStart() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntities.OAK_CHEST, StorageOverhaulChestRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.SPRUCE_CHEST, StorageOverhaulChestRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.BIRCH_CHEST, StorageOverhaulChestRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.ACACIA_CHEST, StorageOverhaulChestRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.JUNGLE_CHEST, StorageOverhaulChestRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.DARK_OAK_CHEST, StorageOverhaulChestRenderer::new);
    }

}
