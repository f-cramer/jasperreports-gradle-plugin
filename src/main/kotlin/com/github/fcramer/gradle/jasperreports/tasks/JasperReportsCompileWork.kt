package com.github.fcramer.gradle.jasperreports.tasks

import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import com.github.fcramer.gradle.jasperreports.utils.compileReport
import org.gradle.workers.WorkAction

abstract class JasperReportsCompileWork : WorkAction<CompilationParameters> {
    override fun execute() {
        val parameters = parameters!!
        val source = parameters.source.asFile.get()
        val output = parameters.output.asFile.get()
        val compiler = parameters.compiler.getOrNull()
        val validateXml = parameters.validateXml.get()
        val keepJava = parameters.keepJava.get()
        val tmpDir = parameters.tmpDir.asFile.get()
        val classpath = parameters.classpath.get()
        val verbose = parameters.verbose.get()

        val task = CompilationTask(
            source,
            output,
            TaskConfiguration(
                compiler,
                validateXml,
                keepJava,
                tmpDir,
                classpath,
                verbose,
            ),
        )

        compileReport(task)
    }
}
