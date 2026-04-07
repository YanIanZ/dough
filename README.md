# :bagel: Dough

<hr />
<p align="center">
    <a href="https://github.com/YanIanZ/dough/actions">
        <img alt="Build Status" src="https://github.com/YanIanZ/dough/actions/workflows/gradle.yml/badge.svg?event=push" />
    </a>
    <a href="https://jitpack.io/#YanIanZ/dough">
	<img alt="jitpack" src="https://jitpack.io/v/YanIanZ/dough.svg" />
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
Dough is hosted on JitPack for easy access from this fork.
Furthermore, it consists of multiple different submodules.

If you want to utilise the entirety of dough, use the artifact `dough-api`.<br>
Otherwise replace `dough-api` in the following examples with whatever module you want to import. Note that
some modules have dependencies on other modules, all modules require `dough-common` as an example.

### Adding dough via Gradle
Dough can easily be included in Gradle using `jitpack.io`.<br />
Simply replace `[DOUGH VERSION]` with the most up to date version branch or tag of dough:
![JitPack](https://img.shields.io/jitpack/v/github/YanIanZ/dough?label=latest%20version)

```gradle
repositories {
	maven("https://jitpack.io")
}

dependencies {
	implementation 'com.github.YanIanZ.dough:dough-api:[DOUGH VERSION]'
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
