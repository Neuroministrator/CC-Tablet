package dev.cctablet.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import dev.cctablet.item.HardDriveItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public final class HddCaseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public HddCaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(
        BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (!(level.getBlockEntity(pos) instanceof HddCaseBlockEntity hddCase)) return InteractionResult.PASS;

        var held = player.getItemInHand(hand);
        var inserting = held.getItem() instanceof HardDriveItem;
        var removing = player.isShiftKeyDown() && held.isEmpty();
        if (!inserting && !removing) {
            if (!held.isEmpty() || player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, hddCase, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (inserting) {
            if (!hddCase.insertDrive(held)) return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) held.shrink(1);
            return InteractionResult.CONSUME;
        }

        ItemStack removed = hddCase.extractLastDrive();
        if (removed.isEmpty()) return InteractionResult.PASS;
        if (!player.getInventory().add(removed)) player.drop(removed, false);
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HddCaseBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HddCaseBlockEntity hddCase) {
            hddCase.dropDrives();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
