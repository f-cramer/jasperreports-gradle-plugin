package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files

@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
class CacheTest {
    @TempDir
    private lateinit var projectDirectory: File
    private lateinit var buildFile: File

    @Test
    fun `cacheableTask is loaded from cache`() {
        buildFile.writeText(
            """
                plugins {
                    java
                    id("io.github.f-cramer.jasperreports")
                }
            """.trimIndent(),
        )

        val result1 = runner().build()
        assertThat(result1.task(":compileAllReports")?.outcome, name = "first compileAllReports outcome")
            .isEqualTo(TaskOutcome.SUCCESS)

        File(projectDirectory, "build").deleteRecursively()

        val result2 = runner().build()
        assertThat(result2.task(":compileAllReports")?.outcome, name = "second compileAllReports outcome")
            .isEqualTo(TaskOutcome.FROM_CACHE)
    }

    private fun runner(): GradleRunner = GradleRunner.create()
        .withPluginClasspath()
        .withArguments("--build-cache", "compileAllReports")
        .withProjectDir(projectDirectory)

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
            val outputFile = File(outputDirectory, "$templateName.jasper")
            Files.newOutputStream(outputFile.toPath()).use { output ->
                input.copyTo(output)
            }
        }
    }
}
