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
            "Format: modId:materialName@multiplier%tier",
            "==Notes==",
            "--Material--",
            "Must have the dust property",
            "--Multiplier--",
            "The range of \"multiplier\" is 1 to 2147483647",
            "The \"multiplier\" value is not affected by StorageDrawers config.",
            "--Tier--",
            "\"tier\" represents the required field generator tier and can be set from 1 (LV) to 8 (UV).",
            "The \"tier\" field is optional and defaults to -1 (no field generator required) if omitted.",
            "--Others--",
            "Original materials (Obsidian/Iron/Gold/Diamond/Emerald) are automatically added with tier -1.",
            "Do not specify them here.",
            "The multiplier values for original materials are controlled by the StorageDrawers config.",
            "If this entry is left empty, only the original materials will be added." })
    public static String[] upgradeMaterials = new String[] {
            "gregtech:wrought_iron@4", "gregtech:bronze@8",
            "gregtech:steel@16", "gregtech:bismuth_bronze@32%1",
            "gregtech:aluminium@64", "gregtech:rose_gold@128%2",
            "gregtech:stainless_steel@256", "gregtech:ultimet@512%3",
            "gregtech:titanium@1024", "gregtech:ruridit@2048%4",
            "gregtech:tungsten_steel@4096", "gregtech:hssg@8192%5",
            "gregtech:rhodium_plated_palladium@16384", "gregtech:europium@32768%6",
            "gregtech:naquadah_alloy@65536", "gregtech:americium@131072%7",
            "gregtech:darmstadtium@262144", "gregtech:neutronium@524288%8"
    };
}
