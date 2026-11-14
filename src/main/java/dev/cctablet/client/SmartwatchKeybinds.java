package dev.cctablet.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.cctablet.CCTablet;
import dev.cctablet.registry.ModItems;
import net.minecraft.client.Minecraft;
import dev.cctablet.network.ModNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;

public final class SmartwatchKeybinds {
    public static final KeyMapping OPEN_SMARTWATCH = new KeyMapping(
        "key.cctablet.open_smartwatch",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V,
        "key.categories.cctablet"
    );

    private SmartwatchKeybinds() {
    }

    @Mod.EventBusSubscriber(modid = CCTablet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBusEvents {
        private ModBusEvents() {
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SMARTWATCH);
        }
    }

    @Mod.EventBusSubscriber(modid = CCTablet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBusEvents {
        private ForgeBusEvents() {
        }

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            var player = Minecraft.getInstance().player;
            if (player == null) return;

            while (OPEN_SMARTWATCH.consumeClick()) {
                if (CuriosApi.getCuriosInventory(player).resolve()
                    .map(handler -> handler.isEquipped(ModItems.SMARTWATCH.get()))
                    .orElse(false)) {
                    ModNetworking.sendOpenSmartwatch();
                }
            }
        }
    }
}
