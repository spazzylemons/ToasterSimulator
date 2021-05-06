package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SHelloMessageType implements MessageType<SHelloMessageType.Message> {
    @Override
    public Class<Message> getMessageType() {
        return Message.class;
    }

    @Override
    public void encode(Message message, PacketBuffer buffer) {
        // nothing to encode
    }

    @Override
    public Message decode(PacketBuffer buffer) {
        return Message.INSTANCE;
    }

    @Override
    public void handle(Message message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be server-to-client
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            ClientTextureManager.setHelloReceived(true);
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Message { INSTANCE }
}
