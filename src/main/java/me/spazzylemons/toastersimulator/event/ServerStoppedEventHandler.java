package me.spazzylemons.toastersimulator.event;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ServerStoppedEventHandler {
    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        ToasterSimulator.getProtogens().clear();
    }
}
