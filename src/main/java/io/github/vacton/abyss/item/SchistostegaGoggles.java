package io.github.vacton.abyss.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Night-vision goggles. While worn in the head slot they keep the Night Vision effect running.
 * The head-slot behaviour itself comes from the EQUIPPABLE component set in ModItems.
 */
public class SchistostegaGoggles extends Item {

    /** Above 200 ticks Night Vision does not flicker; refreshed every tick while worn. */
    private static final int EFFECT_TICKS = 220;

    public SchistostegaGoggles(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, EFFECT_TICKS, 0, false, false, false));
        }
    }
}
