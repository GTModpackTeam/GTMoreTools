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
            "\"tier\" represents the required material voltage and can be set from 1 (LV) to 8 (UV).",
            "The \"tier\" field is optional and defaults to 1 (LV) if omitted.",
            "Note: the \"multiplier\" value is not affected by StorageDrawers's config.",
            "If this entry is left empty, variants using the original materials with the same multipliers will be added at tier 1." })
    public static String[] upgradeMaterials = new String[] {
            "gregtech:obsidian@2", "gregtech:iron@4", "gregtech:gold@8", "gregtech:diamond@16", "gregtech:emerald@32"
    };
}
