package dev.cctablet;

import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dev.cctablet.curios.SmartwatchCurios;
import dev.cctablet.item.SmartwatchComputerItem;
import dev.cctablet.item.TabletComputerItem;
import dev.cctablet.api.ServerComputerTerminalAccess;
import net.minecraft.world.item.ItemStack;

public final class TabletTerminalContext {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    public static final int SMARTWATCH_WIDTH = 18;
    public static final int SMARTWATCH_HEIGHT = 10;

    private TabletTerminalContext() {
    }

    public static ItemStack getStack(PocketHolder holder) {
        if (holder instanceof PocketHolder.PlayerHolder player) {
            if (player.slot() == SmartwatchCurios.SYNTHETIC_PLAYER_SLOT) {
                return SmartwatchCurios.getEquippedWatch(player.entity());
            }

            var inventory = player.entity().getInventory();
            return player.slot() >= 0 && player.slot() < inventory.getContainerSize()
                ? inventory.getItem(player.slot())
                : ItemStack.EMPTY;
        } else if (holder instanceof PocketHolder.ItemEntityHolder item) {
            return item.entity().getItem();
        } else if (holder instanceof PocketHolder.LecternHolder lectern) {
            return lectern.lectern().getItem();
        }

        return ItemStack.EMPTY;
    }

    public static boolean isTablet(PocketHolder holder) {
        return getStack(holder).getItem() instanceof TabletComputerItem;
    }

    public static boolean isSmartwatch(PocketHolder holder) {
        return getStack(holder).getItem() instanceof SmartwatchComputerItem;
    }

    public static int widthFor(PocketHolder holder, int original) {
        if (isTablet(holder)) return WIDTH;
        if (isSmartwatch(holder)) return SMARTWATCH_WIDTH;
        return original;
    }

    public static int heightFor(PocketHolder holder, int original) {
        if (isTablet(holder)) return HEIGHT;
        if (isSmartwatch(holder)) return SMARTWATCH_HEIGHT;
        return original;
    }

    public static int widthFor(ItemStack stack, int original) {
        if (stack.getItem() instanceof TabletComputerItem) return WIDTH;
        if (stack.getItem() instanceof SmartwatchComputerItem) return SMARTWATCH_WIDTH;
        return original;
    }

    public static int heightFor(ItemStack stack, int original) {
        if (stack.getItem() instanceof TabletComputerItem) return HEIGHT;
        if (stack.getItem() instanceof SmartwatchComputerItem) return SMARTWATCH_HEIGHT;
        return original;
    }

    /**
     * pocket computers are cached by id, so swapping the item doesn't make a new terminal.
     * set the device size next time that cached brain gets used. bit of a weird edge case.
     */
    public static void applyDeviceTerminalSize(PocketBrain brain, ItemStack stack) {
        if (!((Object) brain.computer() instanceof ServerComputerTerminalAccess terminal)) return;

        if (stack.getItem() instanceof TabletComputerItem) {
            terminal.cctablet$resizeTerminal(WIDTH, HEIGHT);
        } else if (stack.getItem() instanceof SmartwatchComputerItem) {
            terminal.cctablet$resizeTerminal(SMARTWATCH_WIDTH, SMARTWATCH_HEIGHT);
        }
    }
}
