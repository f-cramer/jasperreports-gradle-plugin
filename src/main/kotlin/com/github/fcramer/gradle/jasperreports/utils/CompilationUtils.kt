package com.github.fcramer.gradle.jasperreports.utils

import com.github.fcramer.gradle.jasperreports.JasperReportsPlugin
import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReportsContext
import net.sf.jasperreports.engine.SimpleJasperReportsContext
import net.sf.jasperreports.engine.design.JRCompiler
import net.sf.jasperreports.engine.xml.JRReportSaxParserFactory
import java.io.File
import java.util.Properties

fun compileReport(task: CompilationTask) {
    if (task.configuration.isVerbose) {
        println("compiling with java.version = ${System.getProperty("java.version")}")
    }

    val context = configureJasperReportsContext(task.configuration)
    val compileManager = JasperCompileManager.getInstance(context)

    fun doCompileReport() {
        compileManager.compileToFile(task.source.absolutePath, task.output.absolutePath)
    }

    val version = getJasperreportsVersion()
    if (version != null && version.startsWith("6")) {
        doCompileReport()
    } else {
        try {
            doCompileReport()
        } catch (e: JRException) {
            val messageTest = "Unable to load report"
            if (e.message == messageTest) {
                throw JRException("$messageTest. Please check if your template is compatible with JasperReports 7+", e)
            }
        }
    }
}

fun getJasperreportsVersion(): String? {
    val properties = Properties()
    JasperReportsPlugin::class.java.getResourceAsStream("/META-INF/maven/net.sf.jasperreports/jasperreports/pom.properties")!!.use {
        properties.load(it)
    }
    return properties.getProperty("version")
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
