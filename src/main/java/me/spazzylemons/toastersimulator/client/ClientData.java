package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.config.ClientConfig;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public final class ClientData {
    private static ClientConfig config;
    private static final Set<UUID> protogens = new HashSet<>();
    private static boolean modSupportedByServer;

    public static final WeakHashMap<ModelRenderer, QuadModel> modelRenderers = new WeakHashMap<>();
    public static UUID currentlyRenderingPlayer;

    private ClientData() {}

    public static void setup() {
        // TODO is there a better way to do this?
        Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        config = specPair.getLeft();
        ForgeConfigSpec spec = specPair.getRight();
        ModLoadingContext ctx = ModLoadingContext.get();
        ModConfig modConfig = new ModConfig(ModConfig.Type.CLIENT, spec, ctx.getActiveContainer());
        spec.setConfig(modConfig.getHandler().reader(FMLPaths.CONFIGDIR.get()).apply(modConfig));
        ctx.getActiveContainer().addConfig(modConfig);
    }

    public static @Nonnull ClientConfig getConfig() {
        return config;
    }

    public static void addProtogen(UUID playerId, byte[] buffer) {
        ToasterSimulator.log("ADDING A PROTOGEN: ID = " + playerId);
        NativeImage image = ImageConversion.bufferToImage(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, buffer);
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(image, () -> {
            registerTexture(image, playerId);
            protogens.add(playerId);
        }));
        ToasterSimulator.log("ADDED A PROTOGEN: ID = " + playerId);
    }

    public static void removeProtogen(UUID playerId) {
        protogens.remove(playerId);
        unregisterTexture(playerId);
    }

    public static void clearProtogens() {
        protogens.clear();
    }

    public static boolean isModSupportedByServer() {
        return modSupportedByServer;
    }

    public static void setModSupportedByServer(boolean modSupportedByServer) {
        ClientData.modSupportedByServer = modSupportedByServer;
    }

    public static boolean isPlayerAProtogen(UUID playerId) {
        if (playerId == null) return false;
        if (modSupportedByServer) {
            return protogens.contains(playerId);
        } else {
            return isPlayerIdClientPlayerId(playerId) && config.isEnabled();
        }
    }

    public static boolean isPlayerIdClientPlayerId(UUID playerId) {
        ClientPlayerEntity player = ClientConstants.mc.player;
        return player != null && player.getUUID() == playerId;
    }

    private static void registerTexture(NativeImage image, UUID playerId) {
        // unregister to free existing texture, if present
        unregisterTexture(playerId);
        // create texture
        DynamicTexture texture = new DynamicTexture(image);
        TextureManager manager = ClientConstants.mc.getTextureManager();
        manager.register(getTextureLocation(playerId), texture);
    }

    private static void unregisterTexture(UUID playerId) {
        TextureManager manager = ClientConstants.mc.getTextureManager();
        ResourceLocation location = getTextureLocation(playerId);
        DynamicTexture texture = (DynamicTexture) manager.getTexture(location);
        if (texture != null) texture.close();
        manager.release(location);
    }

    public static ResourceLocation getTextureLocation(UUID playerId) {
        if (isModSupportedByServer()) {
            return new ResourceLocation(Constants.MOD_ID, "texture-" + playerId.toString());
        } else {
            return ClientConstants.localTextureResource;
        }
    }
}
