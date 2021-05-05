package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void render(CallbackInfo ci) {
        if (!getClass().isAssignableFrom(PlayerRenderer.class)) {
            ClientData.currentlyRenderingPlayer = null;
        }
    }
}
