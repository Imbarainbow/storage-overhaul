package de.maxhenkel.storage.gui;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.storage.Main;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

import java.util.function.Supplier;

public class Containers {

    public static ContainerType<AdvancedShulkerboxContainer> SHULKERBOX_CONTAINER;
    public static ContainerType<HugeChestContainer> GENERIC_18x3;
    public static ContainerType<HugeChestContainer> GENERIC_18x4;
    public static ContainerType<HugeChestContainer> GENERIC_18x5;
    public static ContainerType<HugeChestContainer> GENERIC_18x6;
    public static ContainerType<HugeChestContainer> GENERIC_18x7;
    public static ContainerType<HugeChestContainer> GENERIC_18x8;
    public static ContainerType<HugeChestContainer> GENERIC_18x9;

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<AdvancedShulkerboxContainer, AdvancedShulkerboxScreen>registerScreen(SHULKERBOX_CONTAINER, AdvancedShulkerboxScreen::new);

        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x3, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x4, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x5, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x6, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x7, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x8, HugeChestScreen::new);
        ClientRegistry.<HugeChestContainer, HugeChestScreen>registerScreen(GENERIC_18x9, HugeChestScreen::new);
    }

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        SHULKERBOX_CONTAINER = new ContainerType<>(AdvancedShulkerboxContainer::new);
        SHULKERBOX_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "shulkerbox"));
        event.getRegistry().register(SHULKERBOX_CONTAINER);

        GENERIC_18x3 = registerGeneric(event, 3, () -> GENERIC_18x3);
        GENERIC_18x4 = registerGeneric(event, 4, () -> GENERIC_18x4);
        GENERIC_18x5 = registerGeneric(event, 5, () -> GENERIC_18x5);
        GENERIC_18x6 = registerGeneric(event, 6, () -> GENERIC_18x6);
        GENERIC_18x7 = registerGeneric(event, 7, () -> GENERIC_18x7);
        GENERIC_18x8 = registerGeneric(event, 8, () -> GENERIC_18x8);
        GENERIC_18x9 = registerGeneric(event, 9, () -> GENERIC_18x9);
    }

    private static ContainerType<HugeChestContainer> registerGeneric(RegistryEvent.Register<ContainerType<?>> event, int rows, Supplier<ContainerType<?>> te) {
        ContainerType<HugeChestContainer> type = new ContainerType<>((id, inv) -> new HugeChestContainer(te.get(), id, inv, rows));
        type.setRegistryName(new ResourceLocation(Main.MODID, "generic_18x" + rows));
        event.getRegistry().register(type);
        return type;
    }

}
