package io.github.vacton.abyss.client;

import io.github.vacton.abyss.registry.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.resources.Identifier;

public class AbyssClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(
                ModFluids.ABYSS_LIQUID,
                ModFluids.FLOWING_ABYSS_LIQUID,
                new SimpleFluidRenderHandler(
                        Identifier.fromNamespaceAndPath("abyss", "block/abyss_liquid_still"),
                        Identifier.fromNamespaceAndPath("abyss", "block/abyss_liquid_flow")));
    }
}
