package com.github.cramer.gradle.jasperreports.utils

import assertk.assertThat
import assertk.assertions.isNotNull
import com.github.cramer.gradle.jasperreports.getReportTemplateNameWithoutExtension
import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import com.github.fcramer.gradle.jasperreports.utils.compileReport
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files

class CompilationUtilsTest {
    @Test
    fun testCompilation(@TempDir directory: File?) {
        val fileName = getReportTemplateNameWithoutExtension()
        val input = File(directory, "$fileName.jrxml")
        writeFile("/" + input.name, input)
        val output = File(directory, "$fileName.jasper")
        val tmp = File(directory, "tmp")

        val configuration = TaskConfiguration(compiler = null, isValidateXml = true, isKeepJava = false, tmpDir = tmp)
        compileReport(CompilationTask(input, output, configuration))
    }

    private fun writeFile(resource: String, file: File) {
        CompilationUtilsTest::class.java.getResourceAsStream(resource)!!.use { input ->
            assertThat(input, name = "resource").isNotNull()
            Files.newOutputStream(file.toPath()).use { output ->
                input.copyTo(output)
            }
        }
    }
}
