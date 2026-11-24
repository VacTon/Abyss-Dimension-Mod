package io.github.vacton.abyss.mixin.client;

import io.github.vacton.abyss.fluid.AbyssLiquid;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFogEnvironment.class)
public class LavaFogEnvironmentMixin {

    @Inject(method = "getBaseColor", at = @At("HEAD"), cancellable = true)
    private void abyss$darkFog(ClientLevel level, Camera camera, int i, float f,
                               CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = BlockPos.containing(camera.entity().getEyePosition());
        FluidState fluid = level.getFluidState(pos);
        if (fluid.getType() instanceof AbyssLiquid) {
            cir.setReturnValue(0xFF05050F); // near-black, matches the biome fog
        }
    }
}