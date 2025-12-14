package com.kipti.bnb.content.weathered_girder;

import com.kipti.bnb.registry.BnbBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.decoration.girder.GirderWrenchBehavior;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class WeatheredGirderBlock extends GirderBlock {

    private static final int placementHelperId = PlacementHelpers.register(new WeatheredGirderPlacementHelper());

    public WeatheredGirderBlock(Properties p_49795_) {
        super(p_49795_);
    }

    /**Redirecting to weathered girder handling*/
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (pPlayer == null)
            return InteractionResult.PASS;

        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        if (AllBlocks.SHAFT.isIn(itemInHand)) {
            KineticBlockEntity.switchToBlockState(pLevel, pPos, BnbBlocks.WEATHERED_METAL_GIRDER_ENCASED_SHAFT.getDefaultState()
                .setValue(WATERLOGGED, pState.getValue(WATERLOGGED))
                .setValue(TOP, pState.getValue(TOP))
                .setValue(BOTTOM, pState.getValue(BOTTOM))
                .setValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS, pState.getValue(X) || pHit.getDirection()
                    .getAxis() == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X));

            pLevel.playSound(null, pPos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.25f);
            if (!pLevel.isClientSide && !pPlayer.isCreative()) {
                itemInHand.shrink(1);
                if (itemInHand.isEmpty())
                    pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
            }

            return InteractionResult.SUCCESS;
        }

        if (AllItems.WRENCH.isIn(itemInHand) && !pPlayer.isShiftKeyDown()) {
            if (WeatheredGirderWrenchBehaviour.handleClick(pLevel, pPos, pState, pHit))
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            return InteractionResult.FAIL;
        }

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(itemInHand))
            return helper.getOffset(pPlayer, pLevel, pState, pPos, pHit)
                .placeInWorld(pLevel, (BlockItem) itemInHand.getItem(), pPlayer, pHand, pHit);

        return InteractionResult.PASS;
    }
}
