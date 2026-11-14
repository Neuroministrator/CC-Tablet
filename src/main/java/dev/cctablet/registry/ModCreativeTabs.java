package dev.cctablet.registry;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dev.cctablet.CCTablet;
import dev.cctablet.upgrade.UniversalModemUpgrade;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCTablet.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CC_TABLET = TABS.register("main", () ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cctablet"))
            .icon(() -> new ItemStack(ModItems.TABLET.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.TABLET.get());
                output.accept(ModItems.TABLET.get().create(-1, null, -1, universalModemData()));
                output.accept(ModItems.SMARTWATCH.get());
                output.accept(ModItems.SMARTWATCH.get().create(-1, null, -1, universalModemData()));
                output.accept(ModItems.UNIVERSAL_MODEM.get());
                output.accept(ModItems.HARDDRIVE1.get());
                output.accept(ModItems.HARDDRIVE2.get());
                output.accept(ModItems.HARDDRIVE3.get());
                output.accept(ModItems.HARDDRIVE4.get());
                output.accept(ModItems.HARDDRIVE5.get());
                output.accept(ModItems.DISC.get());
                output.accept(ModItems.STACKED_DISCS.get());
                output.accept(ModItems.HDD_CASE.get());
            })
            .build());

    private ModCreativeTabs() {
    }

    private static UpgradeData<IPocketUpgrade> universalModemData() {
        var upgrade = new UniversalModemUpgrade(
            new ResourceLocation(CCTablet.MOD_ID, "universal_modem"),
            new ItemStack(ModItems.UNIVERSAL_MODEM.get())
        );
        return UpgradeData.ofDefault(upgrade);
    }
}
