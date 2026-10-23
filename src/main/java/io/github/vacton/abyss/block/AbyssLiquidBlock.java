package io.github.vacton.abyss.block;

import io.github.vacton.abyss.Abyss;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class AbyssLiquidBlock extends LiquidBlock {

    public static final ResourceKey<DamageType> ABYSS_LIQUID_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "abyss_liquid"));

    public AbyssLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier applier, boolean precise) {
        super.entityInside(state, level, pos, entity, applier, precise);

        if (level instanceof ServerLevel serverLevel) {
            DamageSource source = new DamageSource(
                    serverLevel.registryAccess()
                            .lookupOrThrow(Registries.DAMAGE_TYPE)
                            .getOrThrow(ABYSS_LIQUID_DAMAGE));
            entity.hurtServer(serverLevel, source, 4.0f);
        }

        if (level instanceof ServerLevel serverLevel) {
            // Lava deals 4 damage; invulnerability frames make it land about every 0.5 s.
            entity.hurtServer(serverLevel, level.damageSources().magic(), 4.0f);
        }
    }
}