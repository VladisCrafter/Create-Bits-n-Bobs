package com.kipti.bnb.mixin.horizontal_chute;

import com.kipti.bnb.content.horizontal_chute.HorizontalChuteBlock;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    @Nullable
    protected abstract IItemHandler grabCapability(@NotNull Direction side);

    @Shadow
    protected abstract void handleInput(@Nullable IItemHandler inv, float startLocation);

    @Shadow
    private float pull;

    @Shadow
    private boolean updateAirFlow;

    @Shadow
    @Nullable
    protected abstract ChuteBlockEntity getTargetChute(BlockState state);

    @Shadow
    protected abstract float calculatePull();

    public ChuteBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "handleInputFromAbove", at = @At("HEAD"), cancellable = true)
    private void handleInputFromAbove(final CallbackInfo ci) {
        final BlockState state = getBlockState();
        if (HorizontalChuteBlock.isHorizontalChute(state)) {
            handleInput(grabCapability(state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING).getOpposite()), 1);
            ci.cancel();
        }
    }

    @Inject(method = "handleInputFromBelow", at = @At("HEAD"), cancellable = true)
    private void handleInputFromBelow(final CallbackInfo ci) {
        final BlockState state = getBlockState();
        if (HorizontalChuteBlock.isHorizontalChute(state)) {
            handleInput(grabCapability(state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING)), 0);
            ci.cancel();
        }
    }

    @Inject(method = "calculatePull", at = @At("RETURN"), cancellable = true)
    protected void calculatePullWithHorizontalInputs(final CallbackInfoReturnable<Float> cir) {
        float additionalPull = 0;
        for (final Direction d : Iterate.directions) {
            final ChuteBlockEntity inputChute = bits_n_bobs$getHorizontalInputChute(d);
            if (inputChute == null)
                continue;
            additionalPull += ((ChuteBlockEntityAccessor) inputChute).bits_n_bobs$getPull();
        }
        cir.setReturnValue(cir.getReturnValue() + additionalPull);
    }

    /**
     * @author cake
     * @reason after modifying with a redirect, its easier to just overwrite than to have duplicated code, and much faster than doing thread variables.
     * it needs the extra argument of a set to track visited chutes to avoid infinite recursion, which isnt done easily with trying to pass this variable otherwise.
     */
    @Overwrite
    public void updatePull() {
        bits_n_bobs$updatePullWithRecursionCheck(new HashSet<>());
    }

    @Unique
    public void bits_n_bobs$updatePullWithRecursionCheck(final Set<ChuteBlockEntity> visited) {
        final float totalPull = calculatePull();
        if (pull == totalPull)
            return;
        pull = totalPull;
        updateAirFlow = true;
        sendData();
        final ChuteBlockEntity targetChute = getTargetChute(getBlockState());
        if (targetChute != null && !visited.contains(targetChute)) {
            visited.add(targetChute);
            ((ChuteBlockEntityMixin) (Object) targetChute).bits_n_bobs$updatePullWithRecursionCheck(visited);
        }
    }

    @Inject(method = "getInputChutes", at = @At("RETURN"))
    private void getInputChutes(final CallbackInfoReturnable<List<ChuteBlockEntity>> cir) {
        final List<ChuteBlockEntity> inputs = cir.getReturnValue();
        for (final Direction direction : Iterate.directions) {
            final ChuteBlockEntity inputChute = bits_n_bobs$getHorizontalInputChute(direction);
            final BlockState state = getBlockState();
            if (inputChute == null)
                continue;
            inputs.add(inputChute);
        }
    }

    @Inject(method = "getTargetChute", at = @At("RETURN"), cancellable = true)
    private void getTargetChuteForHorizontal(final BlockState state, final CallbackInfoReturnable<ChuteBlockEntity> cir) {
        if (level == null)
            return;
        if (!(HorizontalChuteBlock.isHorizontalChute(state)))
            return;
        final Direction facing = state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING);
        final BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
        if (be instanceof final ChuteBlockEntity chute && !be.isRemoved())
            cir.setReturnValue(chute);
    }


    @Inject(method = "spawnAirFlow", at = @At("HEAD"), cancellable = true)
    private void spawnAirFlow(final float verticalStart, final float verticalEnd, final float motion, final float drag, final CallbackInfo ci) {
        final BlockState state = getBlockState();
        if (level == null || !(HorizontalChuteBlock.isHorizontalChute(state)))
            return;
        final AirParticleData airParticleData = new AirParticleData(drag, motion);
        final Vec3 origin = Vec3.atLowerCornerOf(worldPosition);
        final float xOff = Create.RANDOM.nextFloat() * .5f + .25f;
        final float zOff = Create.RANDOM.nextFloat() * .5f + .25f;
        final float yRot = (float) Math.toRadians(state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING).toYRot());
        final Vec3 v = origin.add(new Vec3(xOff, zOff, verticalStart).subtract(0.5, 0.5, 0.5).yRot(yRot).add(0.5, 0.5, 0.5));
        final Vec3 d = origin.add(new Vec3(xOff, zOff, verticalEnd).subtract(0.5, 0.5, 0.5).yRot(yRot).add(0.5, 0.5, 0.5))
                .subtract(v);
        if (Create.RANDOM.nextFloat() < 2 * motion)
            level.addAlwaysVisibleParticle(airParticleData, v.x, v.y, v.z, d.x, d.y, d.z);
        ci.cancel();
    }

    @Unique
    private ChuteBlockEntity bits_n_bobs$getHorizontalInputChute(Direction direction) {
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

    //Mixins for handleUpwardOutput

    @Inject(method = "handleUpwardOutput", at = @At("HEAD"))
    private void handleUpwardOutputInject(final boolean simulate, final CallbackInfoReturnable<Boolean> cir, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        final BlockState state = getBlockState();
        if (HorizontalChuteBlock.isHorizontalChute(state)) {
            horizontalFacing.set(state.getValue(HorizontalChuteBlock.HORIZONTAL_FACING));
        }
    }

    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"))
    private BlockPos bits_n_bobs$redirectAbove(final BlockPos pos, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        return horizontalFacing.get() != null ? pos.relative(horizontalFacing.get().getOpposite()) : pos.above();
    }

    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/chute/ChuteBlockEntity;grabCapability(Lnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/items/IItemHandler;"))
    private IItemHandler bits_n_bobs$redirectGrabCapability(final ChuteBlockEntity instance, final Direction serverLevel, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        return horizontalFacing.get() != null ? grabCapability(horizontalFacing.get().getOpposite()) : grabCapability(Direction.UP);
    }

    //Return UP if they match, return SOUTH of they dont, this way it works into the existing code
    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/funnel/FunnelBlock;getFunnelFacing(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction;"))
    private Direction bits_n_bobs$redirectFunnelFacing(final BlockState state, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        if (horizontalFacing.get() != null) {
            final Direction funnelFacing = FunnelBlock.getFunnelFacing(level.getBlockState(getBlockPos().relative(horizontalFacing.get().getOpposite())));
            return funnelFacing == horizontalFacing.get().getOpposite() ? Direction.UP : Direction.SOUTH;
        }
        return FunnelBlock.getFunnelFacing(state);
    }

    //Return UP if they match, return SOUTH of they dont, this way it works into the existing code
    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;hasBlockSolidSide(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean bits_n_bobs$redirectHasSolidSide(final BlockState correctStateAbove, final BlockGetter level, final BlockPos abovePos, final Direction down, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        return horizontalFacing.get() != null ? BlockHelper.hasBlockSolidSide(correctStateAbove, level, getBlockPos().relative(horizontalFacing.get().getOpposite()), horizontalFacing.get()) : BlockHelper.hasBlockSolidSide(correctStateAbove, level, abovePos, down);
    }

    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 bits_n_bobs$redirectHandleUpwardOutput(Vec3 instance, double x, double y, double z, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        if (horizontalFacing.get() != null) {
            return instance.add(Vec3.atLowerCornerOf(horizontalFacing.get().getNormal()).scale(y));
        }
        return instance.add(x, y, z);
    }

    @Redirect(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private void bits_n_bobs$redirectDeltaMovement(ItemEntity instance, double x, double y, double z, final @Share("horizontalFacingUp") LocalRef<Direction> horizontalFacing) {
        if (horizontalFacing.get() != null) {
            final Vec3 motion = Vec3.atLowerCornerOf(horizontalFacing.get().getNormal()).scale(y);
            instance.setDeltaMovement(motion.x, motion.y, motion.z);
            return;
        }
        instance.setDeltaMovement(x, y, z);
    }


}
