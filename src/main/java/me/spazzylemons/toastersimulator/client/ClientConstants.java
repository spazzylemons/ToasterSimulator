package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public final class ClientConstants {
    private ClientConstants() {}

    public static final ResourceLocation localTextureResource = new ResourceLocation(Constants.MOD_ID, "texture-local");
    public static final File textureFile = FMLPaths.CONFIGDIR.get().resolve(Constants.MOD_ID + "-texture.png").toFile();

    public static final Minecraft mc = Minecraft.getInstance();
}
