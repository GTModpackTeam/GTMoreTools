package com.github.gtexpert.gtmt.integration.exnihilo.recipes;

import gregtech.api.GregTechAPI;
import gregtech.api.items.toolitem.ToolHelper;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.exnihilo.ExNihiloConfigHolder;
import com.github.gtexpert.gtmt.integration.exnihilo.tools.ExNihiloToolsItems;

public class ExNihiloToolRecipe {

    public static void registerRecipes() {
        for (Material material : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!material.hasProperty(PropertyKey.TOOL)) continue;

            crooks(material);
        }

        if (ExNihiloConfigHolder.replaceCrook) {
            replaceCrooks();
        }
    }

    public static void crooks(Material material) {
        if (!ExNihiloConfigHolder.replaceCrook &&
                (material == Materials.Iron || material == Materials.Gold || material == Materials.Diamond)) {
            return;
        }

        if (material.hasFlag(MaterialFlags.GENERATE_ROD)) {
            ModHandler.addShapedRecipe(String.format("crook_%s", material.getName()),
                    ExNihiloToolsItems.CROOK.get(material),
                    "RR", " R", " R",
                    'R', new UnificationEntry(OrePrefix.stick, material));
        } else if (material.hasProperty(PropertyKey.GEM)) {
            ModHandler.addShapedRecipe(String.format("crook_%s", material.getName()),
                    ExNihiloToolsItems.CROOK.get(material),
                    "GG", " G", " G",
                    'G', new UnificationEntry(OrePrefix.gem, material));
        }
    }

    public static void replaceCrooks() {
        // replace recipes
        // Gold
        ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("crook_gold"));
        ModHandler.addShapedRecipe("crook_gold", Mods.ExNihilo.getItem("crook_gold"),
                "RR", " R", " R",
                'R', new UnificationEntry(OrePrefix.stick, Materials.Gold));

        // replace crooks
        // Wood, Iron, Diamond
        ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("crook_wood"));
        ModHandler.addShapedRecipe("crook_wood",
                ToolHelper.getAndSetToolData(ExNihiloToolsItems.CROOK, Materials.Wood, 47, 1, 4F, 1F),
                "RR", " R", " R",
                'R', new UnificationEntry(OrePrefix.stick, Materials.Wood));

        ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("crook_iron"));
        ModHandler.addShapedRecipe("crook_diamond", ExNihiloToolsItems.CROOK.get(Materials.Iron),
                "RR", " R", " R",
                'R', new UnificationEntry(OrePrefix.stick, Materials.Iron));

        ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("crook_diamond"));
        ModHandler.addShapedRecipe("crook_diamond", ExNihiloToolsItems.CROOK.get(Materials.Diamond),
                "RR", " R", " R",
                'R', new UnificationEntry(OrePrefix.stick, Materials.Diamond));
    }
}
