package com.github.gtexpert.gtmt.integration.exnihilo.recipes;

import static com.github.gtexpert.gtmt.integration.exnihilo.items.ExNihiloItems.pebbleItem;

import org.jetbrains.annotations.NotNull;

import gregtech.common.blocks.MetaBlocks;

import exnihilocreatio.blocks.BlockSieve;
import exnihilocreatio.registries.manager.ISieveDefaultRegistryProvider;
import exnihilocreatio.registries.registries.SieveRegistry;
import exnihilocreatio.util.ItemInfo;

public class SieveDrops implements ISieveDefaultRegistryProvider {

    @Override
    public void registerRecipeDefaults(@NotNull SieveRegistry registry) {
        // pebbles
        registry.register("dirt", new ItemInfo(pebbleItem), 0.3f, BlockSieve.MeshType.STRING.getID());
        registry.register("dirt", new ItemInfo(pebbleItem, 1), 0.3f, BlockSieve.MeshType.STRING.getID());
        registry.register("dirt", new ItemInfo(pebbleItem, 2), 0.3f, BlockSieve.MeshType.STRING.getID());
        registry.register("dirt", new ItemInfo(pebbleItem, 3), 0.3f, BlockSieve.MeshType.STRING.getID());
        // saplings
        registry.register("dirt", new ItemInfo(MetaBlocks.RUBBER_SAPLING.getBlockState().getBlock()), 0.3f,
                BlockSieve.MeshType.STRING.getID());
    }
}
