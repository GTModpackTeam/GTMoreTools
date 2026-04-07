package com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades;

import gregtech.api.unification.material.Material;

public class UpgradeMaterialData {

    private final Material material;
    private final int id;
    private final int multiple;
    private final int tier;

    public UpgradeMaterialData(Material material, int id, int multiple, int tier) {
        this.material = material;
        this.id = id;
        this.multiple = multiple;
        this.tier = tier;
    }

    public Material getMaterial() {
        return material;
    }

    public int getId() {
        return id;
    }

    public int getMultiple() {
        return multiple;
    }

    public int getTier() {
        return tier;
    }
}
