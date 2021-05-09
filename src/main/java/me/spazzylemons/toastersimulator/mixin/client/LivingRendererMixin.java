package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.model.OBJLoader;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingRenderer.class)
public class LivingRendererMixin<T extends Entity, M extends EntityModel<T>> {
    @Inject(at = @At("HEAD"), method = "addLayer")
    public void addLayer(LayerRenderer<T, M> renderer, CallbackInfoReturnable<Boolean> cir) {
        if (renderer instanceof BipedArmorLayer<?, ?, ?>) {
            BipedModel<?> innerModel = ((BipedArmorLayerMixin<?, ?>) renderer).getInnerModel();
            BipedModel<?> outerModel = ((BipedArmorLayerMixin<?, ?>) renderer).getOuterModel();
            WeakHashMap<ModelRenderer, QuadModel> modelRenderers = ClientData.getModelRenderers();
            modelRenderers.put(innerModel.leftLeg, OBJLoader.leftLegArmorModel);
            modelRenderers.put(innerModel.rightLeg, OBJLoader.rightLegArmorModel);
            modelRenderers.put(outerModel.leftLeg, OBJLoader.leftBootArmorModel);
            modelRenderers.put(outerModel.rightLeg, OBJLoader.rightBootArmorModel);
        }
    }
}
