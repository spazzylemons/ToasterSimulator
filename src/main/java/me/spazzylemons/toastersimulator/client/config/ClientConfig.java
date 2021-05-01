package me.spazzylemons.toastersimulator.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

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
    }
}
