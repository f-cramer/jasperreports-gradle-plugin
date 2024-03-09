package com.github.fcramer.gradle.jasperreports.utils

import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReportsContext
import net.sf.jasperreports.engine.SimpleJasperReportsContext
import net.sf.jasperreports.engine.design.JRCompiler
import net.sf.jasperreports.engine.xml.JRReportSaxParserFactory

fun compileReport(task: CompilationTask) {
    val context = configureJasperReportsContext(task.configuration)
    val compileManager = JasperCompileManager.getInstance(context)
    compileManager.compileToFile(task.source.absolutePath, task.output.absolutePath)
}

private fun configureJasperReportsContext(configuration: TaskConfiguration): JasperReportsContext {
    val context = SimpleJasperReportsContext()
    context.setProperty(JRReportSaxParserFactory.COMPILER_XML_VALIDATION, configuration.isValidateXml.toString())
    configuration.compiler?.let { context.setProperty(JRCompiler.COMPILER_PREFIX, it) }
    context.setProperty(JRCompiler.COMPILER_KEEP_JAVA_FILE, configuration.isKeepJava.toString())
    context.setProperty(JRCompiler.COMPILER_TEMP_DIR, configuration.tmpDir.canonicalPath)
    return context
}
