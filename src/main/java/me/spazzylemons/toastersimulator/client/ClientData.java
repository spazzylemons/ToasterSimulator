package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.client.config.ClientConfig;
import me.spazzylemons.toastersimulator.client.config.ConfigScreen;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public final class ClientData {
    private static ClientConfig config;
    private static final WeakHashMap<ModelRenderer, QuadModel> modelRenderers = new WeakHashMap<>();
    private static UUID currentlyRenderingPlayerId;

    public static final Minecraft MINECRAFT = Minecraft.getInstance();

    private ClientData() {}

    public static void setup() {
        config = ClientConfig.create();

        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (minecraft, screen) -> new ConfigScreen(screen)
        );
    }

    public static @Nonnull ClientConfig getConfig() {
        return config;
    }

    public static WeakHashMap<ModelRenderer, QuadModel> getModelRenderers() {
        return modelRenderers;
    }

    public static UUID getCurrentlyRenderingPlayerId() {
        return currentlyRenderingPlayerId;
    }

    public static void setCurrentlyRenderingPlayerId(UUID playerId) {
        currentlyRenderingPlayerId = playerId;
    }
}
