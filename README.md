# Gradle JasperReports Plugin

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/github/f-cramer/gradle/jasperreports-gradle-plugin/maven-metadata.xml.svg?colorB=007ec6&label=version)](https://plugins.gradle.org/plugin/io.github.f-cramer.jasperreports)

## Description

Provides the capability to compile JasperReports design files. This plugin is based on a private fork of [gradle-jasperreports](https://github.com/gmazelier/gradle-jasperreports) which seems to have been abandoned.

## Usage

This plugin provides one main task, `compileAllReports`. It uses Gradles [input changes](https://docs.gradle.org/current/dsl/org.gradle.work.InputChanges.html) feature to compile out-of-date files and its [Worker API](https://docs.gradle.org/current/userguide/worker_api.html) for parallel processing. Adapt your build process to your own needs by defining the proper tasks depedencies (see [Custom Build Process](#Custom-Build-Process) below).

If your designs compilation needs to run after Groovy compilation, running `compileAllReports` should give a similar output:

    $ gradle compileAllReports
    :compileJava UP-TO-DATE
    :compileGroovy UP-TO-DATE
    :compileAllReports

    BUILD SUCCESSFUL

    Total time: 6.577 secs

To clean up and start fresh, simply run:

    $ gradle clean compileAllReports

### Installation

    plugins {
        id("io.github.f-cramer.jasperreports") version "<version>"
    }

### Configuration

Below are the parameters that can be used to configure the build:

| Parameter           | Type                         | Description                                                                                   |
|---------------------|------------------------------|-----------------------------------------------------------------------------------------------|
| `srcDir`            | `File`                       | Design source files directory. Default value: `src/main/reports`                              |
| `tmpDir`            | `File`                       | Temporary files (`.java`) directory. Default value: `${project.buildDir}/jasperreports/tmp`   |
| `outDir`            | `File`                       | Compiled reports file directory. Default value: `${project.buildDir}/jasperreports/out`       |
| `srcExt`            | `String`                     | Design source files extension. Default value: `'.jrxml'`                                      |
| `outExt`            | `String`                     | Compiled reports files extension. Default value: `'.jasper'`                                  |
| `compiler`          | `String`                     | The report compiler to use. Default value: `net.sf.jasperreports.engine.design.JRJdtCompiler` |
| `keepJava`          | `boolean`                    | Keep temporary files after compiling. Default value: `false`                                  |
| `validateXml`       | `boolean`                    | Validate source files before compiling. Default value: `true`                                 |
| `verbose`           | `boolean`                    | Verbose plugin outpout. Default value: `false`                                                |
| `useRelativeOutDir` | `boolean`                    | The outDir is relative to java classpath. Default value: `true`                               |
| `classpath`         | `ConfigurableFileCollection` | Classpath to use for compilation. Default value: `<empty>`                                    |
| `launcher`          | `JavaLauncher`               | Launcher from standard java toolchain                                                         |

#### Attention:

The compilation task does *not* add a default version of Jasperreports to the compilation classpath, so by default the task will fail. You have to add a version of Jasperreports and every other dependency needed to compile the reports yourself. The following example shows how to the applications *compile classpath*

    jasperreports {
        classpath.from(configurations.compileClasspath)
    }

### Compatibility

The plugin is tested with Java 8, Gradle 8.0.2 and the following JasperReports versions

* 6.17.0
* 6.18.1
* 6.19.1
* 6.20.6
* 6.21.4
* 7.0.1

### Example

Below is a complete example, with default values:

    jasperreports {
        srcDir = file("src/main/reports")
        tmpDir = file("${project.buildDir}/jasperreports/tmp")
        outDir = file("${project.buildDir}/jasperreports/out")
        srcExt = ".jrxml"
        outExt = ".jasper"
        compiler = "net.sf.jasperreports.engine.design.JRJdtCompiler"
        keepJava = false
        validateXml = true
        verbose = false
        useRelativeOutDir = true
        classpath.from()
    }

### Custom Build Process

Adding a task dependency is very simple. For example, if you want to make sure that Java compilation is successfully performed before JasperReports designs compilation, just add the following to your build script:

    tasks.compileAllReports.configure {
        dependsOn(tasks.compileJava)
    }

### Custom Classpath

#### Adding Project Compiled Sources

Use the `classpath` property to acces your compiled sources in you JasperReports designs. Configure your build script in a similar way:

    jasperreports {
        verbose = true
        classpath.from(project.sourceSets.main)
    }

## Getting Help

To ask questions or report bugs, please use the [Github issues](https://github.com/f-cramer/jasperreports-gradle-plugin/issues).

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
