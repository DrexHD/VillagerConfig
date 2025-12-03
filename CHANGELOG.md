# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [4.5.0] - 2025-12-03
### Added
- Server to client packet for syncing custom level requirements

## [4.4.11] - 2025-10-07
### Fixed
- Villager type test command argument in 1.21–1.21.4

## [4.4.10] - 2025-10-07
### Added
- Villager type (villager biome) test command argument

## [4.4.9] - 2025-10-03
### Fixed
- Ignore invalid trade levels during trade generation

## [4.4.8] - 2025-10-02
### Fixed
- Trade ordering being inconsistent with vanilla in some cases
- Trade generation for invalid trade levels

## [4.4.7] - 2025-08-18
### Fixed
- Config path location. The config was loaded from `config/villagerconfig.json5` from v4.4.0 to v4.4.6, but should have been loaded from `config/VillagerConfig/villagerconfig.json5`. **Make sure to migrate any changes you made in the wrong file to the fixed location.**
- Log error when playing without `cloth-config`

## [4.4.6] - 2025-08-05
### Fixed
- Custom trades not working

## [4.4.5] - 2025-06-25
### Fixed
- Compatibility with NeoForge 21.1.181+

## [4.4.4] - 2025-06-23
### Fixed
- Experimental trade generation

## [4.4.3] - 2025-06-23
### Fixed
- Published NeoForge jar

## [4.4.2] - 2025-06-23
### Fixed
- Dependency management on NeoForge

## [4.4.1] - 2025-06-21
### Added
- Test commands to quickly view randomly generated trade offers

### Changed
- Improved wandering trader default file structure

### Fixed
- Set dye loot function not working

## [4.4.0] - 2025-06-18
### Added
- Neoforge support

## [4.3.5] - 2025-06-13
### Fixed
- Empty trade files in trade generator in 1.21.1 & 1.21.4
- Config saving in modmenu

## [4.3.4] - 2025-05-23
### Changed
- Use stonecutter to support 1.21.1, 1.21.4 and 1.21.5

## [4.3.3] - 2025-04-26
### Fixed
- Incorrect item components ItemCost predicate

## [4.3.2] - 2024-02-03
### Removed
- Config command

## [4.3.1] - 2024-11-23
### Added
- Experimental trade generate requirement hint

## [4.3.0] - 2024-08-18
### Added
- Traditional Chinese translations

### Changed
- Improved trade data generation

### Fixed
- Item cost component predicates not being used
- Suspicious stew effect duration for non-instant effects
- Trades loading to early

## [4.2.0] - 2024-07-29
### Changed
- Removed `trade_enchantments` field from `enchant_randomly` loot function

### Fixed
- Incomplete EmeraldsForVillagerTypeItem causing generation errors
- Stack counts exceeding maximum stack size
- Location condition checks not working

## [4.1.2] - 2023-11-23
### Fixed
- Minecraft version requirement
- Fabric API version

## [4.1.1] - 2023-11-23
### Fixed
- Item tags not working correctly

## [4.1.0] - 2023-09-30
### Changed
- Updated to 1.20.2
- `num_to_select` accepts number providers 
- Trades can have `conditions`
- `villagerconfig:enchant_randomly` has `min_level` and `max_level` options

## [4.0.3] - 2023-09-15
### Fixed
- `exploration_map` function not working

## [4.0.2] - 2023-07-09
### Fixed
- Villager conversion chance config option not working 

## [4.0.1] - 2023-03-01
### Fixed
- Immediate crash on startup

## [4.0.0] - 2023-02-26
### Added
- Major trade schema rewrite (not backwards compatible)
- Config2Brigadier for command config

### Changed
- Use `fabric-resource-loader-v0` api instead of mixins
- Mappings to official mappings

## [3.0.5] - 2022-08-10
### Fixed
- Mod incompatibility with `carpet-fixes` and `rug`

### Changed
- Improved `VC_EnchantBookFactory`

## [3.0.4] - 2022-07-25
### Fixed
- Mod incompatibility with `Origins: Classes`

## [3.0.3] - 2022-07-10
### Fixed
- Incorrectly calculated villager level requirements

## [3.0.2] - 2022-06-21
### Changed
- Updated to 1.19

## [3.0.1] - 2022-03-03
### Added
- Trade validator
- Behaviour pack support
- Infinite trade setting

### Changed
- Updated to 1.18

### Fixed
- Client crash (#23)
- Old trade mechanics (#22)

## [3.0.0] - 2021-12-04
### Added
- Data driven trades
