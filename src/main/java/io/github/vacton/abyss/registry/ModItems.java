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
import io.github.vacton.abyss.item.SchistostegaGoggles;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.sounds.SoundEvents;

public class ModItems {

    public static final Item ABYSS_LIQUID_BUCKET = register("abyss_liquid_bucket",
            key -> new BucketItem(ModFluids.ABYSS_LIQUID,
                    new Item.Properties()
                            .setId(key)
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));

    /** Dropped by AbyssZombie; deposited into the Opus Tenebrarum; centre of the lens recipe. */
    public static final Item DARKNESS_ESSENCE = register("darkness_essence",
            key -> new Item(new Item.Properties().setId(key)));

    public static final Item SCHISTOSTEGA_LENS = register("schistostega_lens",
            key -> new Item(new Item.Properties().setId(key)));

    public static final Item SCHISTOSTEGA_GOGGLES = register("schistostega_goggles",
            key -> new SchistostegaGoggles(new Item.Properties()
                    .setId(key)
                    .stacksTo(1)
                    .component(DataComponents.EQUIPPABLE,
                            Equippable.builder(EquipmentSlot.HEAD)
                                    .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                                    .setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID,
                                            Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "schistostega_goggles")))
                                    .build())));

    private static Item register(String name,
                                 java.util.function.Function<ResourceKey<Item>, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(Abyss.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(key));
    }

    public static void initialize() {}
}