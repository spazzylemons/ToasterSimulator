package me.spazzylemons.toastersimulator.client.render;

import me.spazzylemons.toastersimulator.client.ClientConstants;
import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.model.ProtogenPlayerModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ProtogenPlayerRenderer extends PlayerRenderer {
    public ProtogenPlayerRenderer(EntityRendererManager manager) {
        super(manager);
        model = new ProtogenPlayerModel<>(0F);
    }

    @Override
    public @Nonnull ResourceLocation getTextureLocation(@Nonnull AbstractClientPlayerEntity entity) {
        if (ClientData.isModSupportedByServer()) {
            return ClientData.getTextureLocation(entity.getUUID());
        } else {
            return ClientConstants.localTextureResource;
        }
    }
}
