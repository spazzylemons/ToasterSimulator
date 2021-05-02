package me.spazzylemons.toastersimulator.client.event;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.render.ProtogenPlayerRenderer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class RenderPlayerEventHandler {
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        // don't render the model if the player doesn't have it
        if (!ClientData.isPlayerAProtogen(event.getPlayer().getUUID())) return;
        // prevent infinite recursion (ProtogenPlayerRenderer inherits from PlayerRenderer, and by making it render
        // here, it will call the event again and you'll have a bad time if you don't check the type here)
        if (event.getRenderer() instanceof ProtogenPlayerRenderer) return;
        // render it ourselves
        ClientData.getRenderer().render(
                (AbstractClientPlayerEntity) event.getPlayer(),
                0F, // i think this parameter goes unused?
                event.getPartialRenderTick(),
                event.getMatrixStack(),
                event.getBuffers(),
                event.getLight()
        );
        event.setCanceled(true);
    }
}
