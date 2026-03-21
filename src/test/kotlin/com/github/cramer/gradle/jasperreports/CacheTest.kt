package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.isEqualTo
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

@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
class CacheTest {
    @TempDir
    private lateinit var projectDirectory: File
    private lateinit var buildFile: File

    @ParameterizedTest(name = "{index} - on Gradle {0}")
    @MethodSource("getGradleVersions")
    fun `cacheableTask is loaded from cache`(gradleVersion: String) {
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

                dependencies {
                    jasperreportsClasspath("net.sf.jasperreports:jasperreports:${getJasperreportsVersion()}")
                }
            """.trimIndent(),
        )

        val result1 = runner(gradleVersion).build()
        assertThat(result1.task(":compileAllReports")?.outcome, name = "first compileAllReports outcome")
            .isEqualTo(TaskOutcome.SUCCESS)

        File(projectDirectory, "build").deleteRecursively()

        val result2 = runner(gradleVersion).build()
        assertThat(result2.task(":compileAllReports")?.outcome, name = "second compileAllReports outcome")
            .isEqualTo(TaskOutcome.FROM_CACHE)
    }

    private fun runner(gradleVersion: String): GradleRunner = GradleRunner.create()
        .withPluginClasspath()
        .withArguments("--build-cache", "compileAllReports", "--full-stacktrace")
        .withProjectDir(projectDirectory)
        .withGradleVersion(gradleVersion)
        .forwardOutput()

    @BeforeEach
    fun setup() {
        val localBuildCacheDirector = File(projectDirectory, "local-cache")
        val settings = File(projectDirectory, "settings.gradle.kts")
        settings.writeText(
            """
                buildCache {
                    local {
                        directory = "${localBuildCacheDirector.toURI()}"
                    }
                }
            """.trimIndent(),
        )
        buildFile = File(projectDirectory, "build.gradle.kts")

        val templateName = getReportTemplateNameWithoutExtension()
        CacheTest::class.java.getResourceAsStream("/$templateName.jrxml")!!.use { input ->
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
        fun getGradleVersions(): List<String> = System.getProperty("gradle.versions")!!
            .split(",")
    }
}
