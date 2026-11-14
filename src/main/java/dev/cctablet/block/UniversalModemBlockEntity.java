package dev.cctablet.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dev.cctablet.peripheral.UniversalModemPeripheral;
import dev.cctablet.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UniversalModemBlockEntity extends BlockEntity {
    private final UniversalModemPeripheral modem;
    private final LazyOptional<IPeripheral> peripheralCapability;

    public UniversalModemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UNIVERSAL_MODEM.get(), pos, state);
        modem = new UniversalModemPeripheral(
            () -> (ServerLevel) level,
            () -> new Vec3(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
            )
        );
        peripheralCapability = LazyOptional.of(() -> modem);
    }

    public IPeripheral getPeripheral(Direction side) {
        return side == null || side == getBlockState().getValue(DirectionalBlock.FACING)
            ? modem
            : null;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, UniversalModemBlockEntity modem) {
        if (level.isClientSide || !modem.modem.pollChanged()) return;
        level.setBlock(pos, state.setValue(UniversalModemBlock.ON, modem.modem.isOpen()), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        modem.saveSettings(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        modem.loadSettings(tag);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == Capabilities.CAPABILITY_PERIPHERAL && getPeripheral(side) != null) {
            return peripheralCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void setRemoved() {
        peripheralCapability.invalidate();
        modem.removed();
        super.setRemoved();
    }
}
