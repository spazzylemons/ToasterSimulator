package me.spazzylemons.toastersimulator.client.model.geometry;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Face {
    private final Vertex[] vertices;
    private final Vector3f normal;

    public Face(Vertex a, Vertex b, Vertex c, Vertex d, Vector3f normal) {
        this.vertices = new Vertex[]{a, b, c, d};
        this.normal = normal;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Vector3f getNormal() {
        return normal;
    }
}
