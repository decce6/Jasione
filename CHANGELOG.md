## 1.0.4

- Generated classes and members are now marked as synthetic (`ACC_SYNTHETIC`)
- Added support for 1.19.2 and 1.18.2

## 1.0.3

Fixed NPE when loading classes from threads whose context classloader is not the transforming classloader for the platform.

## 1.0.2

- Improved mod compatibility
  - This fixes compatibility with Illager Invasion, PTS-Deco, and various other mods
- Slightly improved performance
- Fixed analysis failure for MixinExtras wrappers
- Made analysis error logging configurable

## 1.0.1

- Fixed "module does not read unnamed module" errors
- Fixed unnecessary `COMPUTE_FRAMES` calculation on Forge and older NeoForge

## 1.0.0

- Initial release
