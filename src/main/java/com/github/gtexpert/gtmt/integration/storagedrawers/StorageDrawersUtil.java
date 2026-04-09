package com.github.gtexpert.gtmt.integration.storagedrawers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.MathHelper;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;

import com.github.gtexpert.gtmt.api.util.ModLog;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradeMaterialData;

public class StorageDrawersUtil {

    private static final ConfigManager config = StorageDrawers.config;

    public static List<UpgradeMaterialData> UPGRADE_MATERIALS = new ArrayList<>();

    public static List<UpgradeMaterialData> parse(String[] entries) {
        Map<Integer, UpgradeMaterialData> check = new HashMap<>();
        List<UpgradeMaterialData> result = new ArrayList<>();

        ModLog.logger.info("UpgradeMaterialData Registering was started.");

        String[] defaultEntries = new String[] {
                "gregtech:obsidian@" + config.getStorageUpgradeMultiplier(EnumUpgradeStorage.OBSIDIAN.getLevel()),
                "gregtech:iron@" + config.getStorageUpgradeMultiplier(EnumUpgradeStorage.IRON.getLevel()),
                "gregtech:gold@" + config.getStorageUpgradeMultiplier(EnumUpgradeStorage.GOLD.getLevel()),
                "gregtech:diamond@" + config.getStorageUpgradeMultiplier(EnumUpgradeStorage.DIAMOND.getLevel()),
                "gregtech:emerald@" + config.getStorageUpgradeMultiplier(EnumUpgradeStorage.EMERALD.getLevel())
        };

        String[] newEntries = prepend(entries, defaultEntries);

        for (String entry : newEntries) {
            String[] split1 = entry.split("@", 2);
            if (split1.length != 2) {
                ModLog.logger.warn("Missing '@': {}, Skipping entry.", entry);
                continue;
            }

            String materialPart = split1[0];
            String otherPart = split1[1];

            if (!materialPart.contains(":")) {
                ModLog.logger.warn("Invalid material format (missing ':'): {}. Skipping entry.", entry);
                continue;
            }

            Material material = GregTechAPI.materialManager.getMaterial(materialPart);

            if (material == null) {
                ModLog.logger.warn("Cannot find '{}'. Skipping entry", materialPart);
                continue;
            }

            if (!material.hasProperty(PropertyKey.DUST)) {
                ModLog.logger.warn("Material must have dust property. Skipping entry.");
                continue;
            }

            int id = material.getId();

            String[] percentSplit = otherPart.split("%", 2);

            int multiplier;
            try {
                multiplier = Integer.parseInt(percentSplit[0]);
            } catch (NumberFormatException e) {
                ModLog.logger.warn("Invalid multiplier: {}. Skipping entry.", entry, e);
                continue;
            }

            int tier = -1;
            int value;
            if (percentSplit.length == 2 && !percentSplit[1].isEmpty()) {
                try {
                    value = Integer.parseInt(percentSplit[1]);
                } catch (NumberFormatException e) {
                    ModLog.logger.warn("Invalid tier: {}. Skipping entry.", entry, e);
                    continue;
                }

                if (value < 0 || 8 < value) {
                    tier = MathHelper.clamp(value, 1, 8);
                    ModLog.logger.warn("Tier is out of range. Fallback to {}", tier);
                } else
                    tier = value;
            }
            if (check.containsKey(id)) {
                ModLog.logger.warn("Duplicate id: {}. Skipping entry.", id);
                continue;
            }

            UpgradeMaterialData data = new UpgradeMaterialData(material, id, multiplier, tier);
            check.put(id, data);
            result.add(data);
            if (tier == -1) {
                ModLog.logger.info(
                        "Success to add UpgradeMaterialRegistry, Material: {}, meta: {}, multiplier: x{}",
                        material, id, multiplier);
            } else {
                ModLog.logger.info(
                        "Success to add UpgradeMaterialRegistry, Material: {}, meta: {}, multiplier: x{}, requiredTier: {} ({})",
                        material, id, multiplier, tier, GTValues.VN[tier]);

            }
        }
        ModLog.logger.info("UpgradeMaterialData Registering was finished.");
        return result;
    }

    private static String[] prepend(String[] args1, String[] args2) {
        String[] result = new String[args2.length + args1.length];
        System.arraycopy(args2, 0, result, 0, args2.length);
        System.arraycopy(args1, 0, result, args2.length, args1.length);

        return result;
    }
}
