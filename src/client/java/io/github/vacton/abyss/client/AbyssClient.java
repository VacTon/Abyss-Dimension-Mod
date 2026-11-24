package io.github.vacton.abyss.client;

import io.github.vacton.abyss.Abyss;
import io.github.vacton.abyss.client.view.GogglesOverlay;
import io.github.vacton.abyss.registry.ModBlocks;
import io.github.vacton.abyss.registry.ModEntities;
import io.github.vacton.abyss.registry.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
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

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "goggles_overlay"),
                new GogglesOverlay());

        BlockRenderLayerMap.putBlock(ModBlocks.SCHISTOSTEGA, ChunkSectionLayer.CUTOUT);

        EntityRendererRegistry.register(ModEntities.ABYSS_ZOMBIE, AbyssZombieRenderer::new);
    }
}
