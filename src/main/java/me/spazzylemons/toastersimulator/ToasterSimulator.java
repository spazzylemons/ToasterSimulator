package me.spazzylemons.toastersimulator;

import me.spazzylemons.toastersimulator.config.Config;
import me.spazzylemons.toastersimulator.render.ProtogenPlayerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(Constants.MOD_ID)
public class ToasterSimulator {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);

    private static @Nullable ProtogenPlayerRenderer renderer;

    private static Config config;

    public ToasterSimulator() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(MyEventHandler.class);

        // There's probably a better way to do this that I'll encounter soon
        Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        config = specPair.getLeft();
        ModLoadingContext ctx = ModLoadingContext.get();
        ForgeConfigSpec spec = specPair.getRight();
        ModConfig modConfig = new ModConfig(ModConfig.Type.COMMON, spec, ctx.getActiveContainer());
        spec.setConfig(modConfig.getHandler().reader(FMLPaths.CONFIGDIR.get()).apply(modConfig));
        ctx.getActiveContainer().addConfig(modConfig);

        log("Beep!");
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void err(String message) {
        LOGGER.error(message);
    }

    public static @Nonnull ProtogenPlayerRenderer getRenderer() {
        if (renderer == null) {
            // lazy-load this, because when the mod's loaded the entityrendermanager won't be
            renderer = new ProtogenPlayerRenderer(Minecraft.getInstance().getEntityRenderDispatcher());
        }
        return renderer;
    }

    public static @Nonnull Config getConfig() {
        return config;
    }
}
