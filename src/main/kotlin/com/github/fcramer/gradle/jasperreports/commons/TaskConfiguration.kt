package com.github.fcramer.gradle.jasperreports.commons

import java.io.File

data class TaskConfiguration(
    val compiler: String?,
    val isValidateXml: Boolean,
    val isKeepJava: Boolean,
    val tmpDir: File,
    val classpath: Collection<File>,
    val isVerbose: Boolean,
)
