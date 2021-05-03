package me.spazzylemons.toastersimulator.mixin.client;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Button.class)
public class SomeMixin {
    @Inject(at = @At("HEAD"), method = "onPress")
    public void pleaseWork(CallbackInfo ci) {
        System.out.println("hey is this thing working????");
        throw new Error("hey is this thing working");
    }
}
