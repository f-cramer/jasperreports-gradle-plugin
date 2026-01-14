package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.github.fcramer.gradle.jasperreports.utils.getJasperreportsVersion
import org.junit.jupiter.api.Test

class MetaTest {
    @Test
    fun testJasperReportsVersion() {
        val version = System.getProperty("jasperreports.version")
        assertThat(version).isNotEmpty()
        assertThat(getJasperreportsVersion()).isEqualTo(version)
    }
}
