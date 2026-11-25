package io.github.vacton.abyss.client.view;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

public class AbyssBookScreen extends Screen {
    private static final int BACKGROUND_COLOR = 0xF0101010;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int PLACEHOLDER_COLOR = 0xFFAAAAAA;
    private static final int TAB_SELECTED_COLOR = 0xFFFFCC66;
    private static final int PAGE_GROUP_HEIGHT = 104;
    private static final int TAB_TOP_OFFSET = 20;
    private static final int CONTENT_TOP_OFFSET = 60;

    private final Map<BookPage, Button> pageTabs = new EnumMap<>(BookPage.class);
    private BookPage selectedPage = BookPage.STATS;
    private Button depositButton;
    private Component statusMessage;

    public AbyssBookScreen() {
        super(Component.translatable("screen.abyss.book.title"));
    }

    // Overrides Screen.init to create this screen's tabs and placeholder button.
    @Override
    protected void init() {
        pageTabs.clear();
        int tabWidth = Math.clamp((this.width - 28) / BookPage.values().length, 1, 90);
        int tabGap = 4;
        int tabsWidth = tabWidth * BookPage.values().length + tabGap * (BookPage.values().length - 1);
        int startX = (this.width - tabsWidth) / 2;
        int groupTop = getPageGroupTop();
        int tabY = groupTop + TAB_TOP_OFFSET;

        for (int i = 0; i < BookPage.values().length; i++) {
            BookPage page = BookPage.values()[i];
            int x = startX + i * (tabWidth + tabGap);
            Button tab = Button.builder(page.getLabel(), button -> selectPage(page))
                    .bounds(x, tabY, tabWidth, 20)
                    .build();
            pageTabs.put(page, tab);
            this.addRenderableWidget(tab);
        }

        depositButton = Button.builder(
                        Component.translatable("screen.abyss.book.deposit.button"),
                        button -> statusMessage = Component.translatable("screen.abyss.book.deposit.placeholder"))
                .bounds(this.width / 2 - 60, groupTop + CONTENT_TOP_OFFSET + 18, 120, 20)
                .build();
        depositButton.visible = selectedPage == BookPage.DEPOSIT;
        this.addRenderableWidget(depositButton);

        statusMessage = Component.translatable("screen.abyss.book.status.preview");
        updateTabLabels();
    }

    private void selectPage(BookPage page) {
        selectedPage = page;
        depositButton.visible = selectedPage == BookPage.DEPOSIT;
        statusMessage = Component.translatable("screen.abyss.book.status.preview");
        updateTabLabels();
    }

    private void updateTabLabels() {
        pageTabs.forEach((page, button) -> button.setMessage(
                page == selectedPage
                        ? Component.literal("[").append(page.getLabel()).append("]")
                        : page.getLabel()));
    }

    private int getPageGroupTop() {
        return (this.height - PAGE_GROUP_HEIGHT) / 2;
    }

    // Overrides Screen.render so this screen can draw its own background and text.
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 16, TITLE_COLOR);
        guiGraphics.drawCenteredString(this.font, selectedPage.getLabel(),
                this.width / 2, getPageGroupTop(), TAB_SELECTED_COLOR);

        switch (selectedPage) {
            case STATS -> renderStats(guiGraphics);
            case POWERS -> renderPowers(guiGraphics);
            case DEPOSIT -> renderDeposit(guiGraphics);
        }

        guiGraphics.drawCenteredString(this.font, statusMessage,
                this.width / 2, this.height - 16, PLACEHOLDER_COLOR);
    }

    private void renderStats(GuiGraphics guiGraphics) {
        int contentTop = getPageGroupTop() + CONTENT_TOP_OFFSET;
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.stats.essence", contentTop);
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.stats.kills", contentTop + 16);
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.stats.time", contentTop + 32);
    }

    private void renderPowers(GuiGraphics guiGraphics) {
        int contentTop = getPageGroupTop() + CONTENT_TOP_OFFSET;
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.powers.summon", contentTop);
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.powers.explosion", contentTop + 16);
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.powers.haste", contentTop + 32);
    }

    private void renderDeposit(GuiGraphics guiGraphics) {
        drawPlaceholderLine(guiGraphics, "screen.abyss.book.deposit.description",
                getPageGroupTop() + CONTENT_TOP_OFFSET);
    }

    private void drawPlaceholderLine(GuiGraphics guiGraphics, String translationKey, int y) {
        guiGraphics.drawCenteredString(this.font, Component.translatable(translationKey),
                this.width / 2, y, PLACEHOLDER_COLOR);
    }

}
