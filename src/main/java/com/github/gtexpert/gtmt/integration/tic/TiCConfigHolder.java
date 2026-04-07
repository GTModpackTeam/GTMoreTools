package com.github.gtexpert.gtmt.integration.tic;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.modules.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.tic")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_TIC,
        category = "TinkerConstruct")
public class TiCConfigHolder {

    @Config.Comment({
            "Allow GT ore blocks and crushed ore variants to be melted in the TiC Smeltery.",
            "Processed forms (dust, ingot, nugget, gem) are always allowed.",
            "Default: true"
    })
    public static boolean smelteryOreMelting = true;

    @Config.Comment({
            "Allow raw ore blocks melted in the Smeltery to yield double ingots",
            "(matching TiC's default oreToIngotRatio behaviour).",
            "When false, ores melt to exactly 1x ingot value — the same as direct furnace smelting.",
            "Disabling this preserves GregTech's ore-processing progression.",
            "Has no effect when smelteryOreMelting is false.",
            "Default: false"
    })
    public static boolean smelteryOreDoubling = false;
}
