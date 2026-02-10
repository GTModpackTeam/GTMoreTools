package com.github.gtexpert.gtmt.integration.chisel;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

import com.github.gtexpert.gtmt.api.gui.ModGuiTextures;

public class ChiselRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> AUTO_CHISEL_RECIPES = new RecipeMap<>(
            "auto_chisel", 2, 9, 0, 0, new SimpleRecipeBuilder(), true)
                    .setSlotOverlay(false, false, false, GuiTextures.BOXED_BACKGROUND)
                    .setProgressBar(ModGuiTextures.PROGRESS_BAR_CHISEL, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.FILE_TOOL);
}
