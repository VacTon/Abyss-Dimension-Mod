package io.github.vacton.abyss.registry;

import io.github.vacton.abyss.Abyss;
import io.github.vacton.abyss.fluid.AbyssLiquid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FlowingFluid;

public class ModFluids {

    public static final FlowingFluid ABYSS_LIQUID = Registry.register(
            BuiltInRegistries.FLUID,
            Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "abyss_liquid"),
            new AbyssLiquid.Source());

    public static final FlowingFluid FLOWING_ABYSS_LIQUID = Registry.register(
            BuiltInRegistries.FLUID,
            Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "flowing_abyss_liquid"),
            new AbyssLiquid.Flowing());

    public static void initialize() {}
}