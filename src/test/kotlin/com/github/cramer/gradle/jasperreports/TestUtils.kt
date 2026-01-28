package com.github.cramer.gradle.jasperreports

import com.github.fcramer.gradle.jasperreports.utils.getJasperreportsVersion

val preferredGradleVersion: String
    get() = System.getProperty("gradle.version.preferred") ?: error("no preferred gradle version found")

fun getReportTemplateNameWithoutExtension(): String {
    val jasperreportsVersion = getJasperreportsVersion()!!
    return if (jasperreportsVersion.startsWith("6.")) "Blank6" else "Blank"
}
