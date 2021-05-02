package me.spazzylemons.toastersimulator.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.util.Compression;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SProtogenModelUpdateMessage implements AutoRegistrableMessage {
    private final UUID playerId;
    private final boolean enabled;
    private final byte[] texture;

    public SProtogenModelUpdateMessage(UUID playerId, boolean enabled, byte[] texture) {
        this.playerId = playerId;
        this.enabled = enabled;
        if (enabled) {
            this.texture = texture;
        } else {
            this.texture = null;
        }
    }

    @SuppressWarnings("unused") // used via reflection
    public SProtogenModelUpdateMessage(PacketBuffer buffer) {
        playerId = buffer.readUUID();
        enabled = buffer.readBoolean();
        if (enabled) {
            texture = new byte[Constants.TEXTURE_BYTE_SIZE];
            Exceptions.wrapChecked(() -> {
                Compression.decompress(new ByteBufInputStream(buffer), texture);
            });
        } else {
            texture = null;
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(playerId);
        buffer.writeBoolean(enabled);
        if (enabled) {
            Exceptions.wrapChecked(() -> {
                Compression.compress(texture, new ByteBufOutputStream(buffer));
            });
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be server-to-client
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            if (enabled) {
                ClientData.addProtogen(playerId, texture);
            } else {
                ClientData.removeProtogen(playerId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
