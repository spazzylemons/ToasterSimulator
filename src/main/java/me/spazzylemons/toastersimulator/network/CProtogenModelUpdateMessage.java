package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class CProtogenModelUpdateMessage implements AutoRegistrableMessage {
    private final boolean enabled;

    public CProtogenModelUpdateMessage(boolean enabled) {
        this.enabled = enabled;
    }

    @SuppressWarnings("unused") // used via reflection
    public CProtogenModelUpdateMessage(PacketBuffer buffer) {
        enabled = buffer.readBoolean();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(enabled);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be client-to-server
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
            ServerPlayerEntity player = ctx.get().getSender();
            // need the player to really do anything useful with this message
            if (player == null) return;
            UUID playerId = player.getUUID();
            Set<UUID> protogens = ToasterSimulator.getProtogens();
            if (enabled) {
                protogens.add(playerId);
            } else {
                protogens.remove(playerId);
            }
            ToasterSimulator.getChannel().send(
                    PacketDistributor.ALL.noArg(),
                    new SProtogenModelUpdateMessage(playerId, enabled)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
