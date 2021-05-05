package me.spazzylemons.toastersimulator.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void render(
            AbstractClientPlayerEntity player,
            float unknown,
            float partialTick,
            MatrixStack matrices,
            IRenderTypeBuffer buffers,
            int light,
            CallbackInfo ci
    ) {
        ClientData.currentlyRenderingPlayer = player.getUUID();
    }
}
