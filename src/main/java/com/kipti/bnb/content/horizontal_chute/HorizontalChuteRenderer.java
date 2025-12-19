package com.kipti.bnb.content.horizontal_chute;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HorizontalChuteRenderer extends ChuteRenderer {

    public HorizontalChuteRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ChuteBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light,
                              int overlay) {
        if (be.getItem().isEmpty())
            return;
        BlockState blockState = be.getBlockState();
        if (blockState.getValue(HorizontalChuteBlock.HORIZONTAL_FACING) != Direction.DOWN)
            return;
        //TODO: readd optimization - needs mixin accessor
//        if (blockState.getValue(ChuteBlock.SHAPE) != ChuteBlock.Shape.WINDOW
//                && (be.bottomPullDistance == 0 || be.itemPosition.getValue(partialTicks) > .5f))
//            return;

        renderItem(be, partialTicks, ms, buffer, light, overlay);
    }
}
