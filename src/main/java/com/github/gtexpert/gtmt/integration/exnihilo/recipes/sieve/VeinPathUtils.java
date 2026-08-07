package com.github.gtexpert.gtmt.integration.exnihilo.recipes.sieve;

import java.util.Locale;

/**
 * Utility methods for handling GTCEu world-generation definition paths.
 *
 * <p>
 * All paths are normalized relative to
 * {@code config/gregtech/worldgen/vein/}. The {@code .json} extension may be
 * omitted by users.
 * </p>
 */
public final class VeinPathUtils {

    private static final String CONFIG_PREFIX = "config/gregtech/worldgen/vein/";
    private static final String WORLDGEN_PREFIX = "gregtech/worldgen/vein/";
    private static final String ROOT_GROUP = "<root>";

    private VeinPathUtils() {}

    /**
     * Normalizes a configured vein path.
     *
     * @param value a full or relative vein path
     * @return a lower-case, slash-separated path ending in {@code .json}
     * @throws IllegalArgumentException if the path is null or empty
     */
    public static String normalize(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Vein path cannot be null");
        }

        String result = value.trim().replace('\\', '/');
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Vein path cannot be empty");
        }

        while (result.startsWith("/")) {
            result = result.substring(1);
        }

        result = removePrefixIgnoreCase(result, CONFIG_PREFIX);
        result = removePrefixIgnoreCase(result, WORLDGEN_PREFIX);

        while (result.startsWith("/")) {
            result = result.substring(1);
        }

        if (!result.toLowerCase(Locale.ROOT).endsWith(".json")) {
            result += ".json";
        }

        return result.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the first directory below the GTCEu vein root.
     *
     * <p>
     * For example, {@code overworld/copper.json} belongs to the
     * {@code overworld} group. Files placed directly in the vein root belong
     * to {@code <root>}.
     * </p>
     *
     * @param normalizedPath a path returned by {@link #normalize(String)}
     * @return the top-level vein group
     */
    public static String getTopLevelGroup(String normalizedPath) {
        int separator = normalizedPath.indexOf('/');
        return separator < 0 ? ROOT_GROUP : normalizedPath.substring(0, separator);
    }

    private static String removePrefixIgnoreCase(String value, String prefix) {
        return value.regionMatches(true, 0, prefix, 0, prefix.length()) ? value.substring(prefix.length()) : value;
    }
}
