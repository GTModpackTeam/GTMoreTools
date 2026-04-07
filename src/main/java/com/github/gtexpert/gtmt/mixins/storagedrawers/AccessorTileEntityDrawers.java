package com.github.gtexpert.gtmt.mixins.storagedrawers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

@Mixin(value = TileEntityDrawers.class, remap = false)
public interface AccessorTileEntityDrawers {

    @Invoker("getEffectiveDrawerCapacity")
    int gtmt$invokeGetEffectiveDrawerCapacity();
}
