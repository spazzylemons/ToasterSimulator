package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.client.config.ClientConfig;
import me.spazzylemons.toastersimulator.client.render.ProtogenPlayerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class ClientData {
    private static ClientConfig config;
    private static @Nullable ProtogenPlayerRenderer renderer;

    private ClientData() {}

    public static void setup() {
        MinecraftForge.EVENT_BUS.register(MyEventHandler.class);

        // TODO is there a better way to do this?
        Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        config = specPair.getLeft();
        ForgeConfigSpec spec = specPair.getRight();
        ModLoadingContext ctx = ModLoadingContext.get();
        ModConfig modConfig = new ModConfig(ModConfig.Type.CLIENT, spec, ctx.getActiveContainer());
        spec.setConfig(modConfig.getHandler().reader(FMLPaths.CONFIGDIR.get()).apply(modConfig));
        ctx.getActiveContainer().addConfig(modConfig);
    }

    public static @Nonnull ClientConfig getConfig() {
        return config;
    }

    public static @Nonnull ProtogenPlayerRenderer getRenderer() {
        if (renderer == null) {
            // lazy-load this, because when the mod's loaded the EntityRenderManager won't be
            renderer = new ProtogenPlayerRenderer(Minecraft.getInstance().getEntityRenderDispatcher());
        }
        return renderer;
    }
}
