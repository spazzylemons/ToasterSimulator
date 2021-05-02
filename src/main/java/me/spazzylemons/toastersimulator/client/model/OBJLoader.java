package me.spazzylemons.toastersimulator.client.model;

import me.spazzylemons.toastersimulator.client.model.geometry.Face;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import me.spazzylemons.toastersimulator.client.model.geometry.Vertex;
import me.spazzylemons.toastersimulator.util.Exceptions;
import me.spazzylemons.toastersimulator.util.Upvalue;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public final class OBJLoader {
    private OBJLoader() {}

    public static QuadModel load(Reader data) throws IOException {
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector2f> uvs = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();

        BufferedReader buf = new BufferedReader(data);
        for (;;) {
            String line = buf.readLine();
            if (line == null) break;
            Iterator<Float> floatIterator = Arrays.stream(line.split(" ")).skip(1).map(Float::parseFloat).iterator();
            if (line.startsWith("v ")) {
                // flip some coordinates?
                vertices.add(new Vector3f(floatIterator.next(), -floatIterator.next(), -floatIterator.next()));
            } else if (line.startsWith("vt ")) {
                // flip v coordinates
                uvs.add(new Vector2f(floatIterator.next(), -floatIterator.next()));
            } else if (line.startsWith("vn ")) {
                // flip some coordinates?
                normals.add(new Vector3f(floatIterator.next(), -floatIterator.next(), -floatIterator.next()));
            } else if (line.startsWith("f ")) {
                Vector3f normal = new Vector3f();
                Iterator<Vertex> v = Arrays.stream(line.split(" ")).skip(1).map(it -> {
                    Iterator<Integer> components = Arrays.stream(it.split("/"))
                        .map(i -> Integer.parseInt(i) - 1)
                        .iterator();
                    int vi = components.next();
                    int uvi = components.next();
                    int ni = components.next();
                    normal.add(normals.get(ni));
                    return new Vertex(vertices.get(vi), uvs.get(uvi));
                }).iterator();
                Vertex[] va = new Vertex[4];
                for (int i = 0; i < 4; ++i) va[i] = v.next();
                normal.normalize();
                faces.add(new Face(va[0], va[1], va[2], va[3], normal));
            }
        }
        return new QuadModel(faces.toArray(new Face[0]));
    }

    public static QuadModel load(String resourceName) throws IOException {
        try (InputStream stream = OBJLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (stream == null) throw new IOException("Resource " + resourceName + " not found");
            return load(new InputStreamReader(stream));
        }
    }

    public static QuadModel loadOrEmpty(String resourceName) {
        Upvalue<QuadModel> result = new Upvalue<>();
        Exceptions.wrapChecked(() -> {
            result.set(load(resourceName));
        });
        return result.get();
    }
}
