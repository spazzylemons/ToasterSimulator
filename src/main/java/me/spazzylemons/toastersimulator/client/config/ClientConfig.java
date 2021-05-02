package me.spazzylemons.toastersimulator.client.config;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.network.CProtogenModelUpdateMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private final ForgeConfigSpec.BooleanValue enabled;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = builder
                .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                .define("enabled", true);
    }

    public void save() {
        enabled.save();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
        sendToServer();
    }

    public void sendToServer() {
        ToasterSimulator.getChannel().sendToServer(new CProtogenModelUpdateMessage(isEnabled()));
    }
}
