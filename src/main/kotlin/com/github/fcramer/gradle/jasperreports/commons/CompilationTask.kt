package com.github.fcramer.gradle.jasperreports.commons

import java.io.File

class CompilationTask(
    val source: File,
    val output: File,
    val configuration: TaskConfiguration,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompilationTask

        return source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}
