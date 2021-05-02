package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class CProtogenModelUpdateMessage implements AutoRegistrableMessage {
    private final boolean enabled;
    private final byte[] texture;

    public CProtogenModelUpdateMessage(boolean enabled, byte[] texture) {
        this.enabled = enabled;
        if (enabled) {
            this.texture = texture;
        } else {
            this.texture = null;
        }
    }

    @SuppressWarnings("unused") // used via reflection
    public CProtogenModelUpdateMessage(PacketBuffer buffer) {
        enabled = buffer.readBoolean();
        if (enabled) {
            try {
                texture = new byte[ImageTransfer.IMAGE_BYTE_SIZE];
                ImageTransfer.readCompressed(texture, buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            texture = null;
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(enabled);
        if (enabled) {
            try {
                ImageTransfer.writeCompressed(texture, buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
            if (enabled) {
                ToasterSimulator.getProtogens().put(playerId, texture);
            } else {
                ToasterSimulator.getProtogens().remove(playerId);
            }
            ToasterSimulator.getChannel().send(
                    PacketDistributor.ALL.noArg(),
                    new SProtogenModelUpdateMessage(playerId, enabled, texture)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
