package me.spazzylemons.toastersimulator.mixin.client;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingRenderer.class)
public interface ILivingRendererModelShadowMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Accessor
    M getModel();
}
