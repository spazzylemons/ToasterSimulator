package me.spazzylemons.toastersimulator.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface MessageType<T> {
    Class<T> getMessageType();
    void encode(T message, PacketBuffer buffer);
    T decode(PacketBuffer buffer);
    void handle(T message, Supplier<NetworkEvent.Context> ctx);
}
