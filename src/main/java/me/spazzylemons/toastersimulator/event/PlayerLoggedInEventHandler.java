package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.network.SProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.network.SProtogenSupportedMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class PlayerLoggedInEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
        ToasterSimulator.getChannel().send(target, new SProtogenSupportedMessage());
        for (Map.Entry<UUID, byte[]> entry : ToasterSimulator.getProtogens().entrySet()) {
            SProtogenModelUpdateMessage message = new SProtogenModelUpdateMessage(entry.getKey(), true, entry.getValue());
            ToasterSimulator.getChannel().send(target, message);
        }
    }
}
