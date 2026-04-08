package com.github.gtexpert.gtmt.integration.storagedrawers.recipes;

import gregtech.api.recipes.ModHandler;

import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.storagedrawers.StorageDrawersConfigHolder;

public class StorageUpgradeLoader {

    public static void init() {
        if (StorageDrawersConfigHolder.removeOriginal) {
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_obsidian"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_iron"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_gold"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_diamond"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_emerald"));
        }
    }
}
