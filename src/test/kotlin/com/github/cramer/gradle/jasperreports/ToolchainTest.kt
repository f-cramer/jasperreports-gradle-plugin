package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.github.fcramer.gradle.jasperreports.utils.getJasperreportsVersion
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.FileVisitor
import java.nio.file.Files
import kotlin.streams.toList

@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
class ToolchainTest {
    @TempDir
    private lateinit var projectDirectory: File
    private lateinit var propertiesFile: File
    private lateinit var buildFile: File

    @ParameterizedTest(name = "{index} - on Java {0}")
    @MethodSource("getJavaVersions")
    fun `java toolchain should be used if configured`(javaVersion: Int) {
        val javaToolchainPaths = System.getProperty("org.gradle.java.installations.paths")
        if (javaToolchainPaths != null) {
            propertiesFile.writeText(
                """
                org.gradle.java.installations.auto-download=false
                org.gradle.java.installations.auto-detect=false
                org.gradle.java.installations.paths=$javaToolchainPaths
                """.trimIndent(),
            )
        } else {
            propertiesFile.writeText(
                """
                org.gradle.java.installations.auto-download=false
                """.trimIndent(),
            )
        }

        val jasperreportsVersion = getJasperreportsVersion() ?: error("unable to get jasperreports version")
        buildFile.writeText(
            """
                plugins {
                    java
                    id("io.github.f-cramer.jasperreports")
                }

                repositories {
                    mavenCentral()
                }
    
                java {
                    toolchain {
                        languageVersion.set(JavaLanguageVersion.of($javaVersion))
                    }
                }

                jasperreports {
                    verbose.set(true)
                }

                dependencies {
                    jasperreportsClasspath("net.sf.jasperreports:jasperreports:$jasperreportsVersion")
                }
            """.trimIndent(),
        )

        val result = runner().build()
        assertThat(result.task(":compileAllReports")?.outcome, name = "compileAllReports outcome")
            .isEqualTo(TaskOutcome.SUCCESS)
        if (javaVersion == 8) {
            assertThat(result.output).contains("java.version = 1.8")
        } else {
            assertThat(result.output).contains("java.version = $javaVersion")
        }

        val outputRoot = projectDirectory.resolve("build/jasperreports/out")
        val outputFiles = Files.walk(outputRoot.toPath())
            .filter { Files.isRegularFile(it) }
            .toList()
        assertThat(outputFiles).isNotEmpty()
    }

    private fun runner(): GradleRunner = GradleRunner.create()
        .withPluginClasspath()
        .withEnvironment(System.getenv())
        .withArguments("compileAllReports", "--full-stacktrace")
        .forwardOutput()
        .withProjectDir(projectDirectory)

    @BeforeEach
    fun setup() {
        propertiesFile = File(projectDirectory, "gradle.properties")
        buildFile = File(projectDirectory, "build.gradle.kts")

        val templateName = getReportTemplateNameWithoutExtension()
        this::class.java.getResourceAsStream("/$templateName.jrxml")!!.use { input ->
            val outputDirectory = File(projectDirectory, "src/main/reports")
            outputDirectory.mkdirs()
            val outputFile = File(outputDirectory, "$templateName.jrxml")
            Files.newOutputStream(outputFile.toPath()).use { output ->
                input.copyTo(output)
            }
        }
    }

    companion object {
        @JvmStatic
        fun getJavaVersions(): List<Int> = System.getProperty("java.versions")!!
            .split(",")
            .map { it.toInt() }
    }
}
