package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.ServerTextureManager;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod.EventBusSubscriber(modid = ToasterSimulator.ID)
public class ServerStoppedEventHandler {
    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        ServerTextureManager.clear();
    }
}
