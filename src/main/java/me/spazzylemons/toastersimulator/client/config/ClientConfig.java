package me.spazzylemons.toastersimulator.client.config;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.network.CProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

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
    private final ForgeConfigSpec.BooleanValue enabled;
    private final byte[] texture = new byte[Constants.TEXTURE_BYTE_SIZE];
    private @Nonnull NativeImage localImage;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = builder
                .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                .define("enabled", true);
        localImage = new NativeImage(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, true);
        reloadTexture();
    }

    public void save() {
        enabled.save();
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

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
        sendToServer();
    }

    public void sendToServer() {
        if (ClientConstants.mc.getConnection() != null) {
            ToasterSimulator.getChannel().sendToServer(new CProtogenModelUpdateMessage(isEnabled(), texture));
        }
    }

    public static void loadTexture(byte[] out) {
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
}
