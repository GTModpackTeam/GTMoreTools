package com.github.gtexpert.gtmt.integration.tic;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import com.google.common.collect.ImmutableSet;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.ToolProperty;

import com.github.gtexpert.gtmt.api.ModValues;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;

public final class TiCMaterials {

    /** Materials already registered by TiC natively - skip to avoid duplicates */
    private static final Set<String> TIC_NATIVE_MATERIALS = ImmutableSet.of(
            // Metals
            "iron", "cobalt", "ardite", "manyullyn", "copper", "bronze",
            "lead", "silver", "electrum", "steel", "pigiron", "knightslime",
            "alubrass", "alumite",
            // Natural / Vanilla
            "wood", "stone", "flint", "cactus", "bone", "obsidian",
            "prismarine", "endstone", "paper", "sponge", "firewood",
            "netherrack",
            // Gems (vanilla)
            "diamond", "emerald");

    private static final List<MaterialIntegration> integrations = new ArrayList<>();

    private TiCMaterials() {}

    /**
     * Called during registerBlocks when GT materials are available.
     * Also registers fluid blocks into the block registry.
     */
    public static void register(IForgeRegistry<Block> blockRegistry) {
        for (Material gtMaterial : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!gtMaterial.hasProperty(PropertyKey.TOOL)) continue;
            if (!gtMaterial.hasProperty(PropertyKey.INGOT) && !gtMaterial.hasProperty(PropertyKey.GEM)) continue;
            if (TIC_NATIVE_MATERIALS.contains(gtMaterial.getName())) continue;

            registerMaterial(gtMaterial, blockRegistry);
        }
    }

    /**
     * Called during ModelRegistryEvent to register fluid block models.
     */
    public static void registerFluidModels() {
        for (MaterialIntegration integration : integrations) {
            integration.registerFluidModel();
        }
    }

    private static void registerMaterial(Material gtMaterial, IForgeRegistry<Block> blockRegistry) {
        String identifier = ModValues.MODID + "." + gtMaterial.getName();
        int color = gtMaterial.getMaterialRGB();
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        // Create TiC material (hidden until integration activates it)
        slimeknights.tconstruct.library.materials.Material ticMaterial = new slimeknights.tconstruct.library.materials.Material(
                identifier, color, true);

        // Inject localized name from CEu into TiC's translation system
        injectTranslation(identifier, gtMaterial);

        // Register material stats from GT tool properties
        int durability = toolProp.getToolDurability();
        float speed = toolProp.getToolSpeed();
        float attack = toolProp.getToolAttackDamage();
        int harvestLevel = mapHarvestLevel(toolProp.getToolHarvestLevel());

        TinkerRegistry.addMaterialStats(ticMaterial,
                new HeadMaterialStats(durability, speed, attack, harvestLevel),
                new HandleMaterialStats(calcHandleModifier(toolProp), calcHandleDurability(toolProp)),
                new ExtraMaterialStats(calcExtraDurability(durability)),
                calcBowStats(toolProp));

        // Setup item associations
        String oreSuffix = toPascalCase(gtMaterial.getName());
        Fluid fluid = getFluid(gtMaterial);

        if (gtMaterial.hasProperty(PropertyKey.INGOT) && oreSuffix != null) {
            ticMaterial.addCommonItems(oreSuffix);
        } else if (gtMaterial.hasProperty(PropertyKey.GEM) && oreSuffix != null) {
            ticMaterial.addItem("gem" + oreSuffix, 1,
                    slimeknights.tconstruct.library.materials.Material.VALUE_Ingot);
            ticMaterial.addItem("block" + oreSuffix, 1,
                    slimeknights.tconstruct.library.materials.Material.VALUE_Block);
        }

        // Register integration
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

        // TiC's preInit loop already ran by this point, so we drive the lifecycle manually.
        // preInit() has an internal guard against double execution.
        TinkerRegistry.integrate(integration);
        integration.preInit();
        integration.registerFluidBlock(blockRegistry);
        integrations.add(integration);
    }

    /**
     * Inject CEu's localized material name into TiC's translation system.
     * TiC looks up "material.&lt;identifier&gt;.name" via the deprecated I18n / LanguageMap.
     */
    private static void injectTranslation(String ticIdentifier, Material gtMaterial) {
        String key = "material." + ticIdentifier + ".name";
        String localizedName = gtMaterial.getLocalizedName();
        String entry = key + "=" + localizedName + "\n";
        LanguageMap.inject(new ByteArrayInputStream(entry.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Map GT harvest level to TiC harvest level.
     * GT: 0=Wood, 1=Stone, 2=Iron, 3=Diamond, 4+=higher
     * TiC: 0=Stone, 1=Iron, 2=Diamond, 3=Obsidian, 4=Cobalt
     */
    private static int mapHarvestLevel(int gtLevel) {
        return switch (gtLevel) {
            case 0, 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            default -> 4;
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
     * Calculate bow stats from GT tool properties.
     * Reference values (TiC native):
     * iron: drawspeed=0.5, range=1.5, bonus=7
     * steel: drawspeed=0.4, range=2.0, bonus=9
     * copper: drawspeed=0.6, range=1.45, bonus=5
     */
    private static BowMaterialStats calcBowStats(ToolProperty toolProp) {
        float speed = toolProp.getToolSpeed();
        float attack = toolProp.getToolAttackDamage();

        // drawspeed: lighter/faster materials draw faster; clamp 0.2~1.5
        float drawspeed = Math.max(0.2f, Math.min(1.5f, 1.0f / (1.0f + speed * 0.1f)));
        // range: scales with speed; clamp 0.4~3.0
        float range = Math.max(0.4f, Math.min(3.0f, 0.5f + speed * 0.15f));
        // bonusDamage: scales with attack; clamp 0~15
        float bonusDamage = Math.max(0f, Math.min(15f, attack * 1.2f));

        return new BowMaterialStats(drawspeed, range, bonusDamage);
    }

    private static Fluid getFluid(Material material) {
        if (!material.hasProperty(PropertyKey.FLUID)) return null;

        Fluid fluid = material.getFluid();
        if (fluid != null && FluidRegistry.isFluidRegistered(fluid)) {
            return fluid;
        }
        return null;
    }

    private static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                sb.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            }
        }
        return sb.toString();
    }
}
