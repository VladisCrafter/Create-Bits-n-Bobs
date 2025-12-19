package com.kipti.bnb.content.horizontal_chute;

import com.kipti.bnb.registry.BnbBlockEntities;
import com.kipti.bnb.registry.BnbShapes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorizontalChuteBlock extends AbstractChuteBlock implements ProperWaterloggedBlock {

    public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.create("horizontal_facing", Direction.Plane.HORIZONTAL);
    public static final Property<ChuteBlock.Shape> SHAPE = ChuteBlock.SHAPE;

    public HorizontalChuteBlock(final Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SHAPE, ChuteBlock.Shape.NORMAL)
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        ChuteBlock.Shape shape = state.getValue(SHAPE);
        if (shape == ChuteBlock.Shape.INTERSECTION)
            return InteractionResult.PASS;
        Level level = context.getLevel();
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        if (shape == ChuteBlock.Shape.ENCASED) {
            level.setBlockAndUpdate(context.getClickedPos(), state.setValue(SHAPE, ChuteBlock.Shape.NORMAL));
            level.levelEvent(2001, context.getClickedPos(),
                    Block.getId(AllBlocks.INDUSTRIAL_IRON_BLOCK.getDefaultState()));
            return InteractionResult.SUCCESS;
        }
        level.setBlockAndUpdate(context.getClickedPos(),
                state.setValue(SHAPE, shape != ChuteBlock.Shape.NORMAL ? ChuteBlock.Shape.NORMAL : ChuteBlock.Shape.WINDOW));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos,
                                        final CollisionContext context) {
        final ChuteBlock.Shape shape = state.getValue(SHAPE);
        return shape == ChuteBlock.Shape.ENCASED || shape == ChuteBlock.Shape.INTERSECTION ? Shapes.block() : BnbShapes.HORIZONTAL_CHUTE.get(state.getValue(HORIZONTAL_FACING).getOpposite());//TODO: proper shape for dat shi
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos,
                                                 final CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    public @NotNull BlockState rotate(final BlockState pState, final Rotation pRot) {
        return pState.setValue(HORIZONTAL_FACING, pRot.rotate(pState.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(final BlockState pState, final Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected boolean isPathfindable(final @NotNull BlockState state, final @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean isTransparent(final BlockState state) {
        return state.getValue(SHAPE) == ChuteBlock.Shape.WINDOW;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(HORIZONTAL_FACING, context.isSecondaryUseActive() ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING, SHAPE, WATERLOGGED);
    }

    @Override
    public @NotNull BlockState updateShape(final BlockState state, final Direction direction, final BlockState above, final LevelAccessor world,
                                           final BlockPos pos, final BlockPos p_196271_6_) {
        updateWater(world, state, pos);
        return super.updateShape(state, direction, above, world, pos, p_196271_6_);
    }

    @Override
    public BlockState updateChuteState(final BlockState state, final BlockState above, final BlockGetter world, final BlockPos pos) {
        if (!(state.getBlock() instanceof ChuteBlock))
            return state;

        boolean hasIntersection = false;
        final Direction currentFacing = state.getValue(HORIZONTAL_FACING);

        for (final Direction direction : Iterate.horizontalDirections) {
            final BlockPos neighborPos = pos.relative(direction);
            final BlockState neighborState = world.getBlockState(neighborPos);
            if (HorizontalChuteBlock.isHorizontalChute(neighborState) && neighborState.getValue(HORIZONTAL_FACING) == direction.getOpposite() &&
                    neighborState.getValue(HORIZONTAL_FACING).getAxis() != currentFacing.getAxis()) {
                hasIntersection = true;
                break;
            }
        }

        if (!hasIntersection)
            for (final Direction direction : Iterate.directionsInAxis(Direction.Axis.Y)) {
                final BlockPos neighborPos = pos.relative(direction);
                final BlockState neighborState = world.getBlockState(neighborPos);
                if (ChuteBlock.isChute(neighborState)) {
                    hasIntersection = true;
                    break;
                }
            }

        final ChuteBlock.Shape existingShape = state.getValue(SHAPE);
        return state.setValue(SHAPE, hasIntersection ? ChuteBlock.Shape.INTERSECTION : existingShape == ChuteBlock.Shape.INTERSECTION ? ChuteBlock.Shape.NORMAL : existingShape);
    }

    public static boolean isHorizontalChute(final BlockState state) {
        return state.getBlock() instanceof HorizontalChuteBlock;
    }

    @Override
    public BlockEntityType<? extends ChuteBlockEntity> getBlockEntityType() {
        return BnbBlockEntities.CHUTE.get();
    }
}
