package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.menu.HddCaseMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, CCTablet.MOD_ID);

    public static final RegistryObject<MenuType<HddCaseMenu>> HDD_CASE = MENUS.register(
        "hdd_case", () -> IForgeMenuType.create(HddCaseMenu::new)
    );

    private ModMenus() {
    }
}
