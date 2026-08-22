package dev.cctablet;

import dev.cctablet.registry.ModItems;
import dev.cctablet.registry.ModRecipes;
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
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        modBus.addListener(CCTablet::addCreativeTabItems);
    }

    private static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        // cc tweaked creative tab
        if (event.getTabKey().location().equals(new ResourceLocation("computercraft", "tab"))) {
            event.accept(ModItems.TABLET.get());
        }
    }
}
