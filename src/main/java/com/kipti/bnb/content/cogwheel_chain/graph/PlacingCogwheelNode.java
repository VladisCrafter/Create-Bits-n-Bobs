package com.kipti.bnb.content.cogwheel_chain.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public record PlacingCogwheelNode(BlockPos pos, Direction.Axis rotationAxis, boolean isLarge,
                                  boolean hasOffsetForSmallCogwheel) {

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("rotationAxis", rotationAxis.getName());
        tag.putBoolean("isLarge", isLarge);
        tag.putBoolean("hasOffsetForSmallCogwheel", hasOffsetForSmallCogwheel);
        return tag;
    }

    public static PlacingCogwheelNode read(CompoundTag tag) {
        BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        Direction.Axis rotationAxis = Direction.Axis.byName(tag.getString("rotationAxis"));
        boolean isLarge = tag.getBoolean("isLarge");
        boolean hasOffsetForSmallCogwheel = tag.getBoolean("hasOffsetForSmallCogwheel");
        return new PlacingCogwheelNode(pos, rotationAxis, isLarge, hasOffsetForSmallCogwheel);
    }

    public Vec3 center() {
        return this.pos.getCenter();
    }

    public Vec3 rotationAxisVec() {
        return Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(this.rotationAxis, Direction.AxisDirection.POSITIVE).getNormal());
    }

}
