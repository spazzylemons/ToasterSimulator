package me.spazzylemons.toastersimulator.mixin.client;

import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.config.ConfigScreen;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@OnlyIn(Dist.CLIENT)
@Mixin(CustomizeSkinScreen.class)
public class CustomizeSkinScreenMixin extends Screen {
    public CustomizeSkinScreenMixin() {
        super(StringTextComponent.EMPTY);
    }

    @Inject(at = @At("RETURN"), method = "init", locals = LocalCapture.CAPTURE_FAILHARD)
    public void init(CallbackInfo ci, int i) {
        i += 2;
        this.addButton(new Button(
                width / 2 - 100,
                this.height / 6 + 24 * (i >> 1),
                200,
                20,
                new StringTextComponent(ToasterSimulator.NAME + " options..."),
                button -> getMinecraft().setScreen(new ConfigScreen(this))
        ));
    }
}
