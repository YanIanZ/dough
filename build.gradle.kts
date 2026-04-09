import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java-library")
    id("jacoco")
    id("com.gradleup.shadow") version "8.3.6" apply false
}

group = "com.github.YanIanZ.dough"
version = providers.gradleProperty("projectVersion").orElse("1.4.1").get()

val paperApiVersion = "1.21.11-R0.1-SNAPSHOT"
val spigotApiVersion = "1.21.11-R0.2-SNAPSHOT"
val adventureApiVersion = "4.24.0"
val adventureMiniMessageVersion = "4.24.0"
val jsr305Version = "3.0.2"
val junitBomVersion = "5.10.3"
val mockitoVersion = "4.11.0"
val mockBukkitVersion = "2.85.2"
val paperLibVersion = "1.0.7"
val commonsLangVersion = "2.6"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://libraries.minecraft.net/")
        maven("https://jitpack.io/")
        maven("https://maven.playpro.com/")
        maven("https://raw.githubusercontent.com/FabioZumbi12/RedProtect/mvn-repo/")
        maven("https://ci.ender.zone/plugin/repository/everything/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/") {
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        maven("https://repo.panda-lang.org/releases")
        maven("https://www.iani.de/nexus/content/repositories/snapshots/")
        maven("https://repo.william278.net/snapshots/")
    }

    configurations.configureEach {
        resolutionStrategy.capabilitiesResolution.withCapability("org.spigotmc:spigot-api") {
            select("io.papermc.paper:paper-api:$paperApiVersion")
        }
        resolutionStrategy.capabilitiesResolution.withCapability("com.destroystokyo.paper:paper-api") {
            select("io.papermc.paper:paper-api:$paperApiVersion")
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
        withJavadocJar()
    }

    // Note: dough-api handles its own Maven publication via its own afterEvaluate block
    afterEvaluate {
        if (project.name != "dough-api") {
            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])
                    }
                }
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }

    dependencies {
        if (project.name == "dough-protection") {
            compileOnly("org.spigotmc:spigot-api:$spigotApiVersion")
            compileOnly("net.kyori:adventure-api:$adventureApiVersion")
        } else {
            compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
            compileOnly("org.spigotmc:spigot-api:$spigotApiVersion")
            compileOnly("net.kyori:adventure-api:$adventureApiVersion")
            compileOnly("net.kyori:adventure-text-serializer-plain:$adventureApiVersion")
        }
        compileOnly("com.google.code.findbugs:jsr305:$jsr305Version")
        compileOnly("commons-lang:commons-lang:$commonsLangVersion")
        compileOnly("io.papermc:paperlib:$paperLibVersion")

        testImplementation(platform("org.junit:junit-bom:$junitBomVersion"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-core:$mockitoVersion")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("com.github.seeseemelk:MockBukkit-v1.18:$mockBukkitVersion") {
            exclude(group = "org.jetbrains", module = "annotations")
        }
    }

    tasks.test {
        useJUnitPlatform()
        failOnNoDiscoveredTests.set(false)
    }
}

project(":dough-api") {
    // Override publication to explicitly publish the shadow JAR (fat bundle with all modules)
    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    artifact(tasks.named<ShadowJar>("shadowJar"))
                }
            }
        }
    }

    apply(plugin = "com.gradleup.shadow")

    configurations.matching { it.name == "compileOnly" || it.name == "compileOnlyApi" }.configureEach {
        withDependencies {
            forEach { dependency ->
                if (dependency is org.gradle.api.artifacts.ModuleDependency) {
                    dependency.isTransitive = false
                }
            }
        }
    }

    dependencies {
        implementation(project(":dough-common"))
        implementation(project(":dough-config"))
        implementation(project(":dough-chat"))
        implementation(project(":dough-data"))
        implementation(project(":dough-skins"))
        implementation(project(":dough-items"))
        implementation(project(":dough-inventories"))
        implementation(project(":dough-protection"))
        implementation(project(":dough-recipes"))
        implementation(project(":dough-updater"))
        implementation(project(":dough-scheduling"))
        implementation(project(":dough-gui"))
        implementation(project(":dough-storage"))
        implementation("commons-lang:commons-lang:$commonsLangVersion")

        compileOnly("com.sk89q.worldedit:worldedit-core:7.2.17")
        compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.17")
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
        compileOnly("com.github.elBukkit:PreciousStones:1.17.2")
        compileOnly("net.coreprotect:coreprotect:21.3")
        compileOnly("de.diddiz:logblock:1.17.0.0-SNAPSHOT")
        compileOnly("com.github.marcelo-mason:SimpleClans:7c3db52796")
        compileOnly("com.github.TechFortress:GriefPrevention:16.18.2")
        compileOnly("com.github.dmulloy2.LWC:lwc:master-SNAPSHOT")
        compileOnly("me.lucko:helper:5.6.14")
        compileOnly("com.massivecraft:Factions:1.6.9.5-4.1.4-STABLE")
        compileOnly("com.github.LlmDl:Towny:1b86d017c5")
        compileOnly("com.github.fubira:Lockette:9dac96e8f8")
        compileOnly("com.plotsquared:PlotSquared-Core:6.11.1") {
            exclude(group = "org.projectlombok", module = "lombok")
        }
        compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Core:7.7.3")
        compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Spigot:7.7.3")
        compileOnly("world.bentobox:bentobox:1.20.1-SNAPSHOT")
        compileOnly("nl.rutgerkok:blocklocker:1.10.4")
        compileOnly("com.github.angeschossen:LandsAPI:6.29.12")
        compileOnly("com.github.angeschossen:ChestProtectAPI:3.9.1")
        compileOnly("net.dzikoysk.funnyguilds:plugin:4.12.0") {
            exclude(group = "com.github.PikaMug", module = "LocaleLib")
        }
        compileOnly("com.github.WiIIiam278:HuskTowns:1.7")
        compileOnly("de.epiceric:ShopChest:1.13-SNAPSHOT")
        compileOnly("org.popcraft:bolt-bukkit:1.0.580")
        compileOnly("org.popcraft:bolt-common:1.0.580")
    }

    tasks.named<Jar>("jar") {
        enabled = false
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        exclude("META-INF/*")
    }

    tasks.named("assemble") {
        dependsOn(tasks.named("shadowJar"))
    }
}

project(":dough-common") {
    dependencies {
        implementation("io.papermc:paperlib:$paperLibVersion")
        compileOnly("net.kyori:adventure-text-minimessage:$adventureMiniMessageVersion")
    }
}

project(":dough-config") {
    dependencies {
        compileOnly(project(":dough-common"))
        implementation(project(":dough-common"))
    }
}

project(":dough-chat") {
    dependencies {
        compileOnly(project(":dough-common"))
        compileOnly(project(":dough-scheduling"))
        implementation(project(":dough-scheduling"))
    }
}

project(":dough-data") {
    dependencies {
        compileOnly(project(":dough-common"))
    }
}

project(":dough-skins") {
    dependencies {
        compileOnly(project(":dough-common"))
        compileOnly(project(":dough-scheduling"))
    }
}

project(":dough-items") {
    dependencies {
        compileOnly(project(":dough-common"))
        implementation(project(":dough-common"))
    }
}

project(":dough-inventories") {
    dependencies {
        implementation(project(":dough-common"))
        implementation(project(":dough-items"))
    }
}

project(":dough-protection") {
    val plotsquaredVersion = "7.3.6"

    dependencies {
        compileOnly(project(":dough-common"))

        compileOnly("com.sk89q.worldedit:worldedit-core:7.2.17") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.17") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.elBukkit:PreciousStones:1.17.2") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("net.coreprotect:coreprotect:21.3") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("de.diddiz:logblock:1.17.0.0-SNAPSHOT") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.marcelo-mason:SimpleClans:7c3db52796") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.GriefPrevention:GriefPrevention:16.18.2") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.dmulloy2.LWC:lwc:master-SNAPSHOT") {
            isTransitive = false
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("me.lucko:helper:5.6.14") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.massivecraft:Factions:1.6.9.5-4.1.4-STABLE") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.LlmDl:Towny:1b86d017c5") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.fubira:Lockette:9dac96e8f8") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.intellectualsites.plotsquared:plotsquared-core:$plotsquaredVersion") {
            exclude(group = "org.projectlombok", module = "lombok")
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit:$plotsquaredVersion") {
            exclude(group = "*", module = "plotsquared-core")
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Core:7.7.3") {
            isTransitive = false
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Spigot:7.7.3") {
            isTransitive = false
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("world.bentobox:bentobox:1.20.1-SNAPSHOT") {
            isTransitive = false
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("nl.rutgerkok:blocklocker:1.10.4") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.angeschossen:LandsAPI:6.29.12") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("com.github.angeschossen:ChestProtectAPI:3.9.1") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("net.dzikoysk.funnyguilds:plugin:4.12.0") {
            exclude(group = "*", module = "bukkit")
            exclude(group = "com.github.PikaMug", module = "LocaleLib")
        }
        compileOnly("net.william278.husktowns:husktowns-bukkit:3.0-988161b") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.0.2-e60150d") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("de.epiceric:ShopChest:1.13-SNAPSHOT") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("org.popcraft:bolt-bukkit:1.0.580") {
            exclude(group = "*", module = "bukkit")
        }
        compileOnly("org.popcraft:bolt-common:1.0.580")
    }
}

project(":dough-recipes") {
    dependencies {
        compileOnly(project(":dough-common"))
    }
}

project(":dough-updater") {
    dependencies {
        compileOnly(project(":dough-common"))
        compileOnly(project(":dough-scheduling"))
    }
}

project(":dough-scheduling") {
    dependencies {
        compileOnly(project(":dough-common"))
    }
}

project(":dough-gui") {
    dependencies {
        compileOnly(project(":dough-common"))
        compileOnly(project(":dough-scheduling"))
        implementation(project(":dough-scheduling"))
    }
}

project(":dough-storage") {
    dependencies {
        compileOnly(project(":dough-common"))
    }
}
