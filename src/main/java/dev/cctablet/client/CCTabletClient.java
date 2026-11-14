package dev.cctablet.client;

import dev.cctablet.CCTablet;
import dev.cctablet.registry.ModItems;
import dev.cctablet.registry.ModMenus;
import dev.cctablet.client.gui.HddCaseScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = CCTablet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CCTabletClient {
    private CCTabletClient() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(ModItems.SMARTWATCH.get(), SmartwatchRenderer::new);
            MenuScreens.register(ModMenus.HDD_CASE.get(), HddCaseScreen::new);
        });
    }
}
