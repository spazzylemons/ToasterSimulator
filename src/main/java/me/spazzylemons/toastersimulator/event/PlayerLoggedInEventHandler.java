package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.network.SHelloMessageType;
import me.spazzylemons.toastersimulator.network.SModelUpdateMessageType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ToasterSimulator.ID)
public class PlayerLoggedInEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
        ToasterSimulator.getNet().send(target, SHelloMessageType.Message.INSTANCE);
        for (Map.Entry<UUID, byte[]> entry : ToasterSimulator.getProtogens().entrySet()) {
            SModelUpdateMessageType.Message message = new SModelUpdateMessageType.Message(entry.getKey(), true, entry.getValue());
            ToasterSimulator.getNet().send(target, message);
        }
    }
}
