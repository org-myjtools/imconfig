# Changelog


## Version 1.7.0 - 21/04/2026

### Added:

* New property definition type `map`, which describes a composite property whose sub-keys share a
  common prefix and each follow their own typed definition (entries). Supported in both YAML
  definition files (via the `entries:` block) and programmatically via
  `PropertyDefinitionBuilder.mapType(Map<String, PropertyDefinition>)`.


## Version 1.6.0 - 17/03/2026

### Added:

* New method `Config.innerKeys()` to retrieve the distinct first-level keys from the current configuration view

### Changed:

* Configuration string rendering is now sorted alphabetically for deterministic output
* README examples now document `innerKeys()` usage and dependency version `1.6.0`



## Version 1.5.1 - 24/02/2026

### Fixed:

* `PropertyDefinition.toString()` now trims leading/trailing whitespace from description and hint
* `Config.getDefinitionsToString()` now returns definitions sorted alphabetically by property name


## Version 1.5.0 - 20/02/2026

### Changed:

* Updated `commons-configuration2` from 2.11.0 to 2.13.0 (security update)


## Version 1.1.0 - 23/07/2025

### Added:

* New method `Config.loadDefinitionsFromResource`
