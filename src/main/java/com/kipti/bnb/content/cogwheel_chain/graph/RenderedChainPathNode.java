package com.kipti.bnb.content.cogwheel_chain.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public record RenderedChainPathNode(BlockPos relativePos, Vec3 nodeOffset) {

    public Vec3 getPosition() {
        return relativePos.getCenter().add(nodeOffset);
    }

    public void write(CompoundTag tag) {
        tag.putInt("X", relativePos.getX());
        tag.putInt("Y", relativePos.getY());
        tag.putInt("Z", relativePos.getZ());
        tag.putDouble("OffsetX", nodeOffset.x);
        tag.putDouble("OffsetY", nodeOffset.y);
        tag.putDouble("OffsetZ", nodeOffset.z);
    }

    public static RenderedChainPathNode read(CompoundTag tag) {
        BlockPos pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
        Vec3 offset = new Vec3(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
        return new RenderedChainPathNode(pos, offset);
    }

}
