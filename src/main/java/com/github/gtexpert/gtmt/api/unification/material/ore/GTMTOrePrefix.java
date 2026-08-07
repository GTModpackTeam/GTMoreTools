package com.github.gtexpert.gtmt.api.unification.material.ore;

import static gregtech.api.unification.ore.OrePrefix.Conditions.hasOreProperty;
import static gregtech.api.unification.ore.OrePrefix.Flags.ENABLE_UNIFICATION;

import gregtech.api.unification.ore.OrePrefix;

import com.github.gtexpert.gtmt.api.unification.material.info.GTMTMaterialIconType;

public class GTMTOrePrefix {

    // Ex Nihilo: Creatio

    public static final OrePrefix oreChunk = new OrePrefix("oreChunk", -1, null, GTMTMaterialIconType.oreChunk,
            ENABLE_UNIFICATION, hasOreProperty);
    public static final OrePrefix oreEnderChunk = new OrePrefix("oreEnderChunk", -1, null,
            GTMTMaterialIconType.oreEnderChunk, ENABLE_UNIFICATION, hasOreProperty);
    public static final OrePrefix oreNetherChunk = new OrePrefix("oreNetherChunk", -1, null,
            GTMTMaterialIconType.oreNetherChunk, ENABLE_UNIFICATION, hasOreProperty);
    public static final OrePrefix oreSandyChunk = new OrePrefix("oreSandyChunk", -1, null,
            GTMTMaterialIconType.oreSandyChunk, ENABLE_UNIFICATION, hasOreProperty);
}
