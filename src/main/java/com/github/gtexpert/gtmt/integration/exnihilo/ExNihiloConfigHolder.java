package com.github.gtexpert.gtmt.integration.exnihilo;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.modules.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.exnihilo")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_EXNIHILO,
        category = "ExNihilo")
public class ExNihiloConfigHolder {

    @Config.Comment({ "Replaces the original Crooks with GT Crooks or recipe.", "Affected: Wood, Iron, Gold, Diamond.",
            "Default: false" })
    public static boolean replaceCrook = false;
}
