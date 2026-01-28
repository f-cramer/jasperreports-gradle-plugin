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
import java.nio.file.Files
import java.util.Properties
import kotlin.io.path.outputStream
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

        val gradleProperties = Properties()
        gradleProperties["org.gradle.java.installations.auto-download"] = false.toString()
        if (javaToolchainPaths != null) {
            gradleProperties["org.gradle.java.installations.auto-detect"] = false.toString()
            gradleProperties["org.gradle.java.installations.paths"] = javaToolchainPaths
        }
        val propertiesFileWriter = propertiesFile.toPath().outputStream().bufferedWriter(Charsets.UTF_8)
        propertiesFileWriter.use {
            gradleProperties.store(it, null)
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
                    maven { url = uri("https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/") }
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
        val expectedJavaVersion = if (javaVersion == 8) "1.8" else javaVersion
        assertThat(result.output).contains("compiling with java.version = $expectedJavaVersion")

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
        .withGradleVersion(preferredGradleVersion)
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
