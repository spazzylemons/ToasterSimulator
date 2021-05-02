package me.spazzylemons.toastersimulator.client.event;

import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class LoggedOutEventHandler {
    @SubscribeEvent
    public static void onLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientData.clearProtogens();
        ClientData.setModSupportedByServer(false);
    }
}
