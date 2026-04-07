# :bagel: Dough

<hr />
<p align="center">
    <a href="https://github.com/baked-libs/dough/actions">
        <img alt="Build Status" src="https://github.com/baked-libs/dough/actions/workflows/gradle.yml/badge.svg?event=push" />
    </a>
    <a href="https://javadoc.io/doc/io.github.baked-libs/dough-api">
	<img alt="javadocs" src="https://javadoc.io/badge2/io.github.baked-libs/dough-api/javadoc.svg" />
    </a>
    <a href="https://sonarcloud.io/project/overview?id=baked-libs_dough">
        <img alt="Code Coverage" src="https://sonarcloud.io/api/project_badges/measure?project=baked-libs_dough&metric=coverage" />
    </a>
    <a href="https://sonarcloud.io/project/overview?id=baked-libs_dough">
        <img alt="Maintainability" src="https://sonarcloud.io/api/project_badges/measure?project=baked-libs_dough&metric=sqale_rating" />
    </a>
</p>
<hr />

Formerly known as "cs-corelib2", dough is a very powerful library aiming to help the everyday Spigot/Plugin developer.
It is packed to the brim with useful features and APIs to use and play around with.

Dough may be more commonly known as the backbone of [Slimefun](https://github.com/Slimefun/Slimefun4).

## Build
This repository uses Gradle for local and CI builds.

```bash
./gradlew build -x test
```

## :mag: Getting Started
Dough is hosted on Maven Central (OSS Sonatype) for easy access.
Furthermore, it consists of multiple different submodules.

If you want to utilise the entirety of dough, use the artifact `dough-api`.<br>
Otherwise replace `dough-api` in the following examples with whatever module you want to import. Note that
some modules have dependencies on other modules, all modules require `dough-common` as an example.

### Adding dough via Gradle
Dough can easily be included in Gradle using `mavenCentral()`.<br />
Simply replace `[DOUGH VERSION]` with the most up to date version of dough:
![Maven Central](https://img.shields.io/maven-central/v/io.github.baked-libs/dough?label=latest%20version)

```gradle
repositories {
	mavenCentral()
}

dependencies {
	implementation 'io.github.baked-libs:dough-api:[DOUGH VERSION]'
}
```

To shadow dough and relocate it:
```gradle
plugins {
  id "com.gradleup.shadow" version "8.3.6"
}

shadowJar {
   relocate "io.github.bakedlibs.dough", "[YOUR PACKAGE].dough"
}
```
