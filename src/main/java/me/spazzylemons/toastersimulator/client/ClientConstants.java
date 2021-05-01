package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientConstants {
    private ClientConstants() {}

    public static final ResourceLocation textureResource = new ResourceLocation(Constants.MOD_ID, "proto");
}
