package com.github.gtexpert.gtmt.integration.tic.materials;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.ToolProperty;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.integration.tic.api.HarvestLevels;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;

/**
 * Registers GT tool materials as TiC materials and manages the fluid-block integration list.
 *
 * <p>
 * For each GT material that carries both {@code TOOL} and {@code INGOT}/{@code GEM}:
 * <ul>
 * <li>If TiC already knows a material with the same name → <b>merge</b>: take the max of
 * each stat and add GT traits to the existing material.</li>
 * <li>Otherwise → <b>register</b>: create a new TiC material from scratch.</li>
 * </ul>
 */
public final class ToolMaterialRegistrar {

    /** TiC harvest level colors for levels beyond Cobalt (4). */
    private static final TextFormatting[] EXTRA_LEVEL_COLORS = {
            TextFormatting.DARK_AQUA,    // level 5
            TextFormatting.LIGHT_PURPLE, // level 6
            TextFormatting.WHITE,        // level 7+
    };

    private static final List<MaterialIntegration> integrations = new ArrayList<>();

    private ToolMaterialRegistrar() {}

    /**
     * Entry point called during {@code registerBlocks} when GT materials are available.
     * Also registers fluid blocks into the block registry.
     */
    public static void register(IForgeRegistry<Block> blockRegistry) {
        for (Material gtMaterial : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!gtMaterial.hasProperty(PropertyKey.TOOL)) continue;
            if (!gtMaterial.hasProperty(PropertyKey.INGOT) && !gtMaterial.hasProperty(PropertyKey.GEM)) continue;

            slimeknights.tconstruct.library.materials.Material existing = TinkerRegistry
                    .getMaterial(gtMaterial.getName());

            if (!existing.identifier.equals(
                    slimeknights.tconstruct.library.materials.Material.UNKNOWN.identifier)) {
                mergeMaterial(existing, gtMaterial);
            } else {
                registerMaterial(gtMaterial, blockRegistry);
            }
        }

        registerHarvestLevelNames();
    }

    /**
     * Called during {@code ModelRegistryEvent} to register fluid block models.
     * Must be called after {@link #register}.
     */
    public static void registerFluidModels() {
        for (MaterialIntegration integration : integrations) {
            integration.registerFluidModel();
        }
    }

    // -------------------------------------------------------------------------
    // Merge path
    // -------------------------------------------------------------------------

    /**
     * Enhance an already-registered TiC material with GT stats and traits.
     * For each stat type the <b>maximum</b> value is kept; bow draw-speed uses the
     * <b>minimum</b> (lower = faster draw).
     */
    private static void mergeMaterial(slimeknights.tconstruct.library.materials.Material ticMaterial,
                                      Material gtMaterial) {
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        // Head stats
        HeadMaterialStats existingHead = (HeadMaterialStats) ticMaterial.getStats("head");
        int ticHL = MaterialStatCalc.mapHarvestLevel(toolProp.getToolHarvestLevel());
        if (existingHead != null) {
            TinkerRegistry.addMaterialStats(ticMaterial, new HeadMaterialStats(
                    Math.max(existingHead.durability, toolProp.getToolDurability()),
                    Math.max(existingHead.miningspeed, toolProp.getToolSpeed()),
                    Math.max(existingHead.attack, toolProp.getToolAttackDamage()),
                    Math.max(existingHead.harvestLevel, ticHL)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, new HeadMaterialStats(
                    toolProp.getToolDurability(), toolProp.getToolSpeed(),
                    toolProp.getToolAttackDamage(), ticHL));
        }

        // Handle stats
        HandleMaterialStats existingHandle = (HandleMaterialStats) ticMaterial.getStats("handle");
        float gtHandleMod = MaterialStatCalc.calcHandleModifier(toolProp);
        int gtHandleDur = MaterialStatCalc.calcHandleDurability(toolProp);
        if (existingHandle != null) {
            TinkerRegistry.addMaterialStats(ticMaterial, new HandleMaterialStats(
                    Math.max(existingHandle.modifier, gtHandleMod),
                    Math.max(existingHandle.durability, gtHandleDur)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial,
                    new HandleMaterialStats(gtHandleMod, gtHandleDur));
        }

        // Extra stats
        ExtraMaterialStats existingExtra = (ExtraMaterialStats) ticMaterial.getStats("extra");
        int gtExtra = MaterialStatCalc.calcExtraDurability(toolProp.getToolDurability());
        if (existingExtra != null) {
            TinkerRegistry.addMaterialStats(ticMaterial,
                    new ExtraMaterialStats(Math.max(existingExtra.extraDurability, gtExtra)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, new ExtraMaterialStats(gtExtra));
        }

        // Bow stats — lower drawspeed is faster (better)
        var existingBow = (slimeknights.tconstruct.library.materials.BowMaterialStats) ticMaterial.getStats("bow");
        var gtBow = MaterialStatCalc.calcBowStats(toolProp);
        if (existingBow != null) {
            TinkerRegistry.addMaterialStats(ticMaterial,
                    new slimeknights.tconstruct.library.materials.BowMaterialStats(
                            Math.min(existingBow.drawspeed, gtBow.drawspeed),
                            Math.max(existingBow.range, gtBow.range),
                            Math.max(existingBow.bonusDamage, gtBow.bonusDamage)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, gtBow);
        }

        // Arrow shaft stats
        var existingShaft = (slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats) ticMaterial
                .getStats("shaft");
        var gtShaft = MaterialStatCalc.calcShaftStats(toolProp);
        if (existingShaft != null) {
            TinkerRegistry.addMaterialStats(ticMaterial,
                    new slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats(
                            Math.max(existingShaft.modifier, gtShaft.modifier),
                            Math.max(existingShaft.bonusAmmo, gtShaft.bonusAmmo)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, gtShaft);
        }

        if (ticHL > 4) {
            HarvestLevels.registerIfAbsent(ticHL, gtMaterial.getLocalizedName());
        }

        MaterialTraitApplier.applyTraits(ticMaterial, gtMaterial, toolProp);
    }

    // -------------------------------------------------------------------------
    // Register path
    // -------------------------------------------------------------------------

    private static void registerMaterial(Material gtMaterial, IForgeRegistry<Block> blockRegistry) {
        String identifier = ModValues.MODID + "." + gtMaterial.getName();
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        slimeknights.tconstruct.library.materials.Material ticMaterial = new slimeknights.tconstruct.library.materials.Material(
                identifier, gtMaterial.getMaterialRGB(), true);

        injectTranslation(identifier, gtMaterial);

        int durability = toolProp.getToolDurability();
        int ticHarvestLevel = MaterialStatCalc.mapHarvestLevel(toolProp.getToolHarvestLevel());

        if (ticHarvestLevel > 4) {
            HarvestLevels.registerIfAbsent(ticHarvestLevel, gtMaterial.getLocalizedName());
        }

        TinkerRegistry.addMaterialStats(ticMaterial,
                new HeadMaterialStats(durability, toolProp.getToolSpeed(),
                        toolProp.getToolAttackDamage(), ticHarvestLevel),
                new HandleMaterialStats(MaterialStatCalc.calcHandleModifier(toolProp),
                        MaterialStatCalc.calcHandleDurability(toolProp)),
                new ExtraMaterialStats(MaterialStatCalc.calcExtraDurability(durability)),
                MaterialStatCalc.calcBowStats(toolProp),
                MaterialStatCalc.calcShaftStats(toolProp));

        MaterialTraitApplier.applyTraits(ticMaterial, gtMaterial, toolProp);

        String oreSuffix = gtMaterial.toCamelCaseString();
        Fluid fluid = getFluid(gtMaterial);

        if (gtMaterial.hasProperty(PropertyKey.INGOT) && oreSuffix != null) {
            ticMaterial.addCommonItems(oreSuffix);
            ticMaterial.addItem("bolt" + oreSuffix, 1,
                    slimeknights.tconstruct.library.materials.Material.VALUE_Ingot / 4);
        } else if (gtMaterial.hasProperty(PropertyKey.GEM) && oreSuffix != null) {
            ticMaterial.addItem("gem" + oreSuffix, 1,
                    slimeknights.tconstruct.library.materials.Material.VALUE_Ingot);
            ticMaterial.addItem("block" + oreSuffix, 1,
                    slimeknights.tconstruct.library.materials.Material.VALUE_Block);
        }

        MaterialIntegration integration;
        if (fluid != null && oreSuffix != null) {
            integration = new MaterialIntegration(ticMaterial, fluid, oreSuffix);
        } else if (gtMaterial.hasProperty(PropertyKey.INGOT) && oreSuffix != null) {
            integration = new MaterialIntegration("ingot" + oreSuffix, ticMaterial, null, null);
            integration.setRepresentativeItem("ingot" + oreSuffix);
        } else if (gtMaterial.hasProperty(PropertyKey.GEM) && oreSuffix != null) {
            integration = new MaterialIntegration("gem" + oreSuffix, ticMaterial, null, null);
            integration.setRepresentativeItem("gem" + oreSuffix);
        } else {
            integration = new MaterialIntegration(ticMaterial);
        }

        TinkerRegistry.integrate(integration);
        integration.preInit();
        integration.registerFluidBlock(blockRegistry);
        integrations.add(integration);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void registerHarvestLevelNames() {
        // Override TiC's native names (0–4) with vanilla/GT naming convention
        // so that the same harvest level shows the same name in both GT and TiC tooltips.
        java.util.Map<Integer, String> ticNames =
                slimeknights.tconstruct.library.utils.HarvestLevels.harvestLevelNames;
        ticNames.put(0, TextFormatting.DARK_GREEN + "Wood");
        ticNames.put(1, TextFormatting.GRAY + "Stone");
        ticNames.put(2, TextFormatting.WHITE + "Iron");
        ticNames.put(3, TextFormatting.AQUA + "Diamond");
        // Level 4 (Cobalt) — no vanilla equivalent, keep TiC's name

        // Register GT-specific levels above Cobalt (4)
        HarvestLevels.getNames().forEach((level, name) -> {
            int colorIndex = Math.min(level - 5, EXTRA_LEVEL_COLORS.length - 1);
            ticNames.put(level, EXTRA_LEVEL_COLORS[colorIndex] + name);
        });
    }

    static void injectTranslation(String ticIdentifier, Material gtMaterial) {
        String key = "material." + ticIdentifier + ".name";
        String entry = key + "=" + gtMaterial.getLocalizedName() + "\n";
        LanguageMap.inject(new ByteArrayInputStream(entry.getBytes(StandardCharsets.UTF_8)));
    }

    static Fluid getFluid(Material material) {
        if (!material.hasProperty(PropertyKey.FLUID)) return null;
        Fluid fluid = material.getFluid();
        return (fluid != null && FluidRegistry.isFluidRegistered(fluid)) ? fluid : null;
    }

    static List<MaterialIntegration> getIntegrations() {
        return integrations;
    }
}
