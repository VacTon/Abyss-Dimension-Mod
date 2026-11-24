package io.github.vacton.abyss.client;

import io.github.vacton.abyss.registry.ModFluids;
import io.github.vacton.abyss.client.view.AbyssBookScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.resources.Identifier;

public class AbyssClient implements ClientModInitializer {
    private static boolean openBookScreenNextTick;

    // Implements the client initialization method declared by ClientModInitializer.
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openBookScreenNextTick) {
                openBookScreenNextTick = false;
                client.setScreen(new AbyssBookScreen());
            }
        });
        //abyssui command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("abyssui")
                        .executes(context -> {
                            openBookScreenNextTick = true;
                            return 1;
                        })));

        FluidRenderHandlerRegistry.INSTANCE.register(
                ModFluids.ABYSS_LIQUID,
                ModFluids.FLOWING_ABYSS_LIQUID,
                new SimpleFluidRenderHandler(
                        Identifier.fromNamespaceAndPath("abyss", "block/abyss_liquid_still"),
                        Identifier.fromNamespaceAndPath("abyss", "block/abyss_liquid_flow")));
    }
}
