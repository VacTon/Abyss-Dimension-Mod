package io.github.vacton.abyss.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

/**
 * Glowing moss that grows on the faces of other blocks, like vanilla glow lichen.
 * MultifaceSpreadeableBlock is a MultifaceBlock that requires a spreader.
 */
public class SchistostegaBlock extends MultifaceSpreadeableBlock {

    public static final MapCodec<SchistostegaBlock> CODEC = simpleCodec(SchistostegaBlock::new);

    public static final int LIGHT_LEVEL = 7;

    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public SchistostegaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<SchistostegaBlock> codec() {
        return CODEC;
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return spreader;
    }

    /** Light is LIGHT_LEVEL when at least one face has moss, 0 otherwise (same rule as glow lichen). */
    public static ToIntFunction<BlockState> emission() {
        return GlowLichenBlock.emission(LIGHT_LEVEL);
    }
}