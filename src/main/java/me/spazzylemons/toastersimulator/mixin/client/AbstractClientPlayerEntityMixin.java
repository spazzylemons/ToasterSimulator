package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.ClientTextureManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "getSkinTextureLocation", cancellable = true)
    public void getSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        LivingEntity entity = LivingEntity.class.cast(this);
        UUID playerId = entity.getUUID();
        ResourceLocation texture = ClientTextureManager.get(playerId);
        if (texture != null) {
            cir.setReturnValue(texture);
        }
    }
}
