package com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gregtech.api.unification.material.Material;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.config.WorldGenRegistry;

import com.github.gtexpert.gtmt.api.util.ModLog;

/**
 * Produces a diagnostic report of GTCEu vein and ore-material probabilities.
 *
 * <p>
 * The report groups vein definitions by the first directory below
 * {@code config/gregtech/worldgen/vein/}. It is intended only as a reference
 * for configuring Ex Nihilo sieve-drop probabilities.
 * </p>
 *
 * <p>
 * This class must be called after GTCEu has initialized its world-generation
 * definitions, normally during {@code FMLPostInitializationEvent}.
 * </p>
 */
public final class VeinProbabilityReporter {

    private VeinProbabilityReporter() {}

    /**
     * Writes all available vein probabilities to the specified logger.
     */
    public static void output() {
        VeinGroupIndex index = VeinGroupIndex.create();

        List<OreDepositDefinition> definitions = new ArrayList<>(WorldGenRegistry.getOreDeposits());

        definitions.sort(
                Comparator.comparing(definition -> VeinPathUtils.normalize(
                        definition.getDepositName())));

        ModLog.logger.info("========== GTCEu Vein Probability Report ==========");

        for (Map.Entry<String, Integer> group : index.getTotalWeightByGroup().entrySet()) {

            ModLog.logger.info(
                    "Group '{}': total weight = {}",
                    group.getKey(),
                    group.getValue());
        }

        String previousGroup = null;

        for (OreDepositDefinition definition : definitions) {
            if (!definition.isVein() ||
                    definition.getWeight() <= 0) {
                continue;
            }

            String path = VeinPathUtils.normalize(
                    definition.getDepositName());

            String group = VeinPathUtils.getTopLevelGroup(path);

            if (!group.equals(previousGroup)) {
                ModLog.logger.info("");
                ModLog.logger.info("--- Group: {} ---", group);
                previousGroup = group;
            }

            double veinChance = index.getRelativeChance(definition);

            ModLog.logger.info(
                    "Vein: {} | weight: {}",
                    path,
                    definition.getWeight());

            Map<Material, Double> materialRatios = LayeredVeinMaterialDistribution.calculate(
                    definition);

            if (materialRatios.isEmpty()) {
                ModLog.logger.info(
                        "  Material distribution unavailable " +
                                "(unsupported or unresolved filler)");
                continue;
            }

            for (Map.Entry<Material, Double> material : materialRatios.entrySet()) {

                double finalChance = veinChance * material.getValue();

                ModLog.logger.info(
                        "  Material: {} | chance: {}",
                        material.getKey().getRegistryName(),
                        formatPercent(finalChance));
            }
        }

        ModLog.logger.info("====================================================");
    }

    private static String formatPercent(double probability) {
        return String.format(
                java.util.Locale.ROOT,
                "%.4f",
                probability);
    }
}
