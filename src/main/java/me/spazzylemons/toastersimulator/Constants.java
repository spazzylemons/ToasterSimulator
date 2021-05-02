package me.spazzylemons.toastersimulator;

import net.minecraft.util.ResourceLocation;

public final class Constants {
    private Constants() {}

    public static final String MOD_NAME = "Toaster Simulator";
    public static final String MOD_ID = "toastersimulator";

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;
    public static final int TEXTURE_BYTE_SIZE = TEXTURE_WIDTH * TEXTURE_HEIGHT * Integer.BYTES;

    // not using the mod id for the channel id, because a long channel id causes issues with older servers
    public static final ResourceLocation channelId = new ResourceLocation("toaster", "net");
    public static final String PROTOCOL_VERSION = "1";
}
