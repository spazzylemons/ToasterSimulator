package me.spazzylemons.toastersimulator.client.model.geometry;

import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vertex {
    private final Vector3f position;
    private final Vector2f uv;

    public Vertex(Vector3f position, Vector2f uv) {
        this.position = position;
        this.uv = uv;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getUV() {
        return uv;
    }
}
