package me.spazzylemons.toastersimulator.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mixin(ModelRenderer.class)
public abstract class ModelRendererMixin {
    @Shadow public boolean visible;

    @Shadow public abstract void translateAndRotate(MatrixStack p_228307_1_);

    @Inject(
            at = @At("HEAD"),
            method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V",
            cancellable = true
    )
    public void render(
            @Nonnull MatrixStack matrices,
            @Nonnull IVertexBuilder vertices,
            int light,
            int overlay,
            float r,
            float g,
            float b,
            float a,
            CallbackInfo ci
    ) {
        if (visible && ClientTextureManager.get(ClientData.getCurrentlyRenderingPlayerId()) != null) {
            QuadModel model = ClientData.getModelRenderers().get(ModelRenderer.class.cast(this));
            if (model != null) {
                matrices.pushPose();
                try {
                    translateAndRotate(matrices);
                    model.render(matrices, vertices, light, overlay, r, g, b, a);
                } finally {
                    matrices.popPose();
                    ci.cancel();
                }
            }
        }
    }
}
