package com.github.gtexpert.gtmt.integration.storagedrawers.recipes;

import static com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems.upgradeStorageGT;

import gregtech.api.unification.material.Materials;

import net.minecraft.item.ItemStack;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtmt.api.util.Mods;
import com.github.gtexpert.gtmt.integration.storagedrawers.StorageDrawersConfigHolder;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradesMaterialRegistry;

public class UpgradesLoader {

    public static void upgradeStorage() {
        // original
        if (StorageDrawersConfigHolder.removeOriginal) {
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_obsidian"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_iron"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_gold"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_diamond"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_emerald"));
        }
        // custom
        MetaItem.MetaValueItem[] fieldGenerators = new MetaItem.MetaValueItem[] {
                MetaItems.FIELD_GENERATOR_LV, MetaItems.FIELD_GENERATOR_MV, MetaItems.FIELD_GENERATOR_HV,
                MetaItems.FIELD_GENERATOR_EV, MetaItems.FIELD_GENERATOR_IV, MetaItems.FIELD_GENERATOR_LuV,
                MetaItems.FIELD_GENERATOR_ZPM, MetaItems.FIELD_GENERATOR_UV
        };

        UpgradesMaterialRegistry.REGISTRY.values().forEach(data -> {
            Material material = data.getMaterial();
            int meta = data.getId();
            int tier = data.getTier();
            ItemStack output = new ItemStack(upgradeStorageGT, 1, meta);
            String recipeName = "storage_upgrade_" + material.getName();

            ModHandler.addShapedRecipe(true, recipeName, output,
                    "PSP", "SCS", "PTP",
                    'P', new UnificationEntry(OrePrefix.plate, material),
                    'S', new UnificationEntry(OrePrefix.screw, material),
                    'C', Mods.StorageDrawers.getItem("upgrade_template"),
                    'T', tier == -1 ? new UnificationEntry(OrePrefix.stickLong, material) : fieldGenerators[tier - 1]);
            // convert recipe
            if (material == Materials.Obsidian) {
                addStorageUpgradeConvertRecipe(Mods.StorageDrawers.getItem("upgrade_storage", 1, 0), output, material);
            }
            if (material == Materials.Iron) {
                addStorageUpgradeConvertRecipe(Mods.StorageDrawers.getItem("upgrade_storage", 1, 1), output, material);
            }
            if (material == Materials.Gold) {
                addStorageUpgradeConvertRecipe(Mods.StorageDrawers.getItem("upgrade_storage", 1, 2), output, material);
            }
            if (material == Materials.Diamond) {
                addStorageUpgradeConvertRecipe(Mods.StorageDrawers.getItem("upgrade_storage", 1, 3), output, material);
            }
            if (material == Materials.Emerald) {
                addStorageUpgradeConvertRecipe(Mods.StorageDrawers.getItem("upgrade_storage", 1, 4), output, material);
            }
        });
    }

    private static void addStorageUpgradeConvertRecipe(ItemStack original, ItemStack custom, Material material) {
        ModHandler.addShapelessRecipe("original_to_custom/" + material.getUnlocalizedName(), original, custom);
        ModHandler.addShapelessRecipe("custom_to_original/" + material.getUnlocalizedName(), custom, original);
    }
}
