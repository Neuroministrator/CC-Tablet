package dev.cctablet.peripheral;

import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.cctablet.block.HddCaseBlockEntity;
import dev.cctablet.item.HardDriveItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class HddCasePeripheral implements IPeripheral {
    private final HddCaseBlockEntity blockEntity;
    private final Map<IComputerAccess, String[]> mountedPaths = new HashMap<>();

    public HddCasePeripheral(HddCaseBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() {
        return "hdd_case";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof HddCasePeripheral peripheral && peripheral.blockEntity == blockEntity;
    }

    @Override
    public synchronized void attach(IComputerAccess computer) {
        var paths = new String[2];
        mountedPaths.put(computer, paths);
        mountDrives(computer, paths);
    }

    @Override
    public synchronized void detach(IComputerAccess computer) {
        mountedPaths.remove(computer);
    }

    public synchronized void refreshMounts() {
        for (var entry : mountedPaths.entrySet()) {
            unmountDrives(entry.getKey(), entry.getValue());
            mountDrives(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void close() {
        for (var entry : mountedPaths.entrySet()) unmountDrives(entry.getKey(), entry.getValue());
        mountedPaths.clear();
    }

    @LuaFunction(mainThread = true)
    public final String getLabel(int slot) throws LuaException {
        var stack = requireDrive(slot);
        return ((HardDriveItem) stack.getItem()).getDriveName(stack);
    }

    @LuaFunction(mainThread = true)
    public final void setLabel(int slot, String label) throws LuaException {
        if (label.length() > 32) throw new LuaException("Label cannot be longer than 32 characters");
        var stack = requireDrive(slot);
        ((HardDriveItem) stack.getItem()).setLabel(stack, label);
        blockEntity.markDriveChanged(slot - 1);
    }

    @LuaFunction(mainThread = true)
    public final double getCapacity(int slot) throws LuaException {
        var stack = requireDrive(slot);
        var drive = (HardDriveItem) stack.getItem();
        return drive.isReadOnly() ? 0.0 : drive.getCapacityBytes(stack);
    }

    @LuaFunction(mainThread = true)
    public final boolean isReadOnly(int slot) throws LuaException {
        var stack = requireDrive(slot);
        return ((HardDriveItem) stack.getItem()).isReadOnly();
    }

    private ItemStack requireDrive(int slot) throws LuaException {
        var index = slot - 1;
        if (index < 0 || index > 1) throw new LuaException("Slot must be 1 or 2");

        var stack = blockEntity.getDriveStack(index);
        if (!(stack.getItem() instanceof HardDriveItem)) throw new LuaException("No HDD in that slot");
        return stack;
    }

    private void mountDrives(IComputerAccess computer, String[] paths) {
        if (!(blockEntity.getLevel() instanceof ServerLevel level)) return;

        for (var index = 0; index < paths.length; index++) {
            var stack = blockEntity.getDriveStack(index);
            if (!(stack.getItem() instanceof HardDriveItem drive)) continue;

            Mount mount = drive.createMount(stack, level);
            blockEntity.setChanged();
            if (mount == null) continue;

            var location = "hdd" + (index + 1);
            var driveName = drive.getDriveName(stack);
            paths[index] = mount instanceof WritableMount writable
                ? computer.mountWritable(location, writable, driveName)
                : computer.mount(location, mount, driveName);
        }
    }

    private void unmountDrives(IComputerAccess computer, String[] paths) {
        for (var index = 0; index < paths.length; index++) {
            var path = paths[index];
            if (path != null) {
                try {
                    computer.unmount(path);
                } catch (RuntimeException ignored) {
                    // the computer may already have detached
                }
                paths[index] = null;
            }
        }
    }
}
