package me.spazzylemons.toastersimulator.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface AutoRegistrableMessage {
    void encode(PacketBuffer buffer);

    void handle(Supplier<NetworkEvent.Context> ctx);
}
