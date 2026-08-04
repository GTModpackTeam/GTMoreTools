# v1.5.0
## Ex Nihiro: Creatio Integration
- _Crooking Compatibility_
  - Added GT-style crooks.
- _Hammering Compatibility_
  - CEu Hard hammers can now be used as Ex Nihilo hammers.
- _Sieving Compatibility_
  - :warning:**_WARNING_**:warning: This feature is temporary and will be removed once GTCEu supports it natively.
  - Added Steam and Electric Sieve.
  - Added Basalt, Granites, Marble pebble.
  - Added GT-material Ore Chunk.

* * *

# v1.4.0
## New: Tools Module
- Per-item tool restrictions (custom max durability), JEI hiding, and recipe removal — all configurable via a single cfg.

* * *

# v1.3.3
## Chisel Integration
- Improved Auto Chisel processing speed.

* * *

# v1.3.2

## Storage Drawers Integration
- Fixed `stickLong` recipe for materials without `Ingot` or `Gem`.

* * *

# v1.3.1
## Tinkers' Construct Integration
- Fixed TiC main-hand + GT off-hand combination not using the GT off-hand tool when it is the appropriate tool for the block
- Fixed duplicate durability loss on both GT and TiC cross-hand combinations

* * *

# v1.3.0
## New: Storage Drawers Integration
- GT materials can now be used to craft _Storage Upgrades_.
- Craftable materials, multipliers, and related settings can be configured via cfg.
- Original materials (Obsidian/Iron/Gold/Diamond/Emerald) are always included.

* * *

# v1.2.4
## Tinkers' Construct Integration
- Fixed GT main-hand + TiC off-hand combination dropping items twice

* * *

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
