package com.kipti.bnb.content.flywheel_bearing;

import com.kipti.bnb.registry.BnbBlockEntities;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class FlywheelBearingBlock extends DirectionalKineticBlock implements IBE<FlywheelBearingBlockEntity>, ICogWheel {

    public FlywheelBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.mayBuild())
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return InteractionResult.FAIL;
        if (stack.isEmpty()) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            withBlockEntityDo(level, pos, be -> {
                if (be.running) {
                    be.disassemble();
                } else {
                    be.checkAssemblyNextTick = true;
                }
            });
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean isLargeCog() {
        return true;
    }

    @Override
    public Class<FlywheelBearingBlockEntity> getBlockEntityClass() {
        return FlywheelBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FlywheelBearingBlockEntity> getBlockEntityType() {
        return BnbBlockEntities.FLYWHEEL_BEARING.get();
    }

}
