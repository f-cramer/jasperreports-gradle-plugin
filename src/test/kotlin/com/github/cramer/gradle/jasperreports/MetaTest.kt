package com.github.cramer.gradle.jasperreports

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import org.junit.jupiter.api.Test
import java.util.Properties

class MetaTest {
    @Test
    fun testJasperReportsVersion() {
        val version = System.getProperty("jasperreports.version")
        assertThat(version).isNotEmpty()

        val properties = Properties()
        MetaTest::class.java.getResourceAsStream("/META-INF/maven/net.sf.jasperreports/jasperreports/pom.properties")!!.use {
            properties.load(it)
        }
        assertThat(properties.getProperty("version")).isEqualTo(version)
    }
}
