package com.github.gtexpert.gtmt.mixins;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraftforge.fml.common.Loader;

import com.google.common.collect.ImmutableMap;

import com.github.gtexpert.gtmt.api.ModValues;
import com.github.gtexpert.gtmt.api.util.ModLog;
import com.github.gtexpert.gtmt.api.util.Mods;

import zone.rong.mixinbooter.ILateMixinLoader;

@SuppressWarnings("unused")
public class ModMixinLoader implements ILateMixinLoader {

    public static final Map<String, Boolean> modMixinsConfig = new ImmutableMap.Builder<String, Boolean>()
            .put(Mods.Names.BETTER_BUILDERS_WANDS, true)
            .put(Mods.Names.CHISEL, true)
            .put(Mods.Names.TINKERS_CONSTRUCT, true)
            .build();

    @SuppressWarnings("SimplifyStreamApiCallChains")
    @Override
    public List<String> getMixinConfigs() {
        return modMixinsConfig.keySet().stream().map(mod -> "mixins." + ModValues.MODID + "." + mod + ".json")
                .collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");

        if (parts.length != 4 && parts.length != 5) {
            ModLog.logger.fatal("Mixin Config Check Failed! Invalid Length.");
            ModLog.logger.fatal("Mixin Config: {}", mixinConfig);
            return true;
        }

        if (!Objects.equals(parts[1], ModValues.MODID)) {
            ModLog.logger.error("Non GTExpertCore Mixin Found in Mixin Queue. This is probably an error. Skipping...");
            ModLog.logger.error("Mixin Config: {}", mixinConfig);
            return true;
        }

        if (!Loader.isModLoaded(parts[2])) {
            ModLog.logger.error(
                    "Mod '{}' is not loaded. If this is a normal GTExpertCore instance, this is probably an error.",
                    parts[2]);
            ModLog.logger.error("Not Loading Mixin Config {}", mixinConfig);
            return false;
        }

        if (!modMixinsConfig.containsKey(parts[2]) || !modMixinsConfig.get(parts[2])) {
            ModLog.logger.info("Integration for Mod '{}' is not enabled, or does not exist.", parts[2]);
            ModLog.logger.info("Not Loading Mixin Config {}", mixinConfig);
            return false;
        }

        return true;
    }
}
