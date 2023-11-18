# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [4.1.1] - 2023-11-23
- Fixed item tags not working correctly

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
## Added
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
