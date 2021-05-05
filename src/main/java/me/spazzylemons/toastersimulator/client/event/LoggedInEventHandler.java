package me.spazzylemons.toastersimulator.client.event;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class LoggedInEventHandler {
    @SubscribeEvent
    public static void onLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientData.getConfig().sendToServer();
    }
}
