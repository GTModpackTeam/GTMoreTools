package com.github.gtexpert.gtmt.integration.storagedrawers.items;

import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradesMaterialRegistry;

public class StorageDrawersItems {

    public static ItemGTMaterialUpgradeStorage upgradeStorageGT;

    public static void init() {
        upgradeStorageGT = new ItemGTMaterialUpgradeStorage("upgrade_storage_gt", "upgrade_storage_gt",
                UpgradesMaterialRegistry.REGISTRY);
    }
}
