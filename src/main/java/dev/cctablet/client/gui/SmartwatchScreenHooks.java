package dev.cctablet.client.gui;

import dan200.computercraft.client.gui.ComputerScreen;
import dev.cctablet.CCTablet;
import dev.cctablet.item.SmartwatchComputerItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// this swaps in the watch screen
@Mod.EventBusSubscriber(modid = CCTablet.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SmartwatchScreenHooks {
    private SmartwatchScreenHooks() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        var screen = event.getNewScreen();
        if (!(screen instanceof ComputerScreen<?> computerScreen)) return;

        var menu = computerScreen.getMenu();
        if (!(menu.getDisplayStack().getItem() instanceof SmartwatchComputerItem)) return;

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        event.setNewScreen(new SmartwatchScreen(menu, player.getInventory(), computerScreen.getTitle()));
    }
}
