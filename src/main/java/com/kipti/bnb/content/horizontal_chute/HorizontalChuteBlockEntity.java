package com.kipti.bnb.content.horizontal_chute;

import com.kipti.bnb.mixin.horizontal_chute.ChuteBlockEntityAccessor;
import com.kipti.bnb.registry.BnbBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class HorizontalChuteBlockEntity extends ChuteBlockEntity {

    public HorizontalChuteBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BnbBlockEntities.CHUTE.get(),
                (be, context) -> ((ChuteBlockEntityAccessor) be).bits_n_bobs$getItemHandler()
        );
    }

    //Remove default motion since this is horizontal, no gravity
    @Override
    public float getItemMotion() {
        // Chutes per second
        final float fanSpeedModifier = 1 / 64f;
        final float maxItemSpeed = 20f;

        final float motion = (getPush() + getPull()) * fanSpeedModifier;
        return (Mth.clamp(motion, -maxItemSpeed, maxItemSpeed)) / 20f;
    }

    @Override
    protected float calculatePull() {
        final Direction facing = getBlockState().getValue(HorizontalChuteBlock.HORIZONTAL_FACING);
        final BlockPos fanPos = worldPosition.relative(facing.getOpposite());
        final BlockState blockStateOnTop = level.getBlockState(fanPos);
        if (AllBlocks.ENCASED_FAN.has(blockStateOnTop)
                && blockStateOnTop.getValue(EncasedFanBlock.FACING) == facing) {
            final BlockEntity be = level.getBlockEntity(fanPos);
            if (be instanceof final EncasedFanBlockEntity fan && !be.isRemoved()) {
                return fan.getSpeed();
            }
        }

        float totalPull = 0;
        for (final Direction d : Iterate.directions) {
            final ChuteBlockEntity inputChute = ((ChuteBlockEntityAccessor) this).bits_n_bobs$getInputChute(d);
            if (inputChute == null)
                continue;
            totalPull += ((ChuteBlockEntityAccessor) inputChute).bits_n_bobs$getPull();
        }
        for (final Direction d : Iterate.horizontalDirections) {
            final ChuteBlockEntity inputChute = getHorizontalInputChute(d);
            if (inputChute == null)
                continue;
            totalPull += ((ChuteBlockEntityAccessor) inputChute).bits_n_bobs$getPull();
        }
        return totalPull;
    }

    @Override
    protected float calculatePush(final int branchCount) {
        if (level == null)
            return 0;
        final Direction facing = getBlockState().getValue(HorizontalChuteBlock.HORIZONTAL_FACING);
        final BlockPos fanPos = worldPosition.relative(facing);
        final BlockState blockStateOnTop = level.getBlockState(fanPos);
        if (AllBlocks.ENCASED_FAN.has(blockStateOnTop)
                && blockStateOnTop.getValue(EncasedFanBlock.FACING) == facing.getOpposite()) {
            final BlockEntity be = level.getBlockEntity(fanPos);
            if (be instanceof final EncasedFanBlockEntity fan && !be.isRemoved()) {
                return fan.getSpeed();
            }
        }

        final ChuteBlockEntity targetChute = ((ChuteBlockEntityAccessor) this).bits_n_bobs$getTargetChute(getBlockState());
        if (targetChute == null)
            return 0;
        return ((ChuteBlockEntityAccessor) this).bits_n_bobs$getPush() / branchCount;
    }

    public float getPush() {
        return ((ChuteBlockEntityAccessor) this).bits_n_bobs$getPush();
    }

    public float getPull() {
        return ((ChuteBlockEntityAccessor) this).bits_n_bobs$getPull();
    }

    private ChuteBlockEntity getHorizontalInputChute(Direction direction) {
        if (level == null || direction == Direction.DOWN)
            return null;
        direction = direction.getOpposite();
        final BlockPos chutePos = worldPosition.relative(direction);
        final BlockState chuteState = level.getBlockState(chutePos);
        if (!HorizontalChuteBlock.isHorizontalChute(chuteState) || chuteState.getValue(HorizontalChuteBlock.HORIZONTAL_FACING) != direction.getOpposite())
            return null;
        final BlockEntity be = level.getBlockEntity(chutePos);
        if (be instanceof ChuteBlockEntity && !be.isRemoved())
            return (ChuteBlockEntity) be;
        return null;
    }

}
