package me.spazzylemons.toastersimulator.client.event;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.render.ProtogenPlayerRenderer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderHandEventHandler {
    // TODO i suspect this will all come crashing down when i work on multiplayer support, be ready for that, future me

    private static final Method renderArmWithItemMethod =
            ObfuscationReflectionHelper.findMethod(
                    FirstPersonRenderer.class,
                    "func_228405_a_",
                    AbstractClientPlayerEntity.class,
                    float.class,
                    float.class,
                    Hand.class,
                    float.class,
                    ItemStack.class,
                    float.class,
                    MatrixStack.class,
                    IRenderTypeBuffer.class,
                    int.class
            );

    private static class RendererSwapper implements AutoCloseable {
        private static final Field playerRenderersField =
                ObfuscationReflectionHelper.findField(EntityRendererManager.class, "field_178636_l");

        private final Map<String, PlayerRenderer> playerRenderers;
        private final PlayerRenderer defaultRenderer;
        private final PlayerRenderer slimRenderer;

        @SuppressWarnings("unchecked cast") // TODO remove
        private RendererSwapper() throws IllegalAccessException {
            // Get the player renderers
            EntityRendererManager manager = ClientConstants.mc.getEntityRenderDispatcher();
            playerRenderers = (Map<String, PlayerRenderer>) playerRenderersField.get(manager);

            // Get the default (Steve) and slim (Alex) renderers
            defaultRenderer = playerRenderers.get("default");
            slimRenderer = playerRenderers.get("slim");

            // Replace them with our own renderer
            ProtogenPlayerRenderer renderer = ClientData.getRenderer();
            playerRenderers.put("default", renderer);
            playerRenderers.put("slim", renderer);
        }

        @Override
        public void close() {
            playerRenderers.put("default", defaultRenderer);
            playerRenderers.put("slim", slimRenderer);
        }
    }

    private static class TextureSwapper implements AutoCloseable {
        private static final Field textureLocationsField =
                ObfuscationReflectionHelper.findField(NetworkPlayerInfo.class, "field_187107_a");
        private static final Field steveSkinLocationField =
                ObfuscationReflectionHelper.findField(DefaultPlayerSkin.class, "field_177337_a");
        private static final Field alexSkinLocationField =
                ObfuscationReflectionHelper.findField(DefaultPlayerSkin.class, "field_177336_b");

        private final Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;

        private final ResourceLocation steveSkinLocation;

        private final ResourceLocation alexSkinLocation;
        private final ResourceLocation playerSkinLocation;

        @SuppressWarnings("unchecked cast") // TODO remove
        public TextureSwapper() throws IllegalAccessException {
            ClientPlayerEntity player = ClientConstants.mc.player;
            assert player != null; // Player should not be null when this object is used
            NetworkPlayerInfo info = player.connection.getPlayerInfo(player.getUUID());
            if (info != null) {
                // Get the texture locations from the NetworkPlayerInfo
                textureLocations = (Map<MinecraftProfileTexture.Type, ResourceLocation>) textureLocationsField.get(info);
                playerSkinLocation = textureLocations.get(MinecraftProfileTexture.Type.SKIN);
                // Put in our own texture
                textureLocations.put(MinecraftProfileTexture.Type.SKIN, ClientConstants.textureResource);
                // Not using these fields
                steveSkinLocation = null;
                alexSkinLocation = null;
            } else {
                // Get the default skins
                steveSkinLocation = (ResourceLocation) steveSkinLocationField.get(null);
                alexSkinLocation = (ResourceLocation) alexSkinLocationField.get(null);
                // Put in our own texture
                steveSkinLocationField.set(null, ClientConstants.textureResource);
                alexSkinLocationField.set(null, ClientConstants.textureResource);
                // Not using these fields
                textureLocations = null;
                playerSkinLocation = null;
            }
        }

        @Override
        public void close() throws IllegalAccessException {
            if (textureLocations != null) {
                // Put the player skin back into the texture locations
                textureLocations.put(MinecraftProfileTexture.Type.SKIN, playerSkinLocation);
            } else {
                // Put the default skins back
                steveSkinLocationField.set(null, steveSkinLocation);
                alexSkinLocationField.set(null, alexSkinLocation);
            }
        }
    }

    private static void renderArm(RenderHandEvent event) throws InvocationTargetException, IllegalAccessException {
        renderArmWithItemMethod.invoke(
                ClientConstants.mc.getItemInHandRenderer(),
                ClientConstants.mc.player,
                event.getPartialTicks(),
                event.getInterpolatedPitch(),
                event.getHand(),
                event.getSwingProgress(),
                event.getItemStack(),
                event.getEquipProgress(),
                event.getMatrixStack(),
                event.getBuffers(),
                event.getLight()
        );
        event.setCanceled(true);
    }

    @SubscribeEvent
    // TODO not sure if I need the synchronized, since rendering doesn't seem multi-threaded, but i might be wrong
    public static synchronized void onRenderHand(RenderHandEvent event) {
        // don't render the model if disabled
        if (!ClientData.getConfig().isEnabled()) return;
        try {
            try (RendererSwapper rendererSwapper = new RendererSwapper()) {
                try (TextureSwapper textureSwapper = new TextureSwapper()) {
                    renderArm(event);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
