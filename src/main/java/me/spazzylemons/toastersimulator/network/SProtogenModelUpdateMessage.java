package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SProtogenModelUpdateMessage implements AutoRegistrableMessage {
    private final UUID playerId;
    private final boolean enabled;

    public SProtogenModelUpdateMessage(UUID playerId, boolean enabled) {
        this.playerId = playerId;
        this.enabled = enabled;
    }

    @SuppressWarnings("unused") // used via reflection
    public SProtogenModelUpdateMessage(PacketBuffer buffer) {
        playerId = buffer.readUUID();
        enabled = buffer.readBoolean();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(playerId);
        buffer.writeBoolean(enabled);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be server-to-client
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            Set<UUID> protogens = ClientData.getProtogens();
            if (enabled) {
                protogens.add(playerId);
            } else {
                protogens.remove(playerId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
