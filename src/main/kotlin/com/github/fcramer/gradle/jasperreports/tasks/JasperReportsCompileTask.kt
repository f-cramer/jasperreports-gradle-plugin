package com.github.fcramer.gradle.jasperreports.tasks

import com.github.fcramer.gradle.jasperreports.commons.CompilationTask
import com.github.fcramer.gradle.jasperreports.commons.TaskConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.submit
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

@CacheableTask
abstract class JasperReportsCompileTask : DefaultTask() {
    @get:InputFiles
    @get:Classpath
    val classpath: ConfigurableFileCollection = project.files()

    @get:Nested
    val launcher: Property<JavaLauncher> = project.objects.property<JavaLauncher>().convention(
        project.extensions.getByType<JavaToolchainService>()
            .launcherFor(project.extensions.getByType<JavaPluginExtension>().toolchain),
    )

    @get:InputDirectory
    @get:Incremental
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val srcDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.projectDirectory.dir("src/main/reports"))

    @get:Internal
    val tmpDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("jasperreports/tmp"))

    @get:OutputDirectory
    val outDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("jasperreports/out"))

    @get:Input
    val srcExt: Property<String> = project.objects.property<String>()
        .convention(".jrxml")

    @get:Input
    val outExt: Property<String> = project.objects.property<String>()
        .convention(".jasper")

    @get:Input
    val compiler: Property<String> = project.objects.property<String>()
        .convention("net.sf.jasperreports.engine.design.JRJdtCompiler")

    @get:Input
    val verbose: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)

    @get:Input
    val useRelativeOutDir: Property<Boolean> = project.objects.property<Boolean>()
        .convention(true)

    @get:Input
    val keepJava: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)

    @get:Input
    val validateXml: Property<Boolean> = project.objects.property<Boolean>()
        .convention(true)

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun execute(inputs: InputChanges) {
        displayConfiguration()
        compileReports(inputs)
    }

    private fun compileReports(inputs: InputChanges) {
        if (verbose.get() && logger.isLifecycleEnabled) {
            logDependencies()
        }

        // delete all output if not incremental
        if (!inputs.isIncremental) {
            outDir.asFileTree.visit {
                if (!isDirectory) {
                    file.delete()
                }
            }
        }

        val configuration = TaskConfiguration(compiler.getOrNull(), validateXml.get(), keepJava.get(), tmpDir.asFile.get())

        val compilationTasks = buildSet {
            for (change in inputs.getFileChanges(srcDir)) {
                change.createTaskOrDeleteOutput(configuration)?.let { add(it) }
            }
        }

        val workQueue = workerExecutor.processIsolation {
            classpath.from(this@JasperReportsCompileTask.classpath)
            forkOptions {
                val executable = launcher.get().executablePath.asFile.absolutePath
                executable(executable)
            }
        }

        logger.lifecycle("compiling {} report(s)", compilationTasks.size)
        for (task in compilationTasks) {
            val taskConfig = task.configuration
            workQueue.submit(JasperReportsCompileWork::class) {
                source.set(task.source)
                output.set(task.output)
                tmpDir.set(taskConfig.tmpDir)
                taskConfig.compiler?.let(compiler::set)
                validateXml.set(taskConfig.isValidateXml)
                keepJava.set(taskConfig.isKeepJava)
            }
        }

        workQueue.await()
    }

    private fun FileChange.createTaskOrDeleteOutput(configuration: TaskConfiguration): CompilationTask? = if (fileType == FileType.FILE) {
        when (changeType) {
            ChangeType.ADDED, ChangeType.MODIFIED -> {
                createTask(configuration)
            }

            ChangeType.REMOVED -> {
                deleteOutput()
                null
            }
        }
    } else {
        null
    }

    private fun FileChange.createTask(configuration: TaskConfiguration): CompilationTask? {
        if (verbose.get()) {
            logger.lifecycle("{} file {}", changeType.name.lowercase(), file.name)
        }
        if (file.name.endsWith(srcExt.get())) {
            try {
                return CompilationTask(file, outputFile(file), configuration)
            } catch (e: Exception) {
                logger.error("Output file for \"{}\" could not be generated", file, e)
            }
        }
        return null
    }

    private fun FileChange.deleteOutput() {
        if (verbose.get()) {
            logger.lifecycle("Removed file {}", file.name)
        }
        try {
            outputFile(file).delete()
        } catch (e: Exception) {
            logger.error("Could not remove output for file {}", file.name, e)
        }
    }

    private fun outputFile(src: File): File {
        var useOutDir = outDir.asFile.get()

        if (useRelativeOutDir.get()) {
            val srcPath = src.toPath()
            val srcDirPath = srcDir.asFile.get().absoluteFile.toPath()
            val relativePath: Path
            try {
                relativePath = srcDirPath.relativize(srcPath)
            } catch (e: IllegalArgumentException) {
                if (verbose.get()) {
                    logger.error("could not relativize {} to {}", srcDirPath, srcPath)
                }
                throw e
            }

            val parent = if (relativePath.parent != null) relativePath.parent.toString() else ""
            val path = Paths.get(useOutDir.absolutePath, parent)

            useOutDir = path.toFile()
            if (!useOutDir.isDirectory) {
                if (verbose.get()) {
                    logger.lifecycle("Create outDir: {}", useOutDir.absolutePath)
                }
                useOutDir.mkdirs()
            }
        }

        return File(useOutDir, src.name.replace(srcExt.get(), outExt.get()))
    }

    private fun logDependencies() {
        val dependencies = classpath.files.asSequence()
            .filterNotNull()
            .map { it.toURI() }
            .toList()
        logger.lifecycle("Additional classpath: {}", dependencies)
    }

    private fun displayConfiguration() {
        if (!verbose.get()) {
            return
        }

        logger.lifecycle(">>> JasperReports Plugin Configuration")
        val srcDir = srcDir.asFile.getOrNull()
        logger.lifecycle("Source directory: {}", srcDir?.canonicalPath)
        val tmpDir = tmpDir.asFile.getOrNull()
        logger.lifecycle("Temporary directory: {}", tmpDir?.canonicalPath)
        val outDir = outDir.asFile.getOrNull()
        logger.lifecycle("Output directory: {}", outDir?.canonicalPath)
        logger.lifecycle("Source files extension: {}", srcExt.getOrNull())
        logger.lifecycle("Compiled files extension: {}", outExt.getOrNull())
        logger.lifecycle("Compiler: {}", compiler.getOrNull())
        logger.lifecycle("Keep Java files: {}", keepJava.getOrNull())
        logger.lifecycle("Validate XML before compiling: {}", validateXml.getOrNull())
        logger.lifecycle("Use relative outDir: {}", useRelativeOutDir.getOrNull())
        logger.lifecycle("<<<")
    }
}
