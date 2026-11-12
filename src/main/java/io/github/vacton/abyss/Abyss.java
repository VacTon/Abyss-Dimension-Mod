package io.github.vacton.abyss;

import database.SQLiteTest;
import io.github.vacton.abyss.registry.ModBlocks;
import io.github.vacton.abyss.registry.ModFluids;
import io.github.vacton.abyss.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.SQLException;

public class Abyss implements ModInitializer {

    public static final String MOD_ID = "abyss";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();

        //Test SQLite here, using SLF4J logger instead of System.out, System.err, or printStackTrace()
        try {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            SQLiteTest.testSQLite(gameDir);
            LOGGER.info("SQLite integration test passed");
        } catch (SQLException e) {
            LOGGER.error("SQLite integration test failed", e);
        }

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.ABYSS_BLOCK));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(ModItems.ABYSS_LIQUID_BUCKET));
    }
}