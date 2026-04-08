# v1.2.3
## Tinkers' Construct Integration
- GT tool in main-hand + TiC tool in off-hand (and vice versa) now mines blocks using the off-hand tool
  - Mining speed is taken from the off-hand tool
  - Blocks drop items even when the main-hand tool has insufficient harvest level, using the off-hand tool
  - HUD mods (e.g. HWYLA) now show the correct can-harvest indicator (✓/×) based on the off-hand tool

* * *

# v1.2.2
## Tinkers' Construct Integration
- GT material fluids are now registered as TiC Smeltery melting recipes (ingot, nugget, block, dust, gem families always; ore/crushed variants behind `smelteryOreMelting` config). Raw ore doubling is disabled by default (`smelteryOreDoubling=false`) to preserve GT ore-processing progression.
- New traits: **Piercer** (bonus damage vs armored targets, GT harvest level ≥ 5) and **Moonlit** (+50% mining speed and +3 damage at night, precious-metal bearing materials)
- Added `integration.tic.api` package exposing a stable API for addon mods:
  - `TraitRegistry` — register custom composition-based and property-based trait assignment rules
  - `HarvestLevels` — register custom display names for harvest levels above Cobalt (5+)
  - `SmelteryHelper` — register additional Smeltery melting recipes for custom GT materials
- All hardcoded trait conditions (Silver→Holy, precious metals→Moonlit, blast temp tiers, durability, etc.) are now driven through `TraitRegistry` and can be extended or supplemented by other mods

* * *

# v1.2.1
## Tinkers' Construct Integration
- Traits are automatically assigned from GT material properties (no hardcoded material lists):
    - Holy (TiC built-in, Silver-bearing alloys), Heat Resistant (blast temp ≥ 2500 K), Cryogenic (vacuum-freezer processed, blast temp 1750–2499 K),
      Anti-Corrosion (non-unbreakable, durability ≥ 2000), Heavy Blow (attack ≥ 10), Magnetic (TiC built-in, GT isMagnetic), Unbreakable (GT isUnbreakable)
- GT ToolProperty enchantments are automatically applied to TiC parts as traits.
- Harvest levels beyond Cobalt (GT level 5+) are now registered as distinct TiC harvest levels.
- Arrow Shaft stats are now registered; GT bolt items are supported as TiC part items.
- If a GT material shares a name with an existing TiC material (e.g. Flint, Diamond), stats are merged by taking the better value per stat.
- Custom trait tooltips follow TiC's native display format (italic flavor text + effect description).

* * *

# v1.2.0
## New: Tinkers' Construct Integration
- CEu tool materials are now registered as TiC materials with Head, Handle, Extra, and Bow stats.
- Material names are inherited from CEu's localization.
- Molten fluid and smeltery casting/melting support for CEu materials.

* * *

# v1.1.2
## Better Builder's Wands Integration
- Fix BBW block preview showing on all GT tools.

* * *

# v1.1.1
## Chisel Integration
- Fix aluminum recipe conflicts with Chisel.

* * *

# v1.1.0
- Fix modid.

* * *

# v1.0.0
- First Release.
