package me.spazzylemons.toastersimulator.client.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.spazzylemons.toastersimulator.ToasterSimulator;
import me.spazzylemons.toastersimulator.client.ClientData;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(new StringTextComponent(ToasterSimulator.NAME + " configuration"));
        this.parent = parent;
    }

    @Override
    public void init() {
        int[] i = {0};

        addButton(i, createIsEnabledText(), button -> {
            ClientConfig config = ClientData.getConfig();
            config.setEnabled(!config.isEnabled());
            button.setMessage(createIsEnabledText());
        });

        addButton(i, new StringTextComponent("Reload texture"), button -> {
            ClientData.getConfig().reloadTexture();
        });

        ++i[0]; // section break

        addButton(i, DialogTexts.GUI_DONE, button -> {
            if (minecraft != null) minecraft.setScreen(parent);
        });
    }

    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float partial) {
        renderBackground(matrices);
        drawCenteredString(matrices, font, title, width / 2, 20, 16777215);
        super.render(matrices, mouseX, mouseY, partial);
    }

    private void addButton(int[] i, ITextComponent text, Button.IPressable action) {
        addButton(new Button(
                width / 2 - 100,
                height / 6 + 24 * i[0]++,
                200,
                20,
                text,
                action
        ));
    }

    private static ITextComponent createIsEnabledText() {
        return DialogTexts.optionStatus(
                new StringTextComponent("Enable protogen model"),
                ClientData.getConfig().isEnabled()
        );
    }
}
