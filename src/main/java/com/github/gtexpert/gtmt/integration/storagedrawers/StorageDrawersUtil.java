package com.github.gtexpert.gtmt.integration.storagedrawers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.MathHelper;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;

import com.github.gtexpert.gtmt.api.util.ModLog;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradeMaterialData;

public class StorageDrawersUtil {

    public static List<UpgradeMaterialData> UPGRADE_MATERIALS = new ArrayList<>();

    public static List<UpgradeMaterialData> parse(String[] entries) {
        Map<Integer, UpgradeMaterialData> check = new HashMap<>();
        List<UpgradeMaterialData> result = new ArrayList<>();

        if (entries.length == 0) {
            entries = new String[] { "gregtech:obsidian@2", "gregtech:iron@4", "gregtech:gold@8", "gregtech:diamond@16",
                    "gregtech:emerald@32" };
        }

        for (String entry : entries) {
            String[] split1 = entry.split("@", 2);
            if (split1.length != 2) {
                ModLog.logger.warn("Missing '@': {}, skipped", entry);
                continue;
            }

            String materialPart = split1[0];
            String otherPart = split1[1];

            if (!materialPart.contains(":")) {
                ModLog.logger.warn("Invalid material format (missing ':'): {}, skipped", entry);
                continue;
            }

            Material material = GregTechAPI.materialManager.getMaterial(materialPart);

            if (material == null) {
                ModLog.logger.warn("Cannot find '{}'. Skipping entry", materialPart);
                continue;
            }

            int id = material.getId();

            String[] percentSplit = otherPart.split("%", 2);

            int multiple = 1;
            try {
                multiple = Integer.parseInt(percentSplit[0]);
            } catch (NumberFormatException e) {
                ModLog.logger.warn("Invalid multiple: " + entry, e);
                continue;
            }

            int tier = 1;
            int value;
            if (percentSplit.length == 2 && !percentSplit[1].isEmpty()) {
                try {
                    value = Integer.parseInt(percentSplit[1]);
                } catch (NumberFormatException e) {
                    ModLog.logger.warn("Invalid tier: " + entry, e);
                    continue;
                }

                if (value < 0 || 8 < value) {
                    tier = MathHelper.clamp(value, 1, 8);
                    ModLog.logger.warn("Tier is out of range. Fallback to {}", tier);
                } else
                    tier = value;
            }
            if (check.containsKey(id)) {
                ModLog.logger.warn("Duplicate id: {}", id);
            }
            UpgradeMaterialData data = new UpgradeMaterialData(material, id, multiple, tier);
            check.put(id, data);
            result.add(data);
            ModLog.logger.info(
                    "Success to add UpgradeMaterialRegistry, Material: {}, meta: {}, multiplier: x{}, requiredTier: {} ({})",
                    material, id, multiple, tier, GTValues.VN[tier]);
        }
        return result;
    }
}
