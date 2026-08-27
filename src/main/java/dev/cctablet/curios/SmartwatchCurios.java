package dev.cctablet.curios;

import dan200.computercraft.shared.pocket.core.PocketHolder;
import dev.cctablet.item.SmartwatchComputerItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

// this handles the curios slot
public final class SmartwatchCurios {
    public static final String SLOT_ID = "watch";

    // this is the fake inventory slot
    public static final int SYNTHETIC_PLAYER_SLOT = -4242;

    private SmartwatchCurios() {
    }

    public static PocketHolder.PlayerHolder holder(ServerPlayer player) {
        return new PocketHolder.PlayerHolder(player, SYNTHETIC_PLAYER_SLOT);
    }

    public static ItemStack getEquippedWatch(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player).resolve()
            .flatMap(handler -> handler.getStacksHandler(SLOT_ID))
            .map(handler -> {
                var stacks = handler.getStacks();
                return stacks.getSlots() > 0 ? stacks.getStackInSlot(0) : ItemStack.EMPTY;
            })
            .filter(stack -> stack.getItem() instanceof SmartwatchComputerItem)
            .orElse(ItemStack.EMPTY);
    }

    // this syncs the watch slot
    public static void setChanged(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).resolve()
            .flatMap(handler -> handler.getStacksHandler(SLOT_ID))
            .ifPresent(handler -> {
                var stacks = handler.getStacks();
                if (stacks.getSlots() <= 0) return;

                var stack = stacks.getStackInSlot(0);
                if (!stack.isEmpty()) stacks.setStackInSlot(0, stack);
            });
    }
}
