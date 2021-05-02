package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class ServerStoppedEventHandler {
    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        ToasterSimulator.getProtogens().clear();
    }
}
