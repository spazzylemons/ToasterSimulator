package me.spazzylemons.toastersimulator.client.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.network.CModelUpdateMessageType;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private final CachedConfigValue<Boolean> enabled;
    private final byte[] texture = new byte[Constants.TEXTURE_BYTE_SIZE];
    private @Nonnull NativeImage localImage;
    private ModConfig modConfig;

    private ClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = new CachedConfigValue<>(
                this,
                builder
                    .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                    .define("enabled", true)
        );
        localImage = new NativeImage(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, true);
        reloadTexture();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
        sendToServer();
    }

    public void sendToServer() {
        if (ClientConstants.mc.getConnection() != null) {
            ToasterSimulator.getNet().sendToServer(new CModelUpdateMessageType.Message(isEnabled(), texture));
        }
    }

    public void reloadTexture() {
        loadTexture(texture);
        NativeImage newImage = ImageConversion.bufferToImage(
                Constants.TEXTURE_WIDTH,
                Constants.TEXTURE_HEIGHT,
                texture
        );
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(newImage, localImage::close));
        localImage = newImage;
        DynamicTexture texture = new DynamicTexture(localImage);
        ClientConstants.mc.getTextureManager().register(ClientConstants.localTextureResource, texture);
        sendToServer();
    }

    private void withConfigOpen(Runnable f) {
        ForgeConfigSpec spec = modConfig.getSpec();
        ConfigFileTypeHandler handler = modConfig.getHandler();
        try (CommentedFileConfig configData = handler.reader(FMLPaths.CONFIGDIR.get()).apply(modConfig)) {
            spec.setConfig(configData);
            f.run();
        } finally {
            spec.setConfig(null);
        }
    }

    public static ClientConfig create() {
        Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);

        ModContainer container = ModList.get().getModContainerById(Constants.MOD_ID).orElse(null);
        assert container != null; // our own mod should be loaded, or we've got bigger problems

        ForgeConfigSpec spec = specPair.getRight();
        ModConfig modConfig = new ModConfig(ModConfig.Type.CLIENT, spec, container);
        container.addConfig(modConfig);

        ClientConfig config = specPair.getLeft();
        config.modConfig = modConfig;

        return config;
    }

    private static void loadTexture(byte[] out) {
        Exceptions.wrapChecked(() -> {
            InputStream stream;
            try {
                stream = new FileInputStream(ClientConstants.textureFile);
            } catch (FileNotFoundException e) {
                URL resourceURL = ToasterSimulator.class.getResource("/texture.png");
                if (resourceURL == null) {
                    throw new IOException("Cannot open resource");
                }
                try (FileChannel fileCh = new FileOutputStream(ClientConstants.textureFile).getChannel()) {
                    int size = resourceURL.openConnection().getContentLength();
                    int pos = 0;
                    try (ReadableByteChannel resourceCh = Channels.newChannel(resourceURL.openStream())) {
                        while (pos < size) {
                            pos += fileCh.transferFrom(resourceCh, pos, size - pos);
                        }
                    }
                }
                stream = resourceURL.openStream();
            }
            try (NativeImage image = NativeImage.read(stream)) {
                ImageConversion.imageToBuffer(out, image);
            }
        });
    }

    private static class CachedConfigValue<T> {
        private final ClientConfig config;
        private final ForgeConfigSpec.ConfigValue<T> value;
        private T cache = null;

        private CachedConfigValue(ClientConfig config, ForgeConfigSpec.ConfigValue<T> value) {
            this.config = config;
            this.value = value;
        }

        private T get() {
            if (cache == null) {
                config.withConfigOpen(() -> cache = value.get());
            }
            return cache;
        }

        private void set(T t) {
            config.withConfigOpen(() -> {
                value.set(t);
                value.save();
            });
            cache = t;
        }
    }
}
