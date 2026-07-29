package com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve;

import static com.github.gtexpert.gtmt.integration.exnihilo.items.ExNihiloItems.pebbleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.MetaBlocks;

import com.github.gtexpert.gtmt.api.unification.material.ore.GTMTOrePrefix;
import com.github.gtexpert.gtmt.api.util.ModLog;
import com.github.gtexpert.gtmt.integration.exnihilo.ExNihiloConfigHolder;
import com.github.gtexpert.gtmt.integration.exnihilo.recipes.ExNihiloSieveRecipe;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.blocks.BlockSieve;
import exnihilocreatio.registries.manager.ISieveDefaultRegistryProvider;
import exnihilocreatio.registries.registries.SieveRegistry;
import exnihilocreatio.util.ItemInfo;

public class SieveDrops implements ISieveDefaultRegistryProvider {

    private static Map<SieveDropType, List<SieveDrop>> SIEVE_DROPS_MAP = new HashMap<>();

    /**
     * Reads all configured vein entries and prepares sieve drops.
     *
     * <p>
     * Call this after GTCEu has initialized its world-generation registry
     * and before Ex Nihilo Creatio requests default sieve recipes.
     * </p>
     */
    public static void readSieveDropsFromConfig() {
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.sandSieveDrops, SieveDropType.SAND);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.gravelSieveDrops, SieveDropType.GRAVEL);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.graniteSieveDrops, SieveDropType.GRANITE);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.dioriteSieveDrops, SieveDropType.DIORITE);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.andesiteSieveDrops, SieveDropType.ANDESITE);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.netherrackSieveDrops, SieveDropType.NETHERRACK);
        readSieveDropsFromConfig(ExNihiloConfigHolder.drops.endstoneSieveDrops, SieveDropType.END);
    }

    private static void readSieveDropsFromConfig(String[] recipes, SieveDropType type) {
        if (recipes == null || recipes.length == 0) {
            ModLog.logger.info("No configurations found for {} sieve category, skipping...", type.getName());
            return;
        }

        for (String recipe : recipes) {
            SieveDrop drop = validateInputs(recipe);
            if (drop != null) {
                SIEVE_DROPS_MAP.putIfAbsent(type, new ArrayList<>());
                SIEVE_DROPS_MAP.get(type).add(drop);
            }
        }
    }

    private static SieveDrop validateInputs(String recipe) {
        SieveDrop drop = null;
        String materialName;
        float chance;
        int meshLevel;

        String trimmed = recipe.trim();

        int atIndex = trimmed.lastIndexOf('@');
        int starIndex = trimmed.lastIndexOf('*');

        if (atIndex <= 0 || starIndex <= atIndex + 1 || starIndex >= trimmed.length() - 1) {
            throw new IllegalArgumentException(
                    "Expected format [modid:]material@chance*mesh_level: " + recipe);
        }

        String materialText = trimmed.substring(0, atIndex).trim();
        String chanceText = trimmed.substring(atIndex + 1, starIndex).trim();
        String meshText = trimmed.substring(starIndex + 1).trim();

        if (!materialText.contains(":")) {
            materialName = "gregtech:" + materialText;
        } else {
            materialName = materialText;
        }
        Material material = GregTechAPI.materialManager.getMaterial(materialName);
        if (material == null) {
            ModLog.logger.error("{} is null! Skipped: {} ", materialName, recipe);
            return drop;
        }

        try {
            chance = Float.parseFloat(chanceText);
        } catch (NumberFormatException exception) {
            ModLog.logger.error("Invalid chance in sieve entry. Skipped: {}", recipe);
            return drop;
        }

        if (!Float.isFinite(chance) || chance < 0.0 || chance > 1.0) {
            ModLog.logger.error("Chance must be between 0.0 and 1.0! Skipped: {}", recipe);
            return drop;
        }

        try {
            meshLevel = Integer.parseInt(meshText);
        } catch (NumberFormatException exception) {
            ModLog.logger.error("Invalid mesh level in sieve entry. Skipped: {}", recipe);
            return drop;
        }

        if (meshLevel < 1 || meshLevel > 4) {
            ModLog.logger.error("Mesh level must be between 1 and 4! Skipped: {} ", recipe);
            return drop;
        }

        ModLog.logger.info("Register SieveDropEntry: Material={}, Chance={}, MeshLevel={}", material.getRegistryName(),
                chance, meshLevel);
        drop = new SieveDrop(material, chance, meshLevel);
        return drop;
    }

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
        // Materials
        for (Map.Entry<SieveDropType, List<SieveDrop>> drops : SIEVE_DROPS_MAP.entrySet()) {
            OrePrefix prefix = drops.getKey().getOutputPrefix();
            for (SieveDrop drop : drops.getValue()) {
                ItemStack stack = OreDictUnifier.get(prefix, drop.material);
                if (drops.getKey() != SieveDropType.NETHERRACK && drops.getKey() != SieveDropType.END) {
                    registry.register(drops.getKey().getName(), new ItemInfo(stack.getItem(), stack.getMetadata()),
                            drop.getChance(), drop.getMeshLevel());
                } else {
                    registry.register(
                            new ItemStack(drops.getKey() == SieveDropType.END ? ModBlocks.endstoneCrushed :
                                    ModBlocks.netherrackCrushed),
                            new ItemInfo(stack.getItem(), stack.getMetadata()), drop.getChance(), drop.getMeshLevel());
                }
            }
        }
        SIEVE_DROPS_MAP = null;
        ExNihiloSieveRecipe.register(registry);
    }

    enum SieveDropType implements IStringSerializable {

        SAND("sand", GTMTOrePrefix.oreSandyChunk, null),
        GRAVEL("gravel", GTMTOrePrefix.oreChunk, null),
        GRANITE("crushedGranite", GTMTOrePrefix.oreChunk, null),
        DIORITE("crushedDiorite", GTMTOrePrefix.oreChunk, null),
        ANDESITE("crushedAndesite", GTMTOrePrefix.oreChunk, null),

        NETHERRACK("nether", GTMTOrePrefix.oreNetherChunk,
                new ItemStack(ModBlocks.netherrackCrushed)),
        END("end", GTMTOrePrefix.oreEnderChunk,
                new ItemStack(ModBlocks.endstoneCrushed));

        private final String registryName;
        private final OrePrefix outputPrefix;
        private final ItemStack explicitInput;

        SieveDropType(String registryName, OrePrefix outputPrefix, ItemStack explicitInput) {
            this.registryName = registryName;
            this.outputPrefix = outputPrefix;
            this.explicitInput = explicitInput;
        }

        @Override
        @Nonnull
        public String getName() {
            return registryName;
        }

        public OrePrefix getOutputPrefix() {
            return outputPrefix;
        }

        public boolean hasExplicitInput() {
            return explicitInput != null;
        }

        public ItemStack getExplicitInput() {
            return explicitInput == null ? ItemStack.EMPTY : explicitInput.copy();
        }
    }

    /**
     * Immutable data required to register one Ex Nihilo Creatio sieve drop.
     *
     * <p>
     * The source vein path and group are retained for diagnostics. They are
     * deliberately not part of equality or aggregation logic: drops originating
     * from different vein groups must remain independent registry entries.
     * </p>
     */
    public static final class SieveDrop {

        private final Material material;
        private final float chance;
        private final int meshLevel;

        public SieveDrop(Material material, float chance, int meshLevel) {
            this.material = material;
            this.chance = chance;
            this.meshLevel = meshLevel;
        }

        public Material getMaterial() {
            return material;
        }

        public float getChance() {
            return chance;
        }

        public int getMeshLevel() {
            return meshLevel;
        }
    }
}
