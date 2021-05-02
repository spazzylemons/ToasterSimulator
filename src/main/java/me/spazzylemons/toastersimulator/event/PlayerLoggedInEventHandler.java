package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.network.SProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.network.SProtogenSupportedMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class PlayerLoggedInEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
        ToasterSimulator.getChannel().send(target, new SProtogenSupportedMessage());
        for (UUID playerId : ToasterSimulator.getProtogens()) {
            ToasterSimulator.getChannel().send(target, new SProtogenModelUpdateMessage(playerId, true));
        }
    }
}
