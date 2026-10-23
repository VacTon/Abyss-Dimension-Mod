package io.github.vacton.abyss;

import io.github.vacton.abyss.registry.ModBlocks;
import io.github.vacton.abyss.registry.ModFluids;
import io.github.vacton.abyss.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class Abyss implements ModInitializer {

    public static final String MOD_ID = "abyss";

    @Override
    public void onInitialize() {
        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.ABYSS_BLOCK));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(ModItems.ABYSS_LIQUID_BUCKET));
    }
}