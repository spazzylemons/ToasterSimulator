package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SProtogenSupportedMessage implements AutoRegistrableMessage {
    public SProtogenSupportedMessage() {}

    @SuppressWarnings("unused") // used via reflection
    public SProtogenSupportedMessage(PacketBuffer buffer) {}

    @Override
    public void encode(PacketBuffer buffer) {}

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Must be server-to-client
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            ClientData.setModSupportedByServer(true);
        });
        ctx.get().setPacketHandled(true);
    }
}
