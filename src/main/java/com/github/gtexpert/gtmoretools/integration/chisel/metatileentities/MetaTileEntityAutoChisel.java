package com.github.gtexpert.gtmoretools.integration.chisel.metatileentities;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;

import com.github.gtexpert.gtmoretools.api.capability.SingleblockRecipeLogicNoCache;

public class MetaTileEntityAutoChisel extends SimpleMachineMetaTileEntity {

    public MetaTileEntityAutoChisel(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer,
                                    int tier, boolean hasFrontFacing, Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, recipeMap, renderer, tier, hasFrontFacing, tankScalingFunction);
        new AutoChiselRecipeLogic(this, recipeMap, () -> this.energyContainer);
        this.getRecipeLogic().setParallelLimit(Math.max((int) Math.pow(4, (tier - GTValues.EV)) / 2, 1));
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAutoChisel(metaTileEntityId, workable.getRecipeMap(), renderer, getTier(),
                hasFrontFacing(), getTankScalingFunction());
    }

    private static class AutoChiselRecipeLogic extends SingleblockRecipeLogicNoCache {

        public AutoChiselRecipeLogic(MetaTileEntity tileEntity, RecipeMap<?> recipeMap,
                                     Supplier<IEnergyContainer> energyContainer) {
            super(tileEntity, recipeMap, energyContainer);
        }
    }
}
