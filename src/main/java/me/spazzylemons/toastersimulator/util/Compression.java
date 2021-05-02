package me.spazzylemons.toastersimulator.util;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Compression {
    private Compression() {}

    /**
     * Decompress data of a known decompressed size.
     * @param in The source of the compressed data.
     * @param out The location to write the decompressed data.
     */
    public static void decompress(InputStream in, byte[] out) throws IOException {
        try (GzipCompressorInputStream stream = new GzipCompressorInputStream(in)) {
            int i = 0;
            while (i < out.length) {
                int amt = stream.read(out, i, out.length - i);
                if (amt == -1) throw new IOException("Not enough data given");
                i += amt;
            }
            if (stream.read() != -1) throw new IOException("Too much data given");
        }
    }

    /**
     * Compress data.
     * @param in The location of the decompressed data.
     * @param out The location to write the compressed data.
     */
    public static void compress(byte[] in, OutputStream out) throws IOException {
        try (GzipCompressorOutputStream stream = new GzipCompressorOutputStream(out)) {
            stream.write(in);
        }
    }
}
