package me.spazzylemons.toastersimulator;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.network.ToasterNet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ToasterSimulator.ID)
public class ToasterSimulator {
    public static final String ID = "toastersimulator";
    public static final String NAME = "Toaster Simulator";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public ToasterSimulator() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        ToasterNet.setup();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientData::setup);

        LOGGER.info(NAME + " has started successfully :3");
    }
}
