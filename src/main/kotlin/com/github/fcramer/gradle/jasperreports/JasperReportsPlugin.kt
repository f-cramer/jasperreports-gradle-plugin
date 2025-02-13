package com.github.fcramer.gradle.jasperreports

import com.github.fcramer.gradle.jasperreports.tasks.JasperReportsCompileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class JasperReportsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class)
        val extension = project.extensions.create<JasperReportsExtension>(EXTENSION_NAME, project)

        val classpathConfiguration = project.configurations.create(CONFIGURATION_NAME) {
            description = "Classpath for JasperReports design source files."
        }

        project.tasks.register<JasperReportsCompileTask>(COMPILE_TASK_NAME) {
            description = "Compile JasperReports design source files."
            group = GROUP

            classpath.from(extension.classpath)
            classpath.from(classpathConfiguration)
            launcher.convention(extension.launcher)
            srcDir.convention(extension.srcDir)
            tmpDir.convention(extension.tmpDir)
            outDir.convention(extension.outDir)
            srcExt.convention(extension.srcExt)
            outExt.convention(extension.outExt)
            compiler.convention(extension.compiler)
            verbose.convention(extension.verbose)
            useRelativeOutDir.convention(extension.useRelativeOutDir)
            keepJava.convention(extension.keepJava)
            validateXml.convention(extension.validateXml)
        }
    }

    companion object {
        const val GROUP = "jasperReports"
        const val EXTENSION_NAME = "jasperreports"
        const val CONFIGURATION_NAME = "jasperreportsClasspath"
        const val COMPILE_TASK_NAME = "compileAllReports"
    }
}
