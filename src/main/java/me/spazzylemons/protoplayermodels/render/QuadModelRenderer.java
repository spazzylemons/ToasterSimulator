package me.spazzylemons.protoplayermodels.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.spazzylemons.protoplayermodels.model.geometry.QuadModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class QuadModelRenderer extends ModelRenderer {
    private final QuadModel quadModel;

    public QuadModelRenderer(Model model, QuadModel quadModel) {
        super(model);
        this.quadModel = quadModel;
    }

    @Override
    public void render(
            @Nonnull MatrixStack matrices,
            @Nonnull IVertexBuilder vertices,
            int light,
            int overlay,
            float r,
            float g,
            float b,
            float a
    ) {
        super.render(matrices, vertices, light, overlay, r, g, b, a);
        if (visible) {
            matrices.pushPose();
            try {
                translateAndRotate(matrices);
                quadModel.render(matrices, vertices, light, overlay, r, g, b, a);
            } finally {
                matrices.popPose();
            }
        }
    }
}
