package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.item.SmartwatchComputerItem;
import dev.cctablet.item.TabletComputerItem;
import dev.cctablet.item.HardDriveItem;
import dev.cctablet.config.CCTabletConfig;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.BlockItem;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, CCTablet.MOD_ID);

    public static final RegistryObject<TabletComputerItem> TABLET = ITEMS.register(
        "tablet",
        () -> new TabletComputerItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<SmartwatchComputerItem> SMARTWATCH = ITEMS.register(
        "smartwatch",
        () -> new SmartwatchComputerItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<BlockItem> UNIVERSAL_MODEM = ITEMS.register(
        "universal_modem",
        () -> new BlockItem(ModBlocks.UNIVERSAL_MODEM.get(), new Item.Properties())
    );

    public static final RegistryObject<HardDriveItem> HARDDRIVE1 = ITEMS.register(
        "harddrive1",
        () -> new HardDriveItem("HDD1", CCTabletConfig.HDD1_CAPACITY_MIB::get, false, new Item.Properties())
    );

    public static final RegistryObject<Item> DISC = ITEMS.register(
        "disc", () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> STACKED_DISCS = ITEMS.register(
        "stacked_discs", () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<HardDriveItem> HARDDRIVE2 = ITEMS.register(
        "harddrive2",
        () -> new HardDriveItem("HDD2", CCTabletConfig.HDD2_CAPACITY_MIB::get, false, new Item.Properties())
    );

    public static final RegistryObject<HardDriveItem> HARDDRIVE3 = ITEMS.register(
        "harddrive3",
        () -> new HardDriveItem("HDD3", CCTabletConfig.HDD3_CAPACITY_MIB::get, false, new Item.Properties())
    );

    public static final RegistryObject<HardDriveItem> HARDDRIVE4 = ITEMS.register(
        "harddrive4",
        () -> new HardDriveItem("HDD4", CCTabletConfig.HDD4_CAPACITY_MIB::get, false, new Item.Properties())
    );

    public static final RegistryObject<HardDriveItem> HARDDRIVE5 = ITEMS.register(
        "harddrive5",
        () -> new HardDriveItem("HDD5", null, true, new Item.Properties())
    );

    public static final RegistryObject<BlockItem> HDD_CASE = ITEMS.register(
        "hdd_case",
        () -> new BlockItem(ModBlocks.HDD_CASE.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
