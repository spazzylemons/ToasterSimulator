package me.spazzylemons.toastersimulator.client.event;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ToasterSimulator.ID, value = Dist.CLIENT)
public class LoggedOutEventHandler {
    @SubscribeEvent
    public static void onLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientTextureManager.clear();
        ClientTextureManager.setHelloReceived(false);
    }
}
