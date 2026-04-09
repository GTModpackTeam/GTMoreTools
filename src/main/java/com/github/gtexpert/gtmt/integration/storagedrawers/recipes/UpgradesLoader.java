package com.github.gtexpert.gtmt.integration.storagedrawers.recipes;

import static com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems.upgradeStorageGT;

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
        if (StorageDrawersConfigHolder.removeOriginal) {
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_obsidian"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_iron"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_gold"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_diamond"));
            ModHandler.removeRecipeByName(Mods.StorageDrawers.getResource("upgrade_storage_emerald"));
        }
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
        });
    }
}
