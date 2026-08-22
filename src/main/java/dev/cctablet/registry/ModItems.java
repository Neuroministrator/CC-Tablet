package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.item.TabletComputerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, CCTablet.MOD_ID);

    public static final RegistryObject<TabletComputerItem> TABLET = ITEMS.register(
        "tablet",
        () -> new TabletComputerItem(new Item.Properties().stacksTo(1))
    );

    private ModItems() {
    }
}
