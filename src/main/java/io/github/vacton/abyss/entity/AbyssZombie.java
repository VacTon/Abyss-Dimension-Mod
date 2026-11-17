package io.github.vacton.abyss.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

/** A tougher zombie native to the Abyss. Drops Darkness Essence (see its loot table). */
public class AbyssZombie extends Zombie {

    public AbyssZombie(EntityType<? extends AbyssZombie> type, Level level) {
        super(type, level);
    }

    /** Vanilla zombie attributes with more health, applied through the attribute registry. */
    public static AttributeSupplier.Builder createAbyssAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0);
    }
}