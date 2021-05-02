package me.spazzylemons.toastersimulator.client.config;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.network.CProtogenModelUpdateMessage;
import me.spazzylemons.toastersimulator.network.ImageTransfer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private final ForgeConfigSpec.BooleanValue enabled;
    private byte[] texture;
    private NativeImage localImage;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        enabled = builder
                .comment("If true, the protogen model will be displayed instead of the vanilla model.")
                .define("enabled", true);
        reloadTexture();
    }

    public void save() {
        enabled.save();
    }

    public void reloadTexture() {
        texture = loadTexture();
        NativeImage newImage = ImageTransfer.bufferToImage(texture);
        try {
            if (localImage != null) localImage.close();
        } finally {
            localImage = newImage;
        }
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

    public static byte[] loadTexture() {
        try {
            NativeImage image;
            try {
                image = NativeImage.read(new FileInputStream(ClientConstants.textureFile));
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
                image = NativeImage.read(resourceURL.openStream());
            }
            try {
                return ImageTransfer.imageToBuffer(image);
            } finally {
                image.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
