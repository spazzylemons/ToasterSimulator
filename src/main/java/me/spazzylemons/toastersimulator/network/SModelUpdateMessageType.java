package me.spazzylemons.toastersimulator.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.spazzylemons.toastersimulator.TextureConstants;
import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import me.spazzylemons.toastersimulator.util.Compression;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SModelUpdateMessageType implements MessageType<SModelUpdateMessageType.Message> {
    @Override
    public Class<Message> getMessageType() {
        return Message.class;
    }

    @Override
    public void encode(Message message, PacketBuffer buffer) {
        buffer.writeUUID(message.playerId);
        buffer.writeBoolean(message.enabled);
        if (message.enabled) {
            Exceptions.wrapChecked(() -> Compression.compress(message.texture, new ByteBufOutputStream(buffer)));
        }
    }

    @Override
    public Message decode(PacketBuffer buffer) {
        UUID playerId = buffer.readUUID();
        boolean enabled = buffer.readBoolean();
        byte[] texture;
        if (enabled) {
            texture = new byte[TextureConstants.BYTE_SIZE];
            Exceptions.wrapChecked(() -> Compression.decompress(new ByteBufInputStream(buffer), texture));
        } else {
            texture = null;
        }
        return new Message(playerId, enabled, texture);
    }

    @Override
    public void handle(Message message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be server-to-client
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            if (message.enabled) {
                ClientTextureManager.add(message.playerId, message.texture);
            } else {
                ClientTextureManager.remove(message.playerId);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static class Message {
        private final UUID playerId;
        private final boolean enabled;
        private final byte[] texture;

        public Message(UUID playerId, boolean enabled, byte[] texture) {
            this.playerId = playerId;
            this.enabled = enabled;
            if (enabled) {
                this.texture = texture;
            } else {
                this.texture = null;
            }
        }
    }
}
