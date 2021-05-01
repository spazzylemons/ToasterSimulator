package me.spazzylemons.toastersimulator.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.config.ConfigScreen;
import me.spazzylemons.toastersimulator.client.render.ProtogenPlayerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MyEventHandler {
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent event) {
        // don't render the model if disabled
        if (!ClientData.getConfig().isEnabled()) return;
        // prevent infinite recursion (ProtogenPlayerRenderer inherits from PlayerRenderer, and by making it render
        // here, it will call the event again and you'll have a bad time if you don't check the type here)
        if (event.getRenderer() instanceof ProtogenPlayerRenderer) return;
        // render it ourselves
        ClientData.getRenderer().render(
                (AbstractClientPlayerEntity) event.getPlayer(),
                0F, // i think this parameter goes unused?
                event.getPartialRenderTick(),
                event.getMatrixStack(),
                event.getBuffers(),
                event.getLight()
        );
        event.setCanceled(true);
    }

    // whole bunch of stuff needed to render a custom hand
    // ugly solution but it works
    // fabric was better for this stuff
    // TODO i suspect this will all come crashing down when i work on multiplayer support, be ready for that, future me

    private static final Field playerRenderersField =
            ObfuscationReflectionHelper.findField(EntityRendererManager.class, "playerRenderers");
    private static final Field textureLocationsField =
            ObfuscationReflectionHelper.findField(NetworkPlayerInfo.class, "textureLocations");
    private static final Field steveSkinLocationField =
            ObfuscationReflectionHelper.findField(DefaultPlayerSkin.class, "STEVE_SKIN_LOCATION");
    private static final Field alexSkinLocationField =
            ObfuscationReflectionHelper.findField(DefaultPlayerSkin.class, "ALEX_SKIN_LOCATION");
    private static final Method renderArmWithItemMethod =
            ObfuscationReflectionHelper.findMethod(
                    FirstPersonRenderer.class,
                    "renderArmWithItem",
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

    private static Map<String, PlayerRenderer> playerRenderers;

    @SuppressWarnings("unchecked cast") // TODO remove
    private static void getPlayerRenderers() throws IllegalAccessException {
        EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
        playerRenderers = (Map<String, PlayerRenderer>) playerRenderersField.get(manager);
    }

    private static PlayerRenderer oldDefault = null;
    private static PlayerRenderer oldSlim = null;

    private static void savePlayerRenderers() {
        oldDefault = playerRenderers.get("default");
        oldSlim = playerRenderers.get("slim");
    }

    private static void restorePlayerRenderers() {
        playerRenderers.put("default", oldDefault);
        playerRenderers.put("slim", oldSlim);
    }

    private static NetworkPlayerInfo info = null;
    private static Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations = null;
    private static ResourceLocation oldSkinLocation1 = null;
    private static ResourceLocation oldSkinLocation2 = null;

    @SuppressWarnings("unchecked cast") // TODO remove
    private static void saveSkinLocations()
    throws IllegalAccessException {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        assert connection != null;
        info = connection.getPlayerInfo(player.getUUID());
        if (info != null) {
            textureLocations = (Map<MinecraftProfileTexture.Type, ResourceLocation>) textureLocationsField.get(info);
            oldSkinLocation1 = textureLocations.get(MinecraftProfileTexture.Type.SKIN);
        } else {
            oldSkinLocation1 = (ResourceLocation) steveSkinLocationField.get(null);
            oldSkinLocation2 = (ResourceLocation) alexSkinLocationField.get(null);
        }
    }

    private static void restoreSkinLocations() throws IllegalAccessException {
        if (info != null) {
            textureLocations.put(MinecraftProfileTexture.Type.SKIN, oldSkinLocation1);
        } else {
            steveSkinLocationField.set(null, oldSkinLocation1);
            alexSkinLocationField.set(null, oldSkinLocation2);
        }
    }

    private static void overwriteHandRenderSettings(Map<String, PlayerRenderer> playerRenderers) throws IllegalAccessException {
        ProtogenPlayerRenderer renderer = ClientData.getRenderer();
        playerRenderers.put("default", renderer);
        playerRenderers.put("slim", renderer);
        if (info != null) {
            textureLocations.put(MinecraftProfileTexture.Type.SKIN, ClientConstants.textureResource);
        } else {
            steveSkinLocationField.set(null, ClientConstants.textureResource);
            alexSkinLocationField.set(null, ClientConstants.textureResource);
        }
    }

    private static void renderArm(RenderHandEvent event) throws InvocationTargetException, IllegalAccessException {
        renderArmWithItemMethod.invoke(
                Minecraft.getInstance().getItemInHandRenderer(),
                Minecraft.getInstance().player,
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
    // TODO not sure if I need the synchronized, but if rendering is multi-threaded (doesn't look that way but idk),
    // then this could would cause weird rendering stuff
    public static synchronized void onRenderHand(RenderHandEvent event) {
        // don't render the model if disabled
        if (!ClientData.getConfig().isEnabled()) return;
        try {
            getPlayerRenderers();
            savePlayerRenderers();
            saveSkinLocations();
            try {
                overwriteHandRenderSettings(playerRenderers);
                renderArm(event);
            } finally {
                restoreSkinLocations();
                restorePlayerRenderers();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onSomething(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        if (!(screen instanceof CustomizeSkinScreen)) return;

        event.addWidget(new Button(
                screen.width / 2 - 100,
                screen.height / 6 + 24 * 5,
                200,
                20,
                new StringTextComponent(Constants.MOD_NAME + " options..."),
                button -> screen.getMinecraft().setScreen(new ConfigScreen(screen))
        ));
    }
}
