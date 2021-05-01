package me.spazzylemons.toastersimulator;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.MyEventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.MOD_ID)
public class ToasterSimulator {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);

    public ToasterSimulator() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientData::setup);

        log("Beep!");
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void err(String message) {
        LOGGER.error(message);
    }
}
