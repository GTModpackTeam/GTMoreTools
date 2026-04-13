package com.github.gtexpert.gtmt.integration.tic;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.gtexpert.gtmt.integration.tic.materials.ToolMaterialRegistrar;

/**
 * Client-side event handlers for the TiC integration.
 *
 * <p>
 * Registered only on the client via {@link TiCModule#getEventBusSubscribers()}.
 */
@SideOnly(Side.CLIENT)
public class TiCClientEvents {

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        ToolMaterialRegistrar.registerFluidModels();
    }

    /**
     * Re-inject dynamic material name translations after every resource reload.
     *
     * <p>
     * {@link net.minecraft.util.text.translation.LanguageMap#inject} entries are wiped
     * whenever the language manager reloads (F3+T or in-game language change).
     * {@link TextureStitchEvent.Post} fires after the language map has already been
     * refreshed from disk, so this is the correct point to restore the dynamic entries.
     */
    @SubscribeEvent
    public static void onTextureStitchPost(TextureStitchEvent.Post event) {
        ToolMaterialRegistrar.reinjectTranslations();
    }
}
