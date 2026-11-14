package dev.cctablet.menu;

import dev.cctablet.block.HddCaseBlockEntity;
import dev.cctablet.item.HardDriveItem;
import dev.cctablet.registry.ModBlocks;
import dev.cctablet.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public final class HddCaseMenu extends AbstractContainerMenu {
    private final HddCaseBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public HddCaseMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, (HddCaseBlockEntity) playerInventory.player.level()
            .getBlockEntity(data.readBlockPos()));
    }

    public HddCaseMenu(int containerId, Inventory playerInventory, HddCaseBlockEntity blockEntity) {
        super(ModMenus.HDD_CASE.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlot(new SlotItemHandler(blockEntity.getDriveHandler(), 0, 64, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof HardDriveItem;
            }
        });
        addSlot(new SlotItemHandler(blockEntity.getDriveHandler(), 1, 95, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof HardDriveItem;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9,
                    8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        var stack = slot.getItem();
        var original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof HardDriveItem) {
            if (!moveItemStackTo(stack, 0, 2, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!moveItemStackTo(stack, 29, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 2, 29, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return !blockEntity.isRemoved() && stillValid(access, player, ModBlocks.HDD_CASE.get());
    }
}
