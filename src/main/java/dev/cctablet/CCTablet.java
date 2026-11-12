package dev.cctablet;

import dev.cctablet.network.ModNetworking;
import dev.cctablet.registry.ModItems;
import dev.cctablet.registry.ModBlocks;
import dev.cctablet.registry.ModBlockEntities;
import dev.cctablet.registry.ModUpgrades;
import dev.cctablet.registry.ModRecipes;
import dev.cctablet.upgrade.UniversalModemUpgrade;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CCTablet.MOD_ID)
public final class CCTablet {
    public static final String MOD_ID = "cctablet";

    public CCTablet() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModUpgrades.POCKET_UPGRADES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        modBus.addListener(CCTablet::addCreativeTabItems);

        ModNetworking.register();
    }

    private static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(new ResourceLocation("computercraft", "tab"))) {
            event.accept(ModItems.TABLET.get());
            event.accept(ModItems.SMARTWATCH.get());
            event.accept(ModItems.TABLET.get().create(-1, null, -1, universalModemData()));
            event.accept(ModItems.SMARTWATCH.get().create(-1, null, -1, universalModemData()));
            event.accept(ModItems.UNIVERSAL_MODEM.get());
        }
    }

    private static UpgradeData<IPocketUpgrade> universalModemData() {
        var upgrade = new UniversalModemUpgrade(
            new ResourceLocation(MOD_ID, "universal_modem"),
            new ItemStack(ModItems.UNIVERSAL_MODEM.get())
        );
        return UpgradeData.ofDefault(upgrade);
    }
}
