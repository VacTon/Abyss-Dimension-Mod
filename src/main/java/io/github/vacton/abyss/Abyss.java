package io.github.vacton.abyss;

import io.github.vacton.abyss.entity.AbyssZombie;
import io.github.vacton.abyss.registry.ModBlocks;
import io.github.vacton.abyss.registry.ModEntities;
import io.github.vacton.abyss.registry.ModFluids;
import io.github.vacton.abyss.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class Abyss implements ModInitializer {

    public static final String MOD_ID = "abyss";

    @Override
    public void onInitialize() {
        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();
        ModEntities.initialize();
        FabricDefaultAttributeRegistry.register(ModEntities.ABYSS_ZOMBIE, AbyssZombie.createAbyssAttributes());
        SpawnPlacements.register(ModEntities.ABYSS_ZOMBIE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);


        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.ABYSS_BLOCK));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(ModItems.ABYSS_LIQUID_BUCKET));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.SCHISTOSTEGA));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> {
                    entries.accept(ModItems.DARKNESS_ESSENCE);
                    entries.accept(ModItems.SCHISTOSTEGA_LENS);
                });

        // 0-1 Darkness Essence in End City chests.
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && BuiltInLootTables.END_CITY_TREASURE.equals(key)) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(LootItem.lootTableItem(ModItems.DARKNESS_ESSENCE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 1.0f)))));
            }
        });
    }
}