package me.spazzylemons.toastersimulator.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.spazzylemons.toastersimulator.ServerTextureManager;
import me.spazzylemons.toastersimulator.TextureConstants;
import me.spazzylemons.toastersimulator.util.Compression;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class CModelUpdateMessageType implements MessageType<CModelUpdateMessageType.Message> {
    @Override
    public Class<Message> getMessageType() {
        return Message.class;
    }

    @Override
    public void encode(Message message, PacketBuffer buffer) {
        buffer.writeBoolean(message.enabled);
        if (message.enabled) {
            Exceptions.wrapChecked(() -> Compression.compress(message.texture, new ByteBufOutputStream(buffer)));
        }
    }

    @Override
    public Message decode(PacketBuffer buffer) {
        boolean enabled = buffer.readBoolean();
        byte[] texture;
        if (enabled) {
            texture = new byte[TextureConstants.BYTE_SIZE];
            Exceptions.wrapChecked(() -> {
                Compression.decompress(new ByteBufInputStream(buffer), texture);
            });
        } else {
            texture = null;
        }
        return new Message(enabled, texture);
    }

    @Override
    public void handle(Message message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be client-to-server
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
            ServerPlayerEntity player = ctx.get().getSender();
            // need the player to really do anything useful with this message
            if (player == null) return;
            UUID playerId = player.getUUID();
            if (message.enabled) {
                ServerTextureManager.put(playerId, message.texture);
            } else {
                ServerTextureManager.remove(playerId);
            }
            ToasterNet.send(
                    PacketDistributor.ALL.noArg(),
                    new SModelUpdateMessageType.Message(playerId, message.enabled, message.texture)
            );
        });
        ctx.get().setPacketHandled(true);
    }

    public static class Message {
        private final boolean enabled;
        private final byte[] texture;

        public Message(boolean enabled, byte[] texture) {
            this.enabled = enabled;
            if (enabled) {
                this.texture = texture;
            } else {
                this.texture = null;
            }
        }
    }
}
