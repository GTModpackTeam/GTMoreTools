package com.github.gtexpert.gtmt.integration.tic.traits;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Registry for all GTMT custom TiC traits.
 *
 * <p>
 * Static traits are auto-registered with {@code TinkerRegistry} on class initialisation.
 * Their display names and descriptions live in the mod's lang files
 * ({@code assets/gtmt/lang/}) under {@code modifier.<id>.name} / {@code modifier.<id>.desc}.
 *
 * <p>
 * Note: undead bonus-damage is handled by TiC's built-in {@code TinkerTraits#holy};
 * this class only defines traits that have no TiC equivalent.
 *
 * <p>
 * Dynamic enchantment traits are created on demand via
 * {@link #getOrCreateEnchantmentTrait(Enchantment, int)} and their translations are injected
 * at runtime because the set of enchantments is not known at compile time.
 */
public final class GTMTTraits {

    /** Mining speed +30 % in the Nether — blast temperature ≥ 2500 K. */
    public static final AbstractTrait HEAT_RESISTANT = new TraitHeatResistant();

    /** 15 % chance to negate durability loss — non-unbreakable materials with durability ≥ 2000. */
    public static final AbstractTrait ANTI_CORROSION = new TraitAntiCorrosion();

    /** Applies Slowness on hit — blast temperature in [1750, 2500) K (vacuum-freezer processed). */
    public static final AbstractTrait CRYOGENIC = new TraitCryogenic();

    /** Knockback +50 % — materials with attack damage ≥ 10. */
    public static final AbstractTrait HEAVY_BLOW = new TraitHeavyBlow();

    /** Zero durability loss — GT {@code isUnbreakable} flag. */
    public static final AbstractTrait UNBREAKABLE = new TraitUnbreakable();

    private static final Map<String, AbstractTrait> enchantmentTraitCache = new HashMap<>();

    private GTMTTraits() {}

    /**
     * Returns a TiC trait that applies the given vanilla enchantment to tools.
     * Instances are cached by {@code "<enchantment>_<level>"} to avoid duplicate
     * registrations with {@code TinkerRegistry}.
     * Display name and description are provided directly by {@link TraitEnchantment}
     * via its overridden {@code getLocalizedName()} / {@code getLocalizedDesc()} methods.
     */
    public static AbstractTrait getOrCreateEnchantmentTrait(Enchantment enchantment, int level) {
        String id = "gtmt_ench_" + enchantment.getRegistryName().getPath() +
                (level > 1 ? "_" + level : "");
        return enchantmentTraitCache.computeIfAbsent(id, k -> {
            int color = enchantment.type != null ? enchantment.type.ordinal() * 0x112233 + 0x4488BB : 0xFFD700;
            return new TraitEnchantment(id, color, enchantment, level);
        });
    }
}
