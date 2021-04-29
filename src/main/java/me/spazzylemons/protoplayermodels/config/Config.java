package me.spazzylemons.protoplayermodels.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private final ForgeConfigSpec.BooleanValue enabled;

    public Config(ForgeConfigSpec.Builder spec) {
        enabled = spec
                .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                .define("enabled", false);
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
