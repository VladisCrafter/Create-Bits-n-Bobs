package com.kipti.bnb.mixin.horizontal_chute;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChuteBlockEntity.class)
public interface ChuteBlockEntityAccessor {

    @Accessor("push")
    float bits_n_bobs$getPush();

    @Accessor("pull")
    float bits_n_bobs$getPull();

    @Accessor("itemHandler")
    ChuteItemHandler bits_n_bobs$getItemHandler();

    @Invoker("getInputChute")
    ChuteBlockEntity bits_n_bobs$getInputChute(final Direction direction);

    @Invoker("getTargetChute")
    ChuteBlockEntity bits_n_bobs$getTargetChute(BlockState blockState);
}
