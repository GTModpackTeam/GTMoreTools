package com.github.gtexpert.gtmt.integration.tic;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.ToolProperty;
import gregtech.api.unification.stack.MaterialStack;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.integration.tic.traits.GTMTTraits;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.TinkerTraits;

public final class TiCMaterials {

    private static final List<MaterialIntegration> integrations = new ArrayList<>();

    /**
     * Tracks harvest level name for the first GT material seen at each level beyond Cobalt (4).
     * Key = TiC harvest level, Value = localized material name.
     */
    private static final Map<Integer, String> pendingHarvestLevelNames = new LinkedHashMap<>();

    /** TiC harvest level colors for levels beyond Cobalt (4). */
    private static final TextFormatting[] EXTRA_LEVEL_COLORS = {
            TextFormatting.DARK_AQUA,    // level 5
            TextFormatting.LIGHT_PURPLE, // level 6
            TextFormatting.WHITE,        // level 7+
    };

    private TiCMaterials() {}

    /**
     * Called during registerBlocks when GT materials are available.
     * Also registers fluid blocks into the block registry.
     *
     * <p>For each GT tool material:
     * <ul>
     *   <li>If TiC already has a material with the same name → <b>merge</b>: take max stats
     *       and add GT enchantments/traits to the existing material.</li>
     *   <li>Otherwise → <b>register</b>: create a new TiC material.</li>
     * </ul>
     */
    public static void register(IForgeRegistry<Block> blockRegistry) {
        for (Material gtMaterial : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!gtMaterial.hasProperty(PropertyKey.TOOL)) continue;
            if (!gtMaterial.hasProperty(PropertyKey.INGOT) && !gtMaterial.hasProperty(PropertyKey.GEM)) continue;
            slimeknights.tconstruct.library.materials.Material existing =
                    TinkerRegistry.getMaterial(gtMaterial.getName());

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
     * Called during ModelRegistryEvent to register fluid block models.
     */
    public static void registerFluidModels() {
        for (MaterialIntegration integration : integrations) {
            integration.registerFluidModel();
        }
    }

    // -------------------------------------------------------------------------
    // Merge path — GT material matches an existing TiC native material
    // -------------------------------------------------------------------------

    /**
     * Enhance an already-registered TiC material with GT stats and traits.
     * For each stat type, the <b>maximum</b> of the TiC and GT values is kept.
     * Bow drawspeed uses the <b>minimum</b> (faster draw = better).
     */
    private static void mergeMaterial(slimeknights.tconstruct.library.materials.Material ticMaterial,
                                      Material gtMaterial) {
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        // Head stats
        HeadMaterialStats existingHead = (HeadMaterialStats) ticMaterial.getStats("head");
        int ticHL = mapHarvestLevel(toolProp.getToolHarvestLevel());
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
        float gtHandleMod = calcHandleModifier(toolProp);
        int gtHandleDur = calcHandleDurability(toolProp);
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
        int gtExtra = calcExtraDurability(toolProp.getToolDurability());
        if (existingExtra != null) {
            TinkerRegistry.addMaterialStats(ticMaterial,
                    new ExtraMaterialStats(Math.max(existingExtra.extraDurability, gtExtra)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, new ExtraMaterialStats(gtExtra));
        }

        // Bow stats — lower drawspeed is faster (better)
        BowMaterialStats existingBow = (BowMaterialStats) ticMaterial.getStats("bow");
        BowMaterialStats gtBow = calcBowStats(toolProp);
        if (existingBow != null) {
            TinkerRegistry.addMaterialStats(ticMaterial, new BowMaterialStats(
                    Math.min(existingBow.drawspeed, gtBow.drawspeed),
                    Math.max(existingBow.range, gtBow.range),
                    Math.max(existingBow.bonusDamage, gtBow.bonusDamage)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, gtBow);
        }

        // Arrow shaft stats — GT bolt as shaft material
        ArrowShaftMaterialStats existingShaft = (ArrowShaftMaterialStats) ticMaterial.getStats("shaft");
        ArrowShaftMaterialStats gtShaft = calcShaftStats(toolProp);
        if (existingShaft != null) {
            TinkerRegistry.addMaterialStats(ticMaterial, new ArrowShaftMaterialStats(
                    Math.max(existingShaft.modifier, gtShaft.modifier),
                    Math.max(existingShaft.bonusAmmo, gtShaft.bonusAmmo)));
        } else {
            TinkerRegistry.addMaterialStats(ticMaterial, gtShaft);
        }

        // Harvest level name tracking
        if (ticHL > 4 && !pendingHarvestLevelNames.containsKey(ticHL)) {
            pendingHarvestLevelNames.put(ticHL, gtMaterial.getLocalizedName());
        }

        // Apply GT traits/enchantments on top of the existing TiC traits
        applyTraits(ticMaterial, gtMaterial, toolProp);
    }

    // -------------------------------------------------------------------------
    // Register path — new material not already in TiC
    // -------------------------------------------------------------------------

    private static void registerMaterial(Material gtMaterial, IForgeRegistry<Block> blockRegistry) {
        String identifier = ModValues.MODID + "." + gtMaterial.getName();
        int color = gtMaterial.getMaterialRGB();
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        slimeknights.tconstruct.library.materials.Material ticMaterial =
                new slimeknights.tconstruct.library.materials.Material(identifier, color, true);

        injectTranslation(identifier, gtMaterial);

        int durability = toolProp.getToolDurability();
        float speed = toolProp.getToolSpeed();
        float attack = toolProp.getToolAttackDamage();
        int ticHarvestLevel = mapHarvestLevel(toolProp.getToolHarvestLevel());

        if (ticHarvestLevel > 4 && !pendingHarvestLevelNames.containsKey(ticHarvestLevel)) {
            pendingHarvestLevelNames.put(ticHarvestLevel, gtMaterial.getLocalizedName());
        }

        TinkerRegistry.addMaterialStats(ticMaterial,
                new HeadMaterialStats(durability, speed, attack, ticHarvestLevel),
                new HandleMaterialStats(calcHandleModifier(toolProp), calcHandleDurability(toolProp)),
                new ExtraMaterialStats(calcExtraDurability(durability)),
                calcBowStats(toolProp),
                calcShaftStats(toolProp));

        applyTraits(ticMaterial, gtMaterial, toolProp);

        String oreSuffix = toPascalCase(gtMaterial.getName());
        Fluid fluid = getFluid(gtMaterial);

        if (gtMaterial.hasProperty(PropertyKey.INGOT) && oreSuffix != null) {
            ticMaterial.addCommonItems(oreSuffix);
            // GT bolt = 1/4 ingot → matches TiC arrow shaft part cost
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
    // Shared trait / property logic
    // -------------------------------------------------------------------------

    /**
     * Assigns TiC traits based on GT material properties — no material-name hardcoding.
     *
     * <ul>
     * <li><b>Holy</b>: direct composition contains Silver</li>
     * <li><b>Heat Resistant</b>: blast temp ≥ 2500 K</li>
     * <li><b>Cryogenic</b>: blast temp in [1750, 2500) — vacuum-freezer processed</li>
     * <li><b>Anti-Corrosion</b>: durability ≥ 2000 and not already unbreakable</li>
     * <li><b>Heavy Blow</b>: attack damage ≥ 10</li>
     * <li><b>Magnetic</b>: GT {@code isMagnetic} flag</li>
     * <li><b>Unbreakable</b>: GT {@code isUnbreakable} flag</li>
     * <li><b>Enchantment traits</b>: each GT ToolProperty enchantment</li>
     * </ul>
     */
    private static void applyTraits(slimeknights.tconstruct.library.materials.Material ticMaterial,
                                    Material gtMaterial, ToolProperty toolProp) {
        int blastTemp = gtMaterial.getBlastTemperature();
        int durability = toolProp.getToolDurability();
        float attack = toolProp.getToolAttackDamage();

        if (containsMaterial(gtMaterial, Materials.Silver)) {
            ticMaterial.addTrait(TinkerTraits.holy, "head");
        }

        if (blastTemp >= 2500) {
            ticMaterial.addTrait(GTMTTraits.HEAT_RESISTANT, "head");
        } else if (blastTemp >= 1750) {
            ticMaterial.addTrait(GTMTTraits.CRYOGENIC, "head");
        }

        if (!toolProp.getUnbreakable() && durability >= 2000) {
            ticMaterial.addTrait(GTMTTraits.ANTI_CORROSION);
        }

        if (attack >= 10.0f) {
            ticMaterial.addTrait(GTMTTraits.HEAVY_BLOW, "head");
        }

        if (toolProp.isMagnetic()) {
            ticMaterial.addTrait(TinkerTraits.magnetic);
        }
        if (toolProp.getUnbreakable()) {
            ticMaterial.addTrait(GTMTTraits.UNBREAKABLE);
        }

        toolProp.getEnchantments().forEach((enchantment, enchLevel) -> {
            int level = enchLevel.getLevel(toolProp.getToolHarvestLevel());
            if (level > 0) {
                ticMaterial.addTrait(GTMTTraits.getOrCreateEnchantmentTrait(enchantment, level), "head");
            }
        });
    }

    /** Register names for harvest levels beyond TiC's native Cobalt (4). */
    private static void registerHarvestLevelNames() {
        for (Map.Entry<Integer, String> entry : pendingHarvestLevelNames.entrySet()) {
            int level = entry.getKey();
            String name = entry.getValue();
            int colorIndex = Math.min(level - 5, EXTRA_LEVEL_COLORS.length - 1);
            HarvestLevels.harvestLevelNames.put(level, EXTRA_LEVEL_COLORS[colorIndex] + name);
        }
    }

    /** Check whether a material's direct composition contains the given target material. */
    private static boolean containsMaterial(Material material, Material target) {
        for (MaterialStack stack : material.getMaterialComponents()) {
            if (stack.material == target) return true;
        }
        return false;
    }

    private static void injectTranslation(String ticIdentifier, Material gtMaterial) {
        String key = "material." + ticIdentifier + ".name";
        String entry = key + "=" + gtMaterial.getLocalizedName() + "\n";
        LanguageMap.inject(new ByteArrayInputStream(entry.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Maps GT harvest level to TiC harvest level.
     * GT 5 → TiC 4 (Cobalt), GT 6 → TiC 5 (new), GT 7 → TiC 6 (new), …
     */
    private static int mapHarvestLevel(int gtLevel) {
        return switch (gtLevel) {
            case 0, 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            default -> gtLevel - 1;
        };
    }

    private static float calcHandleModifier(ToolProperty toolProp) {
        float modifier = 0.5f + (toolProp.getToolDurability() / 2000.0f);
        return Math.max(0.1f, Math.min(modifier, 2.0f));
    }

    private static int calcHandleDurability(ToolProperty toolProp) {
        return (int) (toolProp.getToolDurability() * 0.1f);
    }

    private static int calcExtraDurability(int durability) {
        return (int) (durability * 0.15f);
    }

    /**
     * Calculates arrow shaft stats from GT tool properties.
     * Reference (TiC native): wood modifier=1.0/bonus=0.0, prismarine modifier=1.5/bonus=0.5.
     * GT metals are generally better shafts: modifier scales with attack, bonus damage also scales.
     */
    private static ArrowShaftMaterialStats calcShaftStats(ToolProperty toolProp) {
        float attack = toolProp.getToolAttackDamage();
        // modifier: 0.8 base + attack contribution, clamped [0.5, 3.0]
        float modifier = Math.max(0.5f, Math.min(3.0f, 0.8f + attack * 0.04f));
        // bonusAmmo: bonus arrows per shot, scales with attack, clamped [0, 10]
        int bonusAmmo = Math.min(10, (int) (attack / 5f));
        return new ArrowShaftMaterialStats(modifier, bonusAmmo);
    }

    /**
     * Calculates bow stats from GT tool properties.
     * Reference (TiC native): iron drawspeed=0.5, range=1.5, bonusDamage=7.
     */
    private static BowMaterialStats calcBowStats(ToolProperty toolProp) {
        float speed = toolProp.getToolSpeed();
        float attack = toolProp.getToolAttackDamage();
        float drawspeed = Math.max(0.2f, Math.min(1.5f, 1.0f / (1.0f + speed * 0.1f)));
        float range = Math.max(0.4f, Math.min(3.0f, 0.5f + speed * 0.15f));
        float bonusDamage = Math.max(0f, Math.min(15f, attack * 1.2f));
        return new BowMaterialStats(drawspeed, range, bonusDamage);
    }

    private static Fluid getFluid(Material material) {
        if (!material.hasProperty(PropertyKey.FLUID)) return null;
        Fluid fluid = material.getFluid();
        return (fluid != null && FluidRegistry.isFluidRegistered(fluid)) ? fluid : null;
    }

    private static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        boolean cap = true;
        for (char c : name.toCharArray()) {
            if (c == '_') { cap = true; } else { sb.append(cap ? Character.toUpperCase(c) : c); cap = false; }
        }
        return sb.toString();
    }
}
