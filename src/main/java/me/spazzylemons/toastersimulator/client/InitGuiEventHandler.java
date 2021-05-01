package me.spazzylemons.toastersimulator.client;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.config.ConfigScreen;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class InitGuiEventHandler {
    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        if (!(screen instanceof CustomizeSkinScreen)) return;

        event.addWidget(new Button(
                screen.width / 2 - 100,
                screen.height / 6 + 24 * 5,
                200,
                20,
                new StringTextComponent(Constants.MOD_NAME + " options..."),
                button -> screen.getMinecraft().setScreen(new ConfigScreen(screen))
        ));
    }
}
