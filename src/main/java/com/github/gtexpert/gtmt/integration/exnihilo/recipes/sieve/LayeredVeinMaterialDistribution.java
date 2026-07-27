package com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.worldgen.config.FillerConfigUtils;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.filler.FillerEntry;
import gregtech.api.worldgen.filler.LayeredBlockFiller;

/**
 * Calculates the expected material composition of a GTCEu layered ore vein.
 *
 * <p>
 * The calculation follows the order used by
 * {@link FillerConfigUtils.LayeredFillerEntry}: between ore, primary/secondary ore, and finally
 * sporadic ore. Source blocks that remain unchanged are omitted, and the
 * resulting ore-material weights are normalized to sum to one.
 * </p>
 */
public final class LayeredVeinMaterialDistribution {

    private LayeredVeinMaterialDistribution() {}

    /**
     * Calculates normalized ore-material ratios for a layered vein.
     *
     * @param definition initialized GTCEu vein definition
     * @return material-to-ratio map; empty if the filler is not layered or no
     *         ore materials can be resolved
     */
    public static Map<Material, Double> calculate(OreDepositDefinition definition) {
        if (!(definition.getBlockFiller() instanceof LayeredBlockFiller)) {
            return Collections.emptyMap();
        }

        LayeredBlockFiller blockFiller = (LayeredBlockFiller) definition.getBlockFiller();

        List<FillerEntry> possibleStates = blockFiller.getAllPossibleStates();
        if (possibleStates.size() != 1 ||
                !(possibleStates.get(0) instanceof FillerConfigUtils.LayeredFillerEntry)) {
            return Collections.emptyMap();
        }

        FillerConfigUtils.LayeredFillerEntry layered = (FillerConfigUtils.LayeredFillerEntry) possibleStates.get(0);

        int primaryLayers = layered.getPrimaryLayers();
        int secondaryLayers = layered.getSecondaryLayers();
        int betweenLayers = layered.getBetweenLayers();
        int totalLayers = primaryLayers + secondaryLayers;

        if (totalLayers <= 0) {
            return Collections.emptyMap();
        }

        double density = clamp01(definition.getDensity());
        int sporadicDivisor = Math.max(1, totalLayers - 1);
        int startPrimary = secondaryLayers;
        int startBetween = secondaryLayers - betweenLayers / 2;

        Map<Material, Double> result = new LinkedHashMap<>();

        Map<Material, Double> primary = getEntryDistribution(layered.getPrimary());
        Map<Material, Double> secondary = getEntryDistribution(layered.getSecondary());
        Map<Material, Double> between = getEntryDistribution(layered.getBetween());
        Map<Material, Double> sporadic = getEntryDistribution(layered.getSporadic());

        for (int layer = 0; layer < totalLayers; layer++) {
            boolean betweenEligible = layer >= startBetween &&
                    layer - startBetween + 1 <= betweenLayers;

            double betweenChance = betweenEligible ? density / 2.0 : 0.0;
            double mainChance = (1.0 - betweenChance) * density;
            double sporadicChance = (1.0 - betweenChance) *
                    (1.0 - density) *
                    density / sporadicDivisor;

            addScaled(result, between, betweenChance);
            addScaled(result,
                    layer >= startPrimary ? primary : secondary,
                    mainChance);
            addScaled(result, sporadic, sporadicChance);
        }

        normalize(result);
        return result;
    }

    /**
     * Resolves the material distribution represented by a filler entry.
     *
     * <p>
     * Weighted random entries retain their configured weights. For simple
     * entries, stone variants of the same ore material are deduplicated so
     * that the number of available host-rock variants does not inflate that
     * material's contribution.
     * </p>
     */
    private static Map<Material, Double> getEntryDistribution(FillerEntry entry) {
        List<Pair<Integer, FillerEntry>> weightedEntries = entry.getEntries();

        if (weightedEntries != null && !weightedEntries.isEmpty()) {
            Map<Material, Double> weightedResult = new LinkedHashMap<>();
            double totalWeight = 0.0;

            for (Pair<Integer, FillerEntry> weightedEntry : weightedEntries) {
                if (weightedEntry.getLeft() != null && weightedEntry.getLeft() > 0) {
                    totalWeight += weightedEntry.getLeft();
                }
            }

            if (totalWeight <= 0.0) {
                return Collections.emptyMap();
            }

            for (Pair<Integer, FillerEntry> weightedEntry : weightedEntries) {
                int weight = weightedEntry.getLeft();
                if (weight <= 0) {
                    continue;
                }

                Map<Material, Double> child = getEntryDistribution(weightedEntry.getRight());
                addScaled(weightedResult, child, weight / totalWeight);
            }

            normalize(weightedResult);
            return weightedResult;
        }

        Set<Material> materials = resolveMaterials(entry.getPossibleResults());
        if (materials.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Material, Double> result = new LinkedHashMap<>();
        double equalShare = 1.0 / materials.size();

        for (Material material : materials) {
            result.put(material, equalShare);
        }

        return result;
    }

    private static Set<Material> resolveMaterials(Collection<IBlockState> states) {
        Set<Material> materials = new LinkedHashSet<>();

        for (IBlockState state : states) {
            Item item = Item.getItemFromBlock(state.getBlock());
            if (item == Items.AIR) {
                continue;
            }

            final int metadata;
            try {
                metadata = state.getBlock().getMetaFromState(state);
            } catch (RuntimeException ignored) {
                continue;
            }

            ItemStack stack = new ItemStack(item, 1, metadata);
            UnificationEntry unificationEntry = OreDictUnifier.getUnificationEntry(stack);

            if (unificationEntry == null ||
                    unificationEntry.material == null ||
                    !unificationEntry.material.hasProperty(PropertyKey.ORE)) {
                continue;
            }

            materials.add(unificationEntry.material);
        }

        return materials;
    }

    private static void addScaled(Map<Material, Double> target,
                                  Map<Material, Double> source,
                                  double scale) {
        if (scale <= 0.0 || source.isEmpty()) {
            return;
        }

        for (Map.Entry<Material, Double> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue() * scale, Double::sum);
        }
    }

    private static void normalize(Map<Material, Double> values) {
        double total = 0.0;

        for (double value : values.values()) {
            if (value > 0.0 && Double.isFinite(value)) {
                total += value;
            }
        }

        if (total <= 0.0) {
            values.clear();
            return;
        }

        Iterator<Map.Entry<Material, Double>> iterator = values.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Material, Double> entry = iterator.next();
            double value = entry.getValue();

            if (value > 0.0 && Double.isFinite(value)) {
                entry.setValue(value / total);
            } else {
                iterator.remove();
            }
        }
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
