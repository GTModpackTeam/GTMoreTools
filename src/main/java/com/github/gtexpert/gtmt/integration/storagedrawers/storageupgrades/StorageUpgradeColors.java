package com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gregtech.api.unification.material.Material;

import com.github.gtexpert.gtmt.integration.storagedrawers.items.ItemGTMaterialUpgradeStorage;
import com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems;

@SideOnly(Side.CLIENT)
public class StorageUpgradeColors {

    public static void init() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                (stack, tintIndex) -> {
                    if (!(stack.getItem() instanceof ItemGTMaterialUpgradeStorage item)) {
                        return 0xFFFFFFFF;
                    }

                    if (tintIndex % 2 == 0) {
                        return 0xFFFFFFFF;
                    }

                    UpgradeMaterialData data = item.getUpgradeMaterialData(stack);

                    if (data == null) {
                        return 0xFFFFFFFF;
                    }
                    Material material = item.getMaterial(stack);

                    return material.getMaterialRGB();
                },
                StorageDrawersItems.upgradeStorageGT);
    }
}
