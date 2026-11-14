package dev.cctablet.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dev.cctablet.item.HardDriveItem;
import dev.cctablet.peripheral.HddCasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.cctablet.menu.HddCaseMenu;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HddCaseBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler drives = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDriveChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof HardDriveItem;
        }
    };

    private final HddCasePeripheral peripheral = new HddCasePeripheral(this);
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> drives);
    private final LazyOptional<IPeripheral> peripheralHandler = LazyOptional.of(() -> peripheral);

    public HddCaseBlockEntity(BlockPos pos, BlockState state) {
        super(dev.cctablet.registry.ModBlockEntities.HDD_CASE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.cctablet.hdd_case");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new HddCaseMenu(containerId, inventory, this);
    }

    public ItemStack getDriveStack(int slot) {
        return drives.getStackInSlot(slot);
    }

    public ItemStackHandler getDriveHandler() {
        return drives;
    }

    public boolean insertDrive(ItemStack heldStack) {
        if (!(heldStack.getItem() instanceof HardDriveItem)) return false;

        var oneDrive = heldStack.copy();
        oneDrive.setCount(1);
        for (var slot = 0; slot < drives.getSlots(); slot++) {
            if (!drives.getStackInSlot(slot).isEmpty()) continue;
            if (drives.insertItem(slot, oneDrive, false).isEmpty()) return true;
        }
        return false;
    }

    public ItemStack extractLastDrive() {
        for (var slot = drives.getSlots() - 1; slot >= 0; slot--) {
            var stack = drives.extractItem(slot, 1, false);
            if (!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    public void markDriveChanged(int slot) {
        setChanged();
        if (level != null && !level.isClientSide) peripheral.refreshMounts();
    }

    public void dropDrives() {
        if (level == null || level.isClientSide) return;
        for (var slot = 0; slot < drives.getSlots(); slot++) {
            var stack = drives.extractItem(slot, 1, false);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, stack);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Drives", drives.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        drives.deserializeNBT(tag.getCompound("Drives"));
    }

    @Override
    public <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> capability, @Nullable Direction side
    ) {
        if (capability == Capabilities.CAPABILITY_PERIPHERAL) return peripheralHandler.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return itemHandler.cast();
        return super.getCapability(capability, side);
    }

    @Override
    public void setRemoved() {
        peripheral.close();
        peripheralHandler.invalidate();
        itemHandler.invalidate();
        super.setRemoved();
    }
}
