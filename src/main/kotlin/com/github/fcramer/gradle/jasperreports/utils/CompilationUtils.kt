package com.github.fcramer.gradle.jasperreports.utils

import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReportsContext
import net.sf.jasperreports.engine.SimpleJasperReportsContext
import net.sf.jasperreports.engine.design.JRCompiler
import net.sf.jasperreports.engine.xml.JRReportSaxParserFactory
import java.io.File

fun compileReport(task: CompilationTask) {
    val context = configureJasperReportsContext(task.configuration)
    val compileManager = JasperCompileManager.getInstance(context)
    compileManager.compileToFile(task.source.absolutePath, task.output.absolutePath)
}

private fun configureJasperReportsContext(configuration: TaskConfiguration): JasperReportsContext {
    val context = SimpleJasperReportsContext()
    context.setProperty(
        JRCompiler.COMPILER_CLASSPATH,
        configuration.classpath.joinToString(separator = File.pathSeparator) { it.absolutePath },
    )
    context.setProperty(JRReportSaxParserFactory.COMPILER_XML_VALIDATION, configuration.isValidateXml.toString())
    configuration.compiler?.let { context.setProperty(JRCompiler.COMPILER_PREFIX, it) }
    context.setProperty(JRCompiler.COMPILER_KEEP_JAVA_FILE, configuration.isKeepJava.toString())

    val tmpDir = configuration.tmpDir.canonicalFile
    if (!tmpDir.exists()) {
        tmpDir.mkdirs()
    }
    require(tmpDir.isDirectory) { "tmpDir could not be created" }
    context.setProperty(JRCompiler.COMPILER_TEMP_DIR, tmpDir.path)

    return context
}
