package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.ClientData;
import me.spazzylemons.toastersimulator.client.model.OBJLoader;
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
        ClientData.modelRenderers.put(biped.head, OBJLoader.headModel);
        ClientData.modelRenderers.put(biped.leftArm, OBJLoader.leftArmModel);
        ClientData.modelRenderers.put(biped.rightArm, OBJLoader.rightArmModel);
        ClientData.modelRenderers.put(biped.body, OBJLoader.bodyModel);
        ClientData.modelRenderers.put(biped.leftLeg, OBJLoader.leftLegModel);
        ClientData.modelRenderers.put(biped.rightLeg, OBJLoader.rightLegModel);
        ClientData.modelRenderers.put(biped.hat, OBJLoader.emptyModel);
        ClientData.modelRenderers.put(leftSleeve, OBJLoader.emptyModel);
        ClientData.modelRenderers.put(rightSleeve, OBJLoader.emptyModel);
        ClientData.modelRenderers.put(jacket, OBJLoader.emptyModel);
        ClientData.modelRenderers.put(leftPants, OBJLoader.emptyModel);
        ClientData.modelRenderers.put(rightPants, OBJLoader.emptyModel);
    }
}
