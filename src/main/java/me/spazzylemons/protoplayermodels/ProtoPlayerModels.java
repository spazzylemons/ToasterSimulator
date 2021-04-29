package me.spazzylemons.protoplayermodels;

import me.spazzylemons.protoplayermodels.render.ProtogenPlayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(Constants.MOD_ID)
public class ProtoPlayerModels {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);

    private static @Nullable ProtogenPlayerRenderer renderer;

    public ProtoPlayerModels() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ProtoPlayerModelsEventHandler.class);
        log("Beep!");
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void err(String message) {
        LOGGER.error(message);
    }

    public static @Nullable ProtogenPlayerRenderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(ProtogenPlayerRenderer renderer) {
        ProtoPlayerModels.renderer = renderer;
    }
}
