@file:Suppress("UnstableApiUsage")

import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("net.researchgate.release") version "3.1.0"
}

group = "io.github.f-cramer.gradle"
version = properties["version"]

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/") }
}

val jasperreportsVersion = "7.0.2"

dependencies {
    compileOnly(group = "net.sf.jasperreports", name = "jasperreports", version = jasperreportsVersion)

    testCompileOnly(group = "net.sf.jasperreports", name = "jasperreports", version = jasperreportsVersion)
    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = "0.28.1")
    testImplementation(gradleTestKit())
}

ktlint {
    enableExperimentalRules.set(true)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.HTML)
        reporter(ReporterType.CHECKSTYLE)
    }
}

detekt {
    buildUponDefaultConfig = true
    config.from(".config/detekt.yml")
}

testing.suites.named<JvmTestSuite>("test") {
    useJUnitJupiter()
    targets {
        all {
            testTask.configure {
                enabled = false
            }
        }
    }
}

val javaVersions = listOf(
    8,
    17,
    21,
    23,
)

val jasperreportsVersions = listOf(
    jasperreportsVersion,
    "6.18.1",
    "6.19.1",
    "6.20.6",
    "6.21.4",
    "7.0.1",
)

for (javaVersion in javaVersions) {
    for (jasperreportsVersion in jasperreportsVersions) {
        testing.suites.create<JvmTestSuite>("test-jdk$javaVersion-jasperreports$jasperreportsVersion") {
            useJUnitJupiter()
            dependencies {
                implementation("net.sf.jasperreports:jasperreports:$jasperreportsVersion")
                if (!jasperreportsVersion.startsWith("6.")) {
                    implementation("net.sf.jasperreports:jasperreports-jdt:$jasperreportsVersion")
                }
                runtimeOnly(sourceSets.test.get().runtimeClasspath)
                compileOnly(sourceSets.test.get().compileClasspath)
                annotationProcessor(sourceSets.test.get().annotationProcessorPath)
            }
            sources {
                kotlin.setSrcDirs(listOf("src/test/kotlin"))
                resources.setSrcDirs(listOf("src/test/resources"))
            }
            targets.configureEach {
                testTask.configure {
                    javaLauncher.set(
                        javaToolchains.launcherFor {
                            languageVersion.set(JavaLanguageVersion.of(javaVersion))
                        },
                    )
                    systemProperty("jasperreports.version", jasperreportsVersion)
                }
            }
        }

        tasks.named("check") {
            dependsOn(testing.suites)
        }
    }
}

gradlePlugin {
    website.set("https://github.com/f-cramer/jasperreports-gradle-plugin")
    vcsUrl.set(website)
    plugins {
        create("jasperreportsPlugin") {
            id = "io.github.f-cramer.jasperreports"
            displayName = "JasperReports Gradle Plugin"
            description = "A plugin for compiling JasperReports design files"
            implementationClass = "com.github.fcramer.gradle.jasperreports.JasperReportsPlugin"
            tags.add("jasperreports")
        }
    }
}

release {
    tagTemplate.set("v\$version")
    preTagCommitMessage.set("[Gradle Release Plugin] - release version ")
    newVersionCommitMessage.set("[Gradle Release Plugin] - start work on ")
    git {
        requireBranch.set("master")
    }
}

val changelogFile = File(rootDir, "CHANGELOG.md")
val setChangelogDate = tasks.register("setChangelogDate") {
    doLast {
        val changelog = changelogFile.readText()
        val now = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val version = project.version.toString().removeSuffix("-SNAPSHOT")
        changelogFile.writeText(changelog.replace("[unreleased]", "$version ($now)"))
    }
}

val addChangelogEntry = tasks.register("addChangelogEntry") {
    doLast {
        val changelog = changelogFile.readText()
        val version = project.version.toString().removeSuffix("-SNAPSHOT")
        val entry = "## [unreleased]"
        if (!changelog.startsWith(entry)) {
            changelogFile.writeText(entry + "\n\n" + changelog)
        }
    }
}

tasks.named("preTagCommit") {
    dependsOn(setChangelogDate)
}

tasks.named("updateVersion") {
    finalizedBy(addChangelogEntry)
}
