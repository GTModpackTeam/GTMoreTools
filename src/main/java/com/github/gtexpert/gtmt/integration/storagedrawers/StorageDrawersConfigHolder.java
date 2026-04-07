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
            "\"tier\" represents the required material voltage and can be set from 1 (LV) to 13 (OpV).",
            "The \"tier\" field is optional and defaults to 1 (LV) if omitted.",
            "If this entry is left empty, variants using the original materials with the same multipliers will be added at tier 1." })
    public static String[] upgradeMaterials = new String[] {
            "gregtech:obsidian@2", "gregtech:iron@3", "gregtech:gold@4", "gregtech:diamond@5", "gregtech:emerald@6"
    };
}
