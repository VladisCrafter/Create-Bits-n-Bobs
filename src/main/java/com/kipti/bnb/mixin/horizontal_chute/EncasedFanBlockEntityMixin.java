package com.kipti.bnb.mixin.horizontal_chute;

import com.kipti.bnb.content.horizontal_chute.HorizontalChuteBlock;
import com.kipti.bnb.content.horizontal_chute.HorizontalChuteBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EncasedFanBlockEntity.class)
public class EncasedFanBlockEntityMixin extends KineticBlockEntity {
    public EncasedFanBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "updateChute", at = @At("HEAD"))
    public void updateChute(CallbackInfo ci) {
        final Direction direction = getBlockState().getValue(EncasedFanBlock.FACING);
        if (direction.getAxis()
                .isVertical())
            return;
        final BlockEntity poweredChute = level.getBlockEntity(worldPosition.relative(direction));
        if (!(poweredChute instanceof HorizontalChuteBlockEntity chuteBE))
            return;
        final Direction chuteFacing = chuteBE.getBlockState().getValue(HorizontalChuteBlock.HORIZONTAL_FACING);

        if (direction == chuteFacing)
            chuteBE.updatePull();
        else if (direction == chuteFacing.getOpposite())
            chuteBE.updatePush(1);
    }


}
