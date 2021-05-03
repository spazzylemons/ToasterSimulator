package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.config.ClientConfig;
import me.spazzylemons.toastersimulator.client.event.InitGuiEventHandler;
import me.spazzylemons.toastersimulator.client.event.LoggedInEventHandler;
import me.spazzylemons.toastersimulator.client.event.LoggedOutEventHandler;
import me.spazzylemons.toastersimulator.client.event.RenderHandEventHandler;
import me.spazzylemons.toastersimulator.client.event.RenderPlayerEventHandler;
import me.spazzylemons.toastersimulator.client.render.ProtogenPlayerRenderer;
import me.spazzylemons.toastersimulator.client.util.ImageConversion;
import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientData {
    private static ClientConfig config;
    private static @Nullable ProtogenPlayerRenderer renderer;
    private static final Set<UUID> protogens = new HashSet<>();
    private static boolean modSupportedByServer;

    private ClientData() {}

    public static void setup() {
        MinecraftForge.EVENT_BUS.register(InitGuiEventHandler.class);
        MinecraftForge.EVENT_BUS.register(LoggedInEventHandler.class);
        MinecraftForge.EVENT_BUS.register(LoggedOutEventHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderHandEventHandler.class);
        // MinecraftForge.EVENT_BUS.register(RenderPlayerEventHandler.class);

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

    public static @Nonnull ProtogenPlayerRenderer getRenderer() {
        if (renderer == null) {
            // lazy-load this, because when the mod's loaded the EntityRenderManager won't be
            renderer = new ProtogenPlayerRenderer(ClientConstants.mc.getEntityRenderDispatcher());
        }
        return renderer;
    }

    public static void addProtogen(UUID playerId, byte[] buffer) {
        NativeImage image = ImageConversion.bufferToImage(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, buffer);
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(image, () -> {
            registerTexture(image, playerId);
            protogens.add(playerId);
        }));
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

    public static boolean areWeAProtogen() {
        ClientPlayerEntity player = ClientConstants.mc.player;
        if (player == null) return false;
        return isPlayerAProtogen(player.getUUID());
    }

    public static boolean isPlayerAProtogen(UUID playerId) {
        if (modSupportedByServer) {
            return protogens.contains(playerId);
        } else {
            return isPlayerIdClientPlayerId(playerId);
        }
    }

    private static boolean isPlayerIdClientPlayerId(UUID playerId) {
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
        return new ResourceLocation(Constants.MOD_ID, "texture-" + playerId.toString());
    }
}
