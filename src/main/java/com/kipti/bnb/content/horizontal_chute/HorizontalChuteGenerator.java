package com.kipti.bnb.content.horizontal_chute;

import com.kipti.bnb.foundation.client.BnbAssetLookup;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class HorizontalChuteGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(final BlockState state) {
        return 90;
    }

    @Override
    protected int getYRotation(final BlockState state) {
        return horizontalAngle(state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov,
                                                final BlockState state) {
        final ChuteBlock.Shape shape = state.getValue(HorizontalChuteBlock.SHAPE);

        return shape == ChuteBlock.Shape.NORMAL ? BnbAssetLookup.partialNamedBaseModel(ctx, prov, "chute")
                : shape == ChuteBlock.Shape.INTERSECTION || shape == ChuteBlock.Shape.ENCASED
                ? BnbAssetLookup.partialNamedBaseModel(ctx, prov, "chute", "intersection")
                : BnbAssetLookup.partialNamedBaseModel(ctx, prov, "chute", "windowed");
    }

}

