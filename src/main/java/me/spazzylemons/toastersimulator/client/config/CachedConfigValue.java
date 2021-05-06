package me.spazzylemons.toastersimulator.client.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class CachedConfigValue<T> {
    private final ClientConfig config;
    private final ForgeConfigSpec.ConfigValue<T> value;
    private T cache = null;

    public CachedConfigValue(ClientConfig config, ForgeConfigSpec.ConfigValue<T> value) {
        this.config = config;
        this.value = value;
    }

    public T get() {
        if (cache == null) {
            withConfigOpen(() -> cache = value.get());
        }
        return cache;
    }

    public void set(T t) {
        withConfigOpen(() -> {
            value.set(t);
            value.save();
        });
        cache = t;
    }

    private void withConfigOpen(Runnable f) {
        ModConfig modConfig = config.getModConfig();
        ForgeConfigSpec spec = modConfig.getSpec();
        ConfigFileTypeHandler handler = modConfig.getHandler();
        try (CommentedFileConfig configData = handler.reader(FMLPaths.CONFIGDIR.get()).apply(modConfig)) {
            spec.setConfig(configData);
            f.run();
        } finally {
            spec.setConfig(null);
        }
    }
}
