package me.spazzylemons.toastersimulator.network;

import me.spazzylemons.toastersimulator.util.Upvalue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ToasterNet {
    private final SimpleChannel channel;

    // not using the mod id for the channel id, because a long channel id causes issues with older servers
    public static final ResourceLocation ID = new ResourceLocation("toaster", "net");
    private static final String VERSION = "1";

    public ToasterNet() {
        channel = NetworkRegistry.newSimpleChannel(ID, () -> VERSION, VERSION::equals, VERSION::equals);
        Upvalue<Integer> i = new Upvalue<>(0);
        addMessage(i, new CModelUpdateMessageType());
        addMessage(i, new SModelUpdateMessageType());
        addMessage(i, new SHelloMessageType());
    }

    private <T, U extends MessageType<T>> void addMessage(Upvalue<Integer> i, U type) {
        int id = i.get();
        channel.registerMessage(id, type.getMessageType(), type::encode, type::decode, type::handle);
        i.set(id + 1);
    }

    public <T> void sendToServer(T message) {
        channel.sendToServer(message);
    }

    public <T> void send(PacketDistributor.PacketTarget target, T message) {
        channel.send(target, message);
    }
}
