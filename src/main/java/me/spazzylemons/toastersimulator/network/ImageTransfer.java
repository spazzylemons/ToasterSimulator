package me.spazzylemons.toastersimulator.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.zip.Deflater;

public final class ImageTransfer {
    private ImageTransfer() {}

    public static final int IMAGE_BYTE_SIZE = 64 * 64 * 4;

    public static void readCompressed(byte[] data, PacketBuffer in) throws IOException {
        ByteBufInputStream bufStream = new ByteBufInputStream(in);
        try (GzipCompressorInputStream stream = new GzipCompressorInputStream(bufStream)) {
            int i = 0;
            while (i < data.length) {
                i += stream.read(data, i, data.length - i);
            }
        }
    }

    private static final GzipParameters compressSettings = new GzipParameters();

    static {
        compressSettings.setCompressionLevel(Deflater.BEST_COMPRESSION);
    }

    public static void writeCompressed(byte[] data, PacketBuffer out) throws IOException {
        ByteBufOutputStream bufStream = new ByteBufOutputStream(out);
        try (GzipCompressorOutputStream stream = new GzipCompressorOutputStream(bufStream, compressSettings)) {
            stream.write(data);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static byte[] imageToBuffer(NativeImage image) {
        assert image.getWidth() == 64;
        assert image.getHeight() == 64;
        byte[] result = new byte[IMAGE_BYTE_SIZE];
        IntBuffer wrapper = ByteBuffer.wrap(result).asIntBuffer();
        for (int y = 0; y < 64; ++y) {
            for (int x = 0; x < 64; ++x) {
                wrapper.put(image.getPixelRGBA(x, y));
            }
        }
        return result;
    }

    @OnlyIn(Dist.CLIENT)
    public static NativeImage bufferToImage(byte[] buffer) {
        NativeImage result = new NativeImage(NativeImage.PixelFormat.RGBA, 64, 64, false);
        boolean failure = true;
        try {
            IntBuffer wrapper = ByteBuffer.wrap(buffer).asIntBuffer();
            for (int y = 0; y < 64; ++y) {
                for (int x = 0; x < 64; ++x) {
                    result.setPixelRGBA(x, y, wrapper.get());
                }
            }
            failure = false;
            return result;
        } finally {
            if (failure) result.close();
        }
    }
}
