package com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.config.WorldGenRegistry;

/**
 * Indexes GTCEu ore-vein definitions by their path and top-level directory.
 *
 * <p>
 * The relative selection chance of a vein is calculated using the sum of
 * positive vein weights in the same top-level directory. For example,
 * {@code overworld/copper.json} is evaluated only against other definitions
 * under {@code overworld/}.
 * </p>
 *
 * <p>
 * Biome modifiers and dimension predicates are intentionally not evaluated.
 * The directory structure is treated as the logical dimension group for sieve
 * recipe generation.
 * </p>
 */
public final class VeinGroupIndex {

    private final Map<String, OreDepositDefinition> definitionsByPath;
    private final Map<String, Integer> totalWeightByGroup;

    private VeinGroupIndex(Map<String, OreDepositDefinition> definitionsByPath,
                           Map<String, Integer> totalWeightByGroup) {
        this.definitionsByPath = definitionsByPath;
        this.totalWeightByGroup = totalWeightByGroup;
    }

    /**
     * Builds an index from the currently registered GTCEu ore definitions.
     *
     * <p>
     * Call this only after GTCEu has initialized its world-generation
     * registry.
     * </p>
     */
    public static VeinGroupIndex create() {
        Map<String, OreDepositDefinition> byPath = new LinkedHashMap<>();
        Map<String, Integer> totals = new LinkedHashMap<>();

        for (OreDepositDefinition definition : WorldGenRegistry.getOreDeposits()) {
            if (!definition.isVein() || definition.getWeight() <= 0) {
                continue;
            }

            String path = VeinPathUtils.normalize(definition.getDepositName());
            String group = VeinPathUtils.getTopLevelGroup(path);

            byPath.put(path, definition);
            totals.merge(group, definition.getWeight(), Integer::sum);
        }

        return new VeinGroupIndex(
                Collections.unmodifiableMap(byPath),
                Collections.unmodifiableMap(totals));
    }

    public OreDepositDefinition getDefinition(String normalizedPath) {
        return definitionsByPath.get(normalizedPath);
    }

    public int getTotalWeight(String group) {
        Integer weight = totalWeightByGroup.get(group);
        return weight == null ? 0 : weight;
    }

    /**
     * Returns the configured vein's one-roll relative selection chance within
     * its own top-level path group.
     *
     * @param definition target vein
     * @return a value in the range {@code [0, 1]}, or zero if its group has no
     *         positive total weight
     */
    public double getRelativeChance(OreDepositDefinition definition) {
        String path = VeinPathUtils.normalize(definition.getDepositName());
        String group = VeinPathUtils.getTopLevelGroup(path);
        int totalWeight = getTotalWeight(group);

        return totalWeight <= 0 ? 0.0 : (double) definition.getWeight() / (double) totalWeight;
    }

    public Map<String, Integer> getTotalWeightByGroup() {
        return totalWeightByGroup;
    }
}
