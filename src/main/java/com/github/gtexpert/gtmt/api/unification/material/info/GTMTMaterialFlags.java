package com.github.gtexpert.gtmt.api.unification.material.info;

import java.util.HashSet;
import java.util.Set;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;

import com.github.gtexpert.gtmt.api.util.ModLog;
import com.github.gtexpert.gtmt.integration.storagedrawers.StorageDrawersConfigHolder;

public class GTMTMaterialFlags {

    public static void integrationStorageDrawers() {
        Set<Material> materials = new HashSet<>();
        materials.add(Materials.Obsidian);
        materials.add(Materials.Iron);
        materials.add(Materials.Gold);
        materials.add(Materials.Diamond);
        materials.add(Materials.Emerald);

        for (String str : StorageDrawersConfigHolder.upgradeMaterials) {
            if (str.isEmpty()) continue;

            String[] split1 = str.split("@", 2);

            if (split1.length != 2) {
                continue;

            }
            String materialPart = split1[0];

            if (!materialPart.contains(":")) {
                continue;
            }

            Material material = GregTechAPI.materialManager.getMaterial(materialPart);

            if (material == null) {
                continue;
            }

            if (!material.hasProperty(PropertyKey.DUST)) {
                ModLog.logger.warn("Material must have dust property. Skipping entry.");
            }
            materials.add(material);
        }

        for (Material material : materials) {
            if (!material.hasFlag(MaterialFlags.GENERATE_PLATE)) material.addFlags(MaterialFlags.GENERATE_PLATE);
            if (!material.hasFlag(MaterialFlags.GENERATE_ROD)) material.addFlags(MaterialFlags.GENERATE_ROD);
            if (!material.hasFlag(MaterialFlags.GENERATE_LONG_ROD)) material.addFlags(MaterialFlags.GENERATE_LONG_ROD);
            if (!material.hasFlag(MaterialFlags.GENERATE_BOLT_SCREW))
                material.addFlags(MaterialFlags.GENERATE_BOLT_SCREW);
        }
    }
}
