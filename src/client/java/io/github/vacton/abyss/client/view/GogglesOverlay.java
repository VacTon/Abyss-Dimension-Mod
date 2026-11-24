package io.github.vacton.abyss.client.view;

import io.github.vacton.abyss.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;

/** View: a faint green tint over the whole screen while the Schistostega Goggles are worn. */
public class GogglesOverlay implements HudElement {

    private static final int TINT = 0x5500D050; // ARGB, low alpha

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (mc.player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SCHISTOSTEGA_GOGGLES)) {
            graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), TINT);
        }
    }
}
