package com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import gregtech.api.unification.material.Material;

public class UpgradesMaterialRegistry {

    private final Map<Integer, UpgradeMaterialData> map = new TreeMap<>();
    public static final UpgradesMaterialRegistry REGISTRY = new UpgradesMaterialRegistry();

    private UpgradesMaterialRegistry() {}

    public void put(Material material, int id, int multiple, int tier) {
        map.put(id, new UpgradeMaterialData(material, id, multiple, tier));
    }

    public void remove(int id) {
        map.remove(id);
    }

    public UpgradeMaterialData getById(int id) {
        return map.get(id);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Collection<UpgradeMaterialData> values() {
        return map.values();
    }
}
