package me.spazzylemons.toastersimulator.client.event;

import me.spazzylemons.toastersimulator.Constants;
import me.spazzylemons.toastersimulator.client.config.ConfigScreen;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class InitGuiEventHandler {
    private static final Field buttonsField =
            ObfuscationReflectionHelper.findField(Screen.class, "field_230710_m_");

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        if (!(screen instanceof CustomizeSkinScreen)) return;

        int widgetYPosition = screen.height / 6 + 120;
        try {
            // find lowest button and situate our button below it, hoping no mod puts a button at the very bottom
            // if this fails we'll default to putting it below where the done button should be
            // this check is useful for dynamic compatibility with other mods adding buttons here, e.g. optifine
            List<?> buttons = (List<?>) buttonsField.get(screen);
            Widget lowest = (Widget) buttons
                    .stream() // stream as to not mutate the original list
                    .sorted(Comparator.comparingInt(b -> ((Widget) b).y + ((Widget) b).getHeight())) // sort by bottom edge
                    .reduce((a, b) -> b) // take last
                    .orElse(null); // convert optional none to null
            if (lowest != null) {
                widgetYPosition = lowest.y + lowest.getHeight() + 4;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        event.addWidget(new Button(
                screen.width / 2 - 100,
                widgetYPosition,
                200,
                20,
                new StringTextComponent(Constants.MOD_NAME + " options..."),
                button -> screen.getMinecraft().setScreen(new ConfigScreen(screen))
        ));
    }
}
