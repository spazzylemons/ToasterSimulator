package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.TextureConstants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientTextureManager {
    private static final Set<UUID> playerIds = new HashSet<>();
    private static NativeImage localImage = new NativeImage(1, 1, false);
    private static boolean helloReceived;

    public static final ResourceLocation LOCAL_LOCATION = new ResourceLocation(ToasterSimulator.ID, "texture-local");

    private ClientTextureManager() {}

    public static void add(UUID playerId, byte[] buffer) {
        // convert bytes to a NativeImage
        NativeImage image = ImageConversion.bufferToImage(TextureConstants.WIDTH, TextureConstants.HEIGHT, buffer);
        // free the image if this fails
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(image, () -> {
            // register the texture
            registerTexture(image, playerId);
            // add to the set
            playerIds.add(playerId);
        }));
    }

    public static void remove(UUID playerId) {
        // free the texture
        unregisterTexture(playerId);
        // remove from the set
        playerIds.remove(playerId);
    }

    public static void clear() {
        // free all textures
        for (UUID playerId : playerIds) {
            unregisterTexture(playerId);
        }
        // clear the set
        playerIds.clear();
    }

    public static ResourceLocation get(UUID playerId) {
        if (playerId == null) return null;
        if (helloReceived) {
            if (playerIds.contains(playerId)) {
                return generate(playerId);
            }
        } else {
            ClientPlayerEntity player = ClientData.MINECRAFT.player;
            if (player != null && player.getUUID() == playerId && ClientData.getConfig().isEnabled()) {
                return ClientTextureManager.LOCAL_LOCATION;
            }
        }
        return null;
    }

    public static void setLocal(byte[] data) {
        NativeImage newImage = ImageConversion.bufferToImage(TextureConstants.WIDTH, TextureConstants.HEIGHT, data);
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(newImage, localImage::close));
        localImage = newImage;
        DynamicTexture texture = new DynamicTexture(localImage);

        ClientData.MINECRAFT.getTextureManager().register(ClientTextureManager.LOCAL_LOCATION, texture);
    }

    public static boolean isHelloReceived() {
        return helloReceived;
    }

    public static void setHelloReceived(boolean value) {
        helloReceived = value;
    }

    private static void registerTexture(NativeImage image, UUID playerId) {
        // unregister to free existing texture, if present
        unregisterTexture(playerId);
        // make new texture and register it
        ClientData.MINECRAFT.getTextureManager().register(generate(playerId), new DynamicTexture(image));
    }

    private static void unregisterTexture(UUID playerId) {
        TextureManager mcManager = ClientData.MINECRAFT.getTextureManager();
        // location of texture
        ResourceLocation location = generate(playerId);
        // get the texture from the texture manager
        DynamicTexture texture = (DynamicTexture) mcManager.getTexture(location);
        // close it if it exists
        if (texture != null) {
            texture.close();
            mcManager.release(location);
        }
    }

    private static ResourceLocation generate(UUID playerId) {
        return new ResourceLocation(ToasterSimulator.ID, "texture-" + playerId);
    }
}
