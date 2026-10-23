package io.github.vacton.abyss.registry;

import io.github.vacton.abyss.Abyss;
import io.github.vacton.abyss.block.AbyssBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final Block ABYSS_BLOCK = register(
            "abyss_block",
            AbyssBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(30.0f, 1000.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.MUD));

    private static Block register(String name,
                                  Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(Abyss.MOD_ID, name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(Abyss.MOD_ID, name));

        Block block = factory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void initialize() {
        // Empty on purpose: calling this loads the class, which registers the blocks.
        // Add a creative tab entry here later.
    }
}