package me.spazzylemons.protoplayermodels.model.geometry;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import javax.annotation.Nonnull;

public class QuadModel {
    private final Face[] faces;

    public QuadModel(Face[] faces) {
        this.faces = faces;
    }

    public Face[] getFaces() {
        return faces;
    }

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
        matrices.pushPose();
        try {
            MatrixStack.Entry entry = matrices.last();
            Matrix4f pose = entry.pose();
            Matrix3f normal = entry.normal();
            Vector4f pos = new Vector4f();
            for (Face face : faces) {
                 Vector3f n = face.getNormal().copy();
                 n.transform(normal);
                 for (Vertex v : face.getVertices()) {
                     Vector3f p = v.getPosition();
                     Vector2f uv = v.getUV();
                     pos.set(p.x(), p.y(), p.z(), 1.0F);
                     pos.transform(pose);
                     vertices.vertex(
                             pos.x(),
                             pos.y(),
                             pos.z(),
                             r,
                             g,
                             b,
                             a,
                             uv.x,
                             uv.y,
                             overlay,
                             light,
                             n.x(),
                             n.y(),
                             n.z()
                     );
                 }
            }
        } finally {
            matrices.popPose();
        }
    }
}
