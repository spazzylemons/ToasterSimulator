package me.spazzylemons.toastersimulator.mixin.client;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(BipedArmorLayer.class)
public interface BipedArmorLayerMixin<T extends LivingEntity, A extends BipedModel<T>> {
    @Accessor
    A getInnerModel();

    @Accessor
    A getOuterModel();
}
