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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(ToasterSimulator.ID)
public class ToasterSimulator {
    public static final String ID = "toastersimulator";
    public static final String NAME = "Toaster Simulator";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final Map<UUID, byte[]> protogens = new HashMap<>();

    private static ToasterNet net;

    public ToasterSimulator() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientData::setup);

        net = new ToasterNet();

        LOGGER.info(NAME + " has started successfully :3");
    }

    public static Map<UUID, byte[]> getProtogens() {
        return protogens;
    }

    public static ToasterNet getNet() {
        return net;
    }
}
