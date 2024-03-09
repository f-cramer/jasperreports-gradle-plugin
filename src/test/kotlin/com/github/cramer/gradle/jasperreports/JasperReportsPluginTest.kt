package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.github.fcramer.gradle.jasperreports.JasperReportsExtension
import com.github.fcramer.gradle.jasperreports.JasperReportsPlugin
import com.github.fcramer.gradle.jasperreports.tasks.JasperReportsCompileTask
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class JasperReportsPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setupProject() {
        project = ProjectBuilder.builder().build().also {
            it.pluginManager.apply("com.github.f-cramer.jasperreports")
        }
    }

    @Test
    fun pluginAddsJasperReportsCompileTask() {
        assertThat(project.tasks.getByName(JasperReportsPlugin.COMPILE_TASK_NAME)).isInstanceOf<JasperReportsCompileTask>()
    }

    @Test
    fun pluginAddsJasperReportsExtension() {
        assertThat(project.extensions.getByName(JasperReportsPlugin.EXTENSION_NAME)).isInstanceOf<JasperReportsExtension>()
    }

    @Test
    fun pluginHasDefaultValues() {
        val extension = this.extension
        assertThat(extension.classpath.files).isEmpty()
        assertThat(extension.srcDir.asFile.get()).isEqualTo(File(project.projectDir, "src/main/reports"))
        assertThat(extension.tmpDir.asFile.get()).isEqualTo(File(project.layout.buildDirectory.asFile.get(), "jasperreports/tmp"))
        assertThat(extension.outDir.asFile.get()).isEqualTo(File(project.layout.buildDirectory.asFile.get(), "jasperreports/out"))
        assertThat(extension.srcExt.get()).isEqualTo(".jrxml")
        assertThat(extension.outExt.get()).isEqualTo(".jasper")
        assertThat(extension.compiler.get()).isEqualTo("net.sf.jasperreports.engine.design.JRJdtCompiler")
        assertThat(extension.keepJava.get()).isFalse()
        assertThat(extension.validateXml.get()).isTrue()
        assertThat(extension.verbose.get()).isFalse()
        assertThat(extension.useRelativeOutDir.get()).isTrue()
    }

    @Test
    fun pluginSpreadsDirOptions() {
        val src = File(project.layout.buildDirectory.asFile.get(), "src/jasperreports")
        val tmp = File(project.layout.buildDirectory.asFile.get(), "tmp/jasperreports")
        val out = File(project.layout.buildDirectory.asFile.get(), "out/jasperreports")

        val extension = this.extension
        extension.srcDir.set(src)
        extension.tmpDir.set(tmp)
        extension.outDir.set(out)

        val compileTask = compileTask

        assertThat(extension.srcDir.asFile.get()).isEqualTo(src)
        assertThat(compileTask.srcDir.asFile.get()).isEqualTo(src)

        assertThat(extension.tmpDir.asFile.get()).isEqualTo(tmp)
        assertThat(compileTask.tmpDir.asFile.get()).isEqualTo(tmp)

        assertThat(extension.outDir.asFile.get()).isEqualTo(out)
        assertThat(compileTask.outDir.asFile.get()).isEqualTo(out)
    }

    @Test
    fun pluginSpreadsExtOptions() {
        val src = ".xml"
        val out = ".class"

        val extension = this.extension

        extension.srcExt.set(src)
        extension.outExt.set(out)

        val compileTask = compileTask

        assertThat(extension.srcExt.get()).isEqualTo(src)
        assertThat(compileTask.srcExt.get()).isEqualTo(src)

        assertThat(extension.outExt.get()).isEqualTo(out)
        assertThat(compileTask.outExt.get()).isEqualTo(out)
    }

    @Test
    fun pluginSpreadsClasspathOption() {
        project.pluginManager.apply("java")

        val extension = this.extension
        val sourceSets = project.extensions.getByType<SourceSetContainer>()
        val mainOutput = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output
        extension.classpath.from(mainOutput)

        assertThat(extension.classpath.files).containsExactlyInAnyOrder(*mainOutput.toList().toTypedArray())
        assertThat(compileTask.classpath.files).containsExactlyInAnyOrder(*mainOutput.toList().toTypedArray())
    }

    @Test
    fun pluginSpreadsCompilerOption() {
        val groovyCompiler = "net.sf.jasperreports.compilers.JRGroovyCompiler"

        val extension = this.extension
        extension.compiler.set(groovyCompiler)

        assertThat(extension.compiler.get()).isEqualTo(groovyCompiler)
        assertThat(compileTask.compiler.get()).isEqualTo(groovyCompiler)
    }

    @Test
    fun pluginSpreadsKeepJavaOption() {
        val extension = this.extension
        extension.keepJava.set(true)

        assertThat(extension.keepJava.get()).isTrue()
        assertThat(compileTask.keepJava.get()).isTrue()
    }

    @Test
    fun pluginSpreadsValidateXmlOption() {
        val extension = this.extension
        extension.validateXml.set(false)

        assertThat(extension.validateXml.get()).isFalse()
        assertThat(compileTask.validateXml.get()).isFalse()
    }

    @Test
    fun pluginSpreadsVerboseOption() {
        val extension = this.extension
        extension.verbose.set(true)

        assertThat(extension.verbose.get()).isTrue()
        assertThat(compileTask.verbose.get()).isTrue()
    }

    @Test
    fun pluginSpreadsUseRelativeOutDirOption() {
        val extension = this.extension
        extension.useRelativeOutDir.set(true)

        assertThat(extension.useRelativeOutDir.get()).isTrue()
        assertThat(compileTask.useRelativeOutDir.get()).isTrue()
    }

    @Test
    fun canAddCompileClasspathToClasspath() {
        project.pluginManager.apply("java")

        val sourceSets = project.extensions.getByType<SourceSetContainer>()
        val compileClasspath = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).compileClasspath

        compileTask.classpath.from(compileClasspath)
        assertThat(compileTask.classpath).containsExactlyInAnyOrder(*compileClasspath.toList().toTypedArray())
    }

    private val extension: JasperReportsExtension
        get() = project.extensions.getByName<JasperReportsExtension>(JasperReportsPlugin.EXTENSION_NAME)

    private val compileTask: JasperReportsCompileTask
        get() = project.tasks.getByName<JasperReportsCompileTask>(JasperReportsPlugin.COMPILE_TASK_NAME)
}
