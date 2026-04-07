# Changelog

## 5.3.0

### Added
- Added centralized version support abstraction via `VersionAdapter`, `VersionResolver`, and `SupportLevel`.
- Added support matrix mapping:
  - `1.21.11` -> `STABLE` (`v1_21_11`)
  - `26.1.1` -> `EXPERIMENTAL` (`v26_1_1`)
- Added fallback behavior to stable adapter when an unsupported Minecraft version is detected.
- Added version-specific adapter namespaces under `versions/v1_21_11` and `versions/v26_1_1`.
- Added NMS isolation package namespaces for both `items` and `skins` adapters:
  - `nms/v1_21_Rx`
  - `nms/v26_1_Rx`
- Added startup support logging:
  - `[Dough] Minecraft Version: X`
  - `[Dough] Support Level: STABLE / EXPERIMENTAL`
  - `[Dough] Adapter Loaded: vX_X_X`

### Updated
- Migrated build from Maven to Gradle multi-module configuration.
- Updated Java toolchain target to Java 21 (`--release 21`) with forward-runtime compatibility intent for Java 25.
- Updated CI workflow to Gradle and Java 21.
- Updated JitPack build pipeline to Gradle.
- Updated API build to produce shaded `dough-api` output via Shadow plugin.
- Updated baseline API dependencies for modern server environments:
  - Paper API `1.21.11-R0.1-SNAPSHOT`
  - Spigot API `1.21.11-R0.2-SNAPSHOT`
  - Adventure API `4.24.0`

### Fixed
- Centralized version mapping and fallback so version checks are no longer scattered for latest supported targets.
- Ensured item and skin adapter resolution consumes centralized version support metadata.

### Deprecated
- Maven build descriptors (`pom.xml`) and Maven-centric CI jobs.

### Compatibility Notes
- LTS baseline: `1.21.11` (stable).
- Experimental baseline: `26.1.1` (best-effort, non-crashing fallback path).
- Slimefun-facing public API signatures were preserved; no public method removals were introduced.
