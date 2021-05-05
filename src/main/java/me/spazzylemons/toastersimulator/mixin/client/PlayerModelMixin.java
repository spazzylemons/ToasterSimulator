package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.model.OBJLoader;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Shadow @Final public ModelRenderer leftSleeve;

    @Shadow @Final public ModelRenderer rightSleeve;

    @Shadow @Final public ModelRenderer jacket;

    @Shadow @Final public ModelRenderer leftPants;

    @Shadow @Final public ModelRenderer rightPants;

    @Inject(at = @At("RETURN"), method = "<init>")
    public void constructor(CallbackInfo ci) {
        BipedModel<?> biped = BipedModel.class.cast(this);
        WeakHashMap<ModelRenderer, QuadModel> modelRenderers = ClientData.getModelRenderers();
        modelRenderers.put(biped.head, OBJLoader.headModel);
        modelRenderers.put(biped.leftArm, OBJLoader.leftArmModel);
        modelRenderers.put(biped.rightArm, OBJLoader.rightArmModel);
        modelRenderers.put(biped.body, OBJLoader.bodyModel);
        modelRenderers.put(biped.leftLeg, OBJLoader.leftLegModel);
        modelRenderers.put(biped.rightLeg, OBJLoader.rightLegModel);
        modelRenderers.put(biped.hat, OBJLoader.emptyModel);
        modelRenderers.put(leftSleeve, OBJLoader.emptyModel);
        modelRenderers.put(rightSleeve, OBJLoader.emptyModel);
        modelRenderers.put(jacket, OBJLoader.emptyModel);
        modelRenderers.put(leftPants, OBJLoader.emptyModel);
        modelRenderers.put(rightPants, OBJLoader.emptyModel);
    }
}
