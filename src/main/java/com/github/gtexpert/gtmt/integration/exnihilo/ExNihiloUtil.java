package com.github.gtexpert.gtmt.integration.exnihilo;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;

public class ExNihiloUtil {

    private static final List<GTRecipeInput> recipeInputs = new ArrayList<>();

    public static void init() {
        recipeInputs.clear();

        RecipeMaps.FORGE_HAMMER_RECIPES.getRecipeList().forEach(recipe -> recipeInputs.addAll(recipe.getInputs()));
    }

    public static boolean isContained(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        return recipeInputs.stream()
                .anyMatch(input -> input.acceptsStack(stack));
    }
}
