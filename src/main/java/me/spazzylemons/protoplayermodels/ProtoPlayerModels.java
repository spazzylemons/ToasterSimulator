package me.spazzylemons.protoplayermodels;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("protoplayermodels")
public class ProtoPlayerModels {
    private static final Logger LOGGER = LogManager.getLogger("ProtoPlayerModels");

    public ProtoPlayerModels() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        log("Beep!");
    }

    public static void log(String message) {
        LOGGER.info(message);
    }
}
