package com.github.cramer.gradle.jasperreports

import java.util.Properties

fun getJasperreportsVersion(): String? {
    val properties = Properties()
    MetaTest::class.java.getResourceAsStream("/META-INF/maven/net.sf.jasperreports/jasperreports/pom.properties")!!.use {
        properties.load(it)
    }
    return properties.getProperty("version")
}
