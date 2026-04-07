package com.github.gtexpert.gtmt.integration.storagedrawers.items;

import static com.github.gtexpert.gtmt.integration.storagedrawers.items.StorageDrawersItems.upgradeStorageGT;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;

import gregtech.api.unification.material.Material;

import com.github.gtexpert.gtmt.integration.storagedrawers.StorageDrawersUtil;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.IGTMaterialStorageUpgrade;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradeMaterialData;
import com.github.gtexpert.gtmt.integration.storagedrawers.storageupgrades.UpgradesMaterialRegistry;

public class ItemGTMaterialUpgradeStorage extends ItemUpgrade implements IGTMaterialStorageUpgrade {

    private final UpgradesMaterialRegistry registry;

    public ItemGTMaterialUpgradeStorage(String registryName, String unlocalizedName,
                                        UpgradesMaterialRegistry registry) {
        super(registryName, unlocalizedName);

        this.registry = registry;
        setMaxDamage(0);
        setHasSubtypes(true);
        setAllowMultiple(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    public UpgradeMaterialData getUpgradeMaterialData(ItemStack stack) {
        return UpgradesMaterialRegistry.REGISTRY.getById(stack.getMetadata());
    }

    public Material getMaterial(ItemStack stack) {
        return getUpgradeMaterialData(stack).getMaterial();
    }

    @Override
    public int getStorageMultiplier(ItemStack stack) {
        return getUpgradeMaterialData(stack).getMultiple();
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        ModelLoader.setCustomMeshDefinition(upgradeStorageGT, stack -> getModelLocation());
        ModelLoader.registerItemVariants(upgradeStorageGT, getModelLocation());
    }

    @SideOnly(Side.CLIENT)
    public static ModelResourceLocation getModelLocation() {
        return new ModelResourceLocation(upgradeStorageGT.getRegistryName(), "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @NotNull String getItemStackDisplayName(@NotNull ItemStack stack) {
        UpgradeMaterialData data = getUpgradeMaterialData(stack);
        if (data == null) {
            return super.getItemStackDisplayName(stack);
        }

        String materialName = getMaterial(stack).getUnlocalizedName();
        return I18n.format("gtmt.item.upgrade_storage.name", I18n.format(materialName));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> list,
                               ITooltipFlag advanced) {
        UpgradeMaterialData data = getUpgradeMaterialData(stack);
        if (data != null) {
            int multi = data.getMultiple();
            list.add(I18n.format("storagedrawers.upgrade.description", multi));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@NotNull CreativeTabs creativeTabs, @NotNull NonNullList<ItemStack> list) {
        if (isInCreativeTab(creativeTabs)) {
            for (UpgradeMaterialData data : StorageDrawersUtil.UPGRADE_MATERIALS)
                list.add(new ItemStack(this, 1, data.getId()));
        }
    }
}
