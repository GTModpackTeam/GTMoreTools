package com.github.gtexpert.gtmt.integration.storagedrawers;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.modules.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.storage_drawers")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_DRAWERS,
        category = "StorageDrawers")
public class StorageDrawersConfigHolder {

    @Config.Comment({ "Remove original storage upgrade recipe", "default: true" })
    public static boolean removeOriginal = true;

    @Config.Comment({ "Specifies the materials that can be used to craft Storage Upgrades.",
            "Format: modId:materialName@multiplier$tier",
            "\"tier\" represents the required field generator tier and can be set from 1 (LV) to 8 (UV).",
            "The \"tier\" field is optional and defaults to -1 (field generator is not required) if omitted.",
            "Note: the \"multiplier\" value is not affected by StorageDrawers's config.",
            "The range of \"multiplier\" is 1 to (2,147,483,647 / (1x1 Drawer Capacity * 64 * 7)) (default: 149796)",
            "Original materials (Obsidian/Iron/Gold/Diamond/Emerald) are automatically added with tier -1.",
            "Do not specify them here.",
            "The multiplier values for original materials are controlled by the StorageDrawers config.",
            "If this entry is left empty, only the original materials will be added." })
    public static String[] upgradeMaterials = new String[] {
    };
}
