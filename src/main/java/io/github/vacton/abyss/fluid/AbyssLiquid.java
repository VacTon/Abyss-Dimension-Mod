package io.github.vacton.abyss.fluid;

import io.github.vacton.abyss.registry.ModBlocks;
import io.github.vacton.abyss.registry.ModFluids;
import io.github.vacton.abyss.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class AbyssLiquid extends FlowingFluid {

    @Override public Fluid getFlowing() { return ModFluids.FLOWING_ABYSS_LIQUID; }
    @Override public Fluid getSource() { return ModFluids.ABYSS_LIQUID; }
    @Override public Item getBucket() { return ModItems.ABYSS_LIQUID_BUCKET; } // bucket comes later

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.ABYSS_LIQUID || fluid == ModFluids.FLOWING_ABYSS_LIQUID;
    }

    @Override protected boolean canConvertToSource(ServerLevel level) { return false; }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity be = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, be);
    }

    @Override protected int getSlopeFindDistance(LevelReader level) { return 4; }
    @Override protected int getDropOff(LevelReader level) { return 1; }
    @Override public int getTickDelay(LevelReader level) { return 30; } // same as lava in the Overworld

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos,
                                        Fluid fluid, Direction direction) {
        return false;
    }

    @Override protected float getExplosionResistance() { return 100.0f; }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.ABYSS_LIQUID.defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Source extends AbyssLiquid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }

    public static class Flowing extends AbyssLiquid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}