package dev.cctablet;

import dev.cctablet.network.ModNetworking;
import dev.cctablet.registry.ModItems;
import dev.cctablet.registry.ModBlocks;
import dev.cctablet.registry.ModBlockEntities;
import dev.cctablet.registry.ModUpgrades;
import dev.cctablet.registry.ModRecipes;
import dev.cctablet.registry.ModCreativeTabs;
import dev.cctablet.registry.ModMenus;
import dev.cctablet.config.CCTabletConfig;
import dev.cctablet.item.HardDriveItem;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CCTablet.MOD_ID)
public final class CCTablet {
    public static final String MOD_ID = "cctablet";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CCTablet() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModUpgrades.POCKET_UPGRADES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        CCTabletConfig.register();
        HardDriveItem.prepareHdd5ProgramFolder();

        ModNetworking.register();
    }
}
