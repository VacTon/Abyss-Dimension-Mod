package io.github.vacton.abyss.client.view;
import net.minecraft.network.chat.Component;

public enum BookPage {
    STATS("screen.abyss.book.tab_stats"),
    POWERS("screen.abyss.book.tab_powers");

    private final String translationKey;

    BookPage(String translationKey) {
        this.translationKey = translationKey;
    }
    public Component getLabel(){
        return Component.translatable(this.translationKey);
    }
}
