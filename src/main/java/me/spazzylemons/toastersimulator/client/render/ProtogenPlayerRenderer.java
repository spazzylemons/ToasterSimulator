package me.spazzylemons.toastersimulator.client.render;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.client.model.ProtogenPlayerModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class ProtogenPlayerRenderer extends PlayerRenderer {
    public ProtogenPlayerRenderer(EntityRendererManager manager) {
        super(manager);
        model = new ProtogenPlayerModel<>(0F);
    }

    @Override
    public @Nonnull ResourceLocation getTextureLocation(@Nonnull AbstractClientPlayerEntity entity) {
        return ClientConstants.textureResource;
    }

    static {
        NativeImage nativeImage;
        try {
            InputStream resource = ProtogenPlayerRenderer.class.getClassLoader().getResourceAsStream("proto.png");
            if (resource != null) {
                // NativeImage.read will close the resource for us.
                nativeImage = NativeImage.read(resource);
            } else {
                throw new IOException("Resource was null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ToasterSimulator.err("Couldn't read the texture, texture will be black.");
            nativeImage = new NativeImage(1, 1, true);
        }
        DynamicTexture texture = new DynamicTexture(nativeImage);
        ClientConstants.mc.getTextureManager().register(ClientConstants.textureResource, texture);
    }
}
