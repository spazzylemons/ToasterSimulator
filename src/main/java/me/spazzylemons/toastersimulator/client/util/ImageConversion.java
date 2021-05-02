package me.spazzylemons.toastersimulator.client.util;

import me.spazzylemons.toastersimulator.util.Exceptions;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public final class ImageConversion {
    private ImageConversion() {}

    public static void imageToBuffer(byte[] out, NativeImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        assert out.length == w * h * 4;
        IntBuffer wrapper = ByteBuffer.wrap(out).asIntBuffer();
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                wrapper.put(image.getPixelRGBA(x, y));
            }
        }
    }

    public static NativeImage bufferToImage(int w, int h, byte[] buffer) {
        assert buffer.length == w * h * 4;
        NativeImage result = new NativeImage(NativeImage.PixelFormat.RGBA, w, h, false);
        Exceptions.wrapChecked(() -> Exceptions.closeOnFailure(result, () -> {
            IntBuffer wrapper = ByteBuffer.wrap(buffer).asIntBuffer();
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    result.setPixelRGBA(x, y, wrapper.get());
                }
            }
        }));
        return result;
    }
}
