package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.client.render.ModelSavestate;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public class SomeMixin {
    private ModelSavestate state;

    @Inject(at = @At("HEAD"), method = "render")
    @SuppressWarnings("unchecked cast") // TODO remove
    public void renderHead(CallbackInfo ci) {
        PlayerModel<AbstractClientPlayerEntity> model =
                ((ILivingRendererModelShadowMixin<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>) this).getModel();
        state = new ModelSavestate(model);
        try {
            state.applyCustom();
        } catch (Exception e) {
            state.restore();
            throw e;
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void renderTail(CallbackInfo ci) {
        state.restore();
    }
}
