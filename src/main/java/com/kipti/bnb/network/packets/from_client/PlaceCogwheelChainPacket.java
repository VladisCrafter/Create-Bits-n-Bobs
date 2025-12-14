package com.kipti.bnb.network.packets.from_client;

import com.kipti.bnb.content.cogwheel_chain.graph.*;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlaceCogwheelChainPacket extends SimplePacketBase {

    PlacingCogwheelChain worldSpacePartialChain;
    int priorityChainTakeHand;

    public PlaceCogwheelChainPacket(PlacingCogwheelChain worldSpacePartialChain, int priorityChainTakeHand) {
        this.worldSpacePartialChain = worldSpacePartialChain;
        this.priorityChainTakeHand = priorityChainTakeHand;
    }

    public PlaceCogwheelChainPacket(FriendlyByteBuf buffer) {
        this.worldSpacePartialChain = PlacingCogwheelChain.readFromBuffer(buffer);
        this.priorityChainTakeHand = buffer.readInt();
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            MinecraftServer server = player.getServer();
            handle(server, player);
        });
        return true;
    }

    public void handle(@Nullable MinecraftServer server, @Nullable ServerPlayer player) {
        //Server side validation of the chain
        if (worldSpacePartialChain.maxBounds() > PlacingCogwheelChain.MAX_CHAIN_BOUNDS)
            return;

        if (!worldSpacePartialChain.checkMatchingNodesInLevel(player.level()))
            return;

        final int chainsRequired = worldSpacePartialChain.getChainsRequiredInLoop();

        final boolean hasEnough = player.isCreative() || ChainConveyorBlockEntity.getChainsFromInventory(player, Items.CHAIN.getDefaultInstance(), chainsRequired, true);
        if (!hasEnough)
            return;
        if (!player.isCreative())
            ChainConveyorBlockEntity.getChainsFromInventory(player, Items.CHAIN.getDefaultInstance(), chainsRequired, false);

        final List<PathedCogwheelNode> chainGeometry;
        try {
            chainGeometry = CogwheelChainPathfinder.buildChainPath(worldSpacePartialChain);
        } catch (final
        ChainInteractionFailedException ignored) { //We assume the client has been notified if the path was invalid, anything else is tampering
            return;
        }
        if (chainGeometry == null)
            return;

        final CogwheelChain chain = new CogwheelChain(chainGeometry);

        chain.placeInLevel(player.level(), worldSpacePartialChain);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        PlacingCogwheelChain.writeToBuffer(worldSpacePartialChain, buffer);
        buffer.writeInt(priorityChainTakeHand);
    }

}
