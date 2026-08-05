package com.github.gtexpert.gtmt.integration.exnihilo.recipes;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;

import com.github.gtexpert.gtmt.integration.exnihilo.ExNihiloRecipeMaps;

import exnihilocreatio.compatibility.jei.sieve.SieveRecipe;
import exnihilocreatio.registries.registries.SieveRegistry;
import exnihilocreatio.registries.types.Siftable;

public class ExNihiloSieveRecipe {

    // Mirror Ex Nihilo Sifter recipes to Sifter RecipeMap
    public static void register(@NotNull SieveRegistry registry) {
        for (SieveRecipe recipe : registry.getRecipeList()) {
            for (ItemStack stack : recipe.getSievables()) {
                if (ExNihiloRecipeMaps.SIEVE_RECIPES.findRecipe(16, Arrays.asList(stack, recipe.getMesh()),
                        new ArrayList<>(), true) != null)
                    continue;
                SimpleRecipeBuilder builder = ExNihiloRecipeMaps.SIEVE_RECIPES.recipeBuilder()
                        .inputs(stack)
                        .inputNBT((new GTRecipeItemInput(recipe.getMesh()).setNonConsumable()), NBTMatcher.ANY,
                                NBTCondition.ANY);
                for (Siftable siftable : registry.getDrops(stack)) {
                    if (siftable.getDrop() == null) continue;
                    if (siftable.getChance() <= 0) continue;
                    if (siftable.getMeshLevel() == recipe.getMesh().getMetadata()) {
                        if ((int) siftable.getChance() * 10000 >= 10000) {
                            builder.outputs(siftable.getDrop().getItemStack());
                        } else {
                            builder.chancedOutput(siftable.getDrop().getItemStack(),
                                    (int) (siftable.getChance() * 10000), 500);
                        }
                    }
                }
                builder.buildAndRegister();
            }
        }
    }
}
