package com.github.cramer.gradle.jasperreports

import com.github.fcramer.gradle.jasperreports.utils.getJasperreportsVersion

fun getReportTemplateNameWithoutExtension(): String {
    val jasperreportsVersion = getJasperreportsVersion()!!
    return if (jasperreportsVersion.startsWith("6.")) "Blank6" else "Blank"
}
