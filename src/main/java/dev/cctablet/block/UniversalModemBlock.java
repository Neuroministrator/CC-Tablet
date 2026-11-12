package dev.cctablet.block;

import dev.cctablet.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class UniversalModemBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty ON = BooleanProperty.create("on");
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 4);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 12, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 4, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(12, 0, 0, 16, 16, 16);
    private static final VoxelShape DOWN_SHAPE = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape UP_SHAPE = Block.box(0, 12, 0, 16, 16, 16);

    public UniversalModemBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ON, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()).setValue(ON, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        return direction == state.getValue(FACING) && !canSurvive(state, level, pos)
            ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
            : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var facing = state.getValue(FACING);
        var supportPos = pos.relative(facing);
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing.getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UniversalModemBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        net.minecraft.world.level.Level world, BlockState state, BlockEntityType<T> type
    ) {
        return type == ModBlockEntities.UNIVERSAL_MODEM.get()
            ? (level, pos, blockState, blockEntity) -> UniversalModemBlockEntity.tick(
                level, pos, blockState, (UniversalModemBlockEntity) blockEntity)
            : null;
    }
}
