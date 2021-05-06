package me.spazzylemons.toastersimulator.client.config;

import me.spazzylemons.toastersimulator.TextureConstants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.network.CModelUpdateMessageType;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
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
    private final byte[] texture = new byte[TextureConstants.BYTE_SIZE];
    private ModConfig modConfig;

    private static final File TEXTURE_FILE =
            FMLPaths.CONFIGDIR.get().resolve(ToasterSimulator.ID + "-texture.png").toFile();

    private ClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = new CachedConfigValue<>(
                this,
                builder
                    .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                    .define("enabled", true)
        );
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
        if (ClientData.MINECRAFT.getConnection() != null) {
            ToasterSimulator.getNet().sendToServer(new CModelUpdateMessageType.Message(isEnabled(), texture));
        }
    }

    public void reloadTexture() {
        loadTexture(texture);
        ClientTextureManager.setLocal(texture);
        sendToServer();
    }

    public static ClientConfig create() {
        Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);

        ModContainer container = ModList.get().getModContainerById(ToasterSimulator.ID).orElse(null);
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
                stream = new FileInputStream(TEXTURE_FILE);
            } catch (FileNotFoundException e) {
                URL resourceURL = ToasterSimulator.class.getResource("/texture.png");
                if (resourceURL == null) {
                    throw new IOException("Cannot open resource");
                }
                try (FileChannel fileCh = new FileOutputStream(TEXTURE_FILE).getChannel()) {
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

    public ModConfig getModConfig() {
        return modConfig;
    }
}
