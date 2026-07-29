package com.github.gtexpert.gtmt.integration.exnihilo.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.exnihilo.ExNihiloConfigHolder;

import exnihilocreatio.ModBlocks;

public class ExNihiloMiscRecipe {

    public static void init() {
        // Mesh
        if (ExNihiloConfigHolder.harderMeshes) {
            ItemStack mesh = Mods.ExNihilo.getItem("item_mesh", 1, 1);
            // Flint -> Tin Alloy
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 2));
            ModHandler.addShapedRecipe("tin_alloy_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 2), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.TinAlloy),
                    'I', new ItemStack(Items.FLINT),
                    'M', mesh);
            // Iron -> Steel
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 3));
            ModHandler.addShapedRecipe("steel_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 3), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.Steel),
                    'I', new UnificationEntry(OrePrefix.ingot, Materials.Iron),
                    'M', mesh);
            // Diamond -> Aluminum
            ModHandler.removeRecipeByOutput(Mods.ExNihilo.getItem("item_mesh", 1, 4));
            ModHandler.addShapedRecipe("aluminium_mesh", Mods.ExNihilo.getItem("item_mesh", 1, 4), "RIR", "RMR", "RIR",
                    'R', new UnificationEntry(OrePrefix.stick, Materials.Aluminium),
                    'I', new UnificationEntry(OrePrefix.gem, Materials.Diamond),
                    'M', mesh);
        }

        // Hammer conversion
        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.SAND)
                .output(ModBlocks.dust)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 1))
                .output(ModBlocks.crushedGranite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 3))
                .output(ModBlocks.crushedDiorite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE, 1, 5))
                .output(ModBlocks.crushedAndesite)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.NETHERRACK)
                .output(ModBlocks.netherrackCrushed)
                .EUt(16).duration(10)
                .buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(Blocks.END_STONE)
                .output(ModBlocks.endstoneCrushed)
                .EUt(16).duration(10)
                .buildAndRegister();
    }
}
