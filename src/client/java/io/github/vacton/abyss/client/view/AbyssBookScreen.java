package io.github.vacton.abyss.client.view;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AbyssBookScreen extends Screen {
    //color is ARGB format
    private static final int BACKGROUND_COLOR = 0xF0101010;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int PLACEHOLDER_COLOR = 0xFFAAAAAA;

    public AbyssBookScreen() {
        super(Component.translatable("screen.abyss.book.title"));
    }

    // Overrides Screen.render so this screen can draw its own background and text.
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 12, TITLE_COLOR);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("screen.abyss.book.placeholder"),
                this.width / 2, this.height / 2 + 4, PLACEHOLDER_COLOR);
    }
}
