package io.github.vacton.abyss.registry;

import io.github.vacton.abyss.Abyss;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItems {

    public static final Item ABYSS_LIQUID_BUCKET = register("abyss_liquid_bucket",
            key -> new BucketItem(ModFluids.ABYSS_LIQUID,
                    new Item.Properties()
                            .setId(key)
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));

    private static Item register(String name,
                                 java.util.function.Function<ResourceKey<Item>, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(Abyss.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(key));
    }

    public static void initialize() {}
}