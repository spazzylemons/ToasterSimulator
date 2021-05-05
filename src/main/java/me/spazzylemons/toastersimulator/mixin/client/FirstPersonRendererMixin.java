package me.spazzylemons.toastersimulator.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(FirstPersonRenderer.class)
public class FirstPersonRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderArmWithItem")
    public void renderArmWithItem(AbstractClientPlayerEntity player, float p_228405_2_, float p_228405_3_, Hand p_228405_4_, float p_228405_5_, ItemStack p_228405_6_, float p_228405_7_, MatrixStack p_228405_8_, IRenderTypeBuffer p_228405_9_, int p_228405_10_, CallbackInfo ci) {
        ClientData.currentlyRenderingPlayer = player.getUUID();
    }
}
