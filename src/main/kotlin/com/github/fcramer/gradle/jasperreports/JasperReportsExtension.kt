package com.github.fcramer.gradle.jasperreports

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property

abstract class JasperReportsExtension(project: Project) {
    val classpath: ConfigurableFileCollection = project.files()
    val launcher: Property<JavaLauncher> = project.objects.property<JavaLauncher>().convention(
        project.extensions.getByType<JavaToolchainService>()
            .launcherFor(project.extensions.getByType<JavaPluginExtension>().toolchain),
    )
    val srcDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.projectDirectory.dir("src/main/reports"))
    val tmpDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("jasperreports/tmp"))
    val outDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("jasperreports/out"))
    val srcExt: Property<String> = project.objects.property<String>()
        .convention(".jrxml")
    val outExt: Property<String> = project.objects.property<String>()
        .convention(".jasper")
    val compiler: Property<String> = project.objects.property<String>()
        .convention("net.sf.jasperreports.engine.design.JRJdtCompiler")
    val keepJava: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)
    val validateXml: Property<Boolean> = project.objects.property<Boolean>()
        .convention(true)
    val verbose: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)
    val useRelativeOutDir: Property<Boolean> = project.objects.property<Boolean>()
        .convention(true)
}
