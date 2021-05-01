package me.spazzylemons.toastersimulator.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private final ForgeConfigSpec.BooleanValue enabled;

    public Config(ForgeConfigSpec.Builder builder) {
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
