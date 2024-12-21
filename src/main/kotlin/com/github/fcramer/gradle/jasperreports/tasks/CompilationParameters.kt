package com.github.fcramer.gradle.jasperreports.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkParameters
import java.io.File

interface CompilationParameters : WorkParameters {
    val source: RegularFileProperty
    val output: RegularFileProperty

    val compiler: Property<String>
    val validateXml: Property<Boolean>
    val keepJava: Property<Boolean>
    val tmpDir: DirectoryProperty

    val classpath: SetProperty<File>
}
