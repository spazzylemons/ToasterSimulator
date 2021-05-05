package me.spazzylemons.toastersimulator;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.network.AutoRegistrableMessage;
import me.spazzylemons.toastersimulator.network.CProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.network.SProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.network.SProtogenSupportedMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Mod(Constants.MOD_ID)
public class ToasterSimulator {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);

    private static final Map<UUID, byte[]> protogens = new HashMap<>();

    private static SimpleChannel channel;

    public ToasterSimulator() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientData::setup);

        channel = NetworkRegistry.newSimpleChannel(
                Constants.channelId,
                () -> Constants.PROTOCOL_VERSION,
                Constants.PROTOCOL_VERSION::equals,
                Constants.PROTOCOL_VERSION::equals
        );

        int[] i = {0};
        addMessage(i, CProtogenModelUpdateMessage.class);
        addMessage(i, SProtogenModelUpdateMessage.class);
        addMessage(i, SProtogenSupportedMessage.class);

        log(Constants.MOD_NAME + "has started successfully :3");
    }

    private static <T extends AutoRegistrableMessage> void addMessage(int[] i, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor(PacketBuffer.class);
            Function<PacketBuffer, T> function = buf -> {
                try {
                    return constructor.newInstance(buf);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            channel.registerMessage(
                    i[0]++,
                    clazz,
                    AutoRegistrableMessage::encode,
                    function,
                    AutoRegistrableMessage::handle
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void err(String message) {
        LOGGER.error(message);
    }

    public static Map<UUID, byte[]> getProtogens() {
        return protogens;
    }

    public static SimpleChannel getChannel() {
        return channel;
    }
}
