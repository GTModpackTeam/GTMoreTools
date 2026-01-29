package com.github.gtexpert.gtmoretools.integration.chisel.tools;

import static gregtech.api.unification.material.info.MaterialFlags.*;

import gregtech.api.GregTechAPI;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtmoretools.integration.chisel.ChiselConfigHolder;

public class ChiselToolRecipeHandler {

    public static void registerRecipes() {
        for (Material material : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!material.hasProperty(PropertyKey.TOOL)) continue;

            // Flint uses gem instead of plate/ingot
            if (material == Materials.Flint) {
                processFlintChiselRecipe();
                continue;
            }

            if (!material.hasFlag(GENERATE_PLATE)) continue;

            processChiselRecipe(material);
        }
    }

    private static void processChiselRecipe(Material material) {
        if (ChiselConfigHolder.hardToolRecipes) {
            ModHandler.addShapedRecipe(String.format("chisel_%s", material.getName()),
                    ChiselToolItems.CHISEL.get(material),
                    "fP", "Sh",
                    'P', new UnificationEntry(OrePrefix.plate, material),
                    'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
        } else {
            ModHandler.addShapedRecipe(String.format("chisel_%s", material.getName()),
                    ChiselToolItems.CHISEL.get(material),
                    " I", "S ",
                    'I', new UnificationEntry(OrePrefix.ingot, material),
                    'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
        }
    }

    private static void processFlintChiselRecipe() {
        if (ChiselConfigHolder.hardToolRecipes) {
            ModHandler.addShapedRecipe("chisel_flint",
                    ChiselToolItems.CHISEL.get(Materials.Flint),
                    "fF", "Sh",
                    'F', new UnificationEntry(OrePrefix.gem, Materials.Flint),
                    'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
        } else {
            ModHandler.addShapedRecipe("chisel_flint",
                    ChiselToolItems.CHISEL.get(Materials.Flint),
                    " F", "S ",
                    'F', new UnificationEntry(OrePrefix.gem, Materials.Flint),
                    'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
        }
    }
}
