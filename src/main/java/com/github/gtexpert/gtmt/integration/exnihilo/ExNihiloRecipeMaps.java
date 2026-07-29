package com.github.gtexpert.gtmt.integration.exnihilo;

import net.minecraft.init.SoundEvents;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;

import com.github.gtexpert.gtmt.integration.exnihilo.metatileentities.SieveRecipeMap;

public class ExNihiloRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> SIEVE_RECIPES = new SieveRecipeMap("auto_sieve", 2, 42, 0, 0,
            new SimpleRecipeBuilder().duration(100).EUt(16), false)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_SIFT, ProgressWidget.MoveType.VERTICAL_DOWNWARDS)
                    .setSound(SoundEvents.BLOCK_SAND_PLACE);
}
