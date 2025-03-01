## [unreleased]
- Document that the plugin is tested with multiple Java versions

### Dependencies
- Bump `io.gitlab.arturbosch.detekt` from 1.23.7 to 1.23.8 ([#25](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/25))
- Bump `org.jlleitschuh.gradle.ktlint` from 12.1.2 to 12.2.0 ([#26](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/26))

## 0.0.4 (2025-02-14)
- Add classpath to property `JRCompiler.COMPILER_CLASSPATH`
- Create a configuration called `jasperreportsClasspath` that is automatically added to the classpath of the default task
- Add documentation on how to compile designs for JasperReports 7+
- Add a textfield, a field and a parameter to the test reports

### Dependencies
- Bump `com.gradle.plugin-publish` from 1.2.2 to 1.3.1 ([#15](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/15), [#23](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/23))
- Bump `io.gitlab.arturbosch.detekt` from 1.23.6 to 1.23.7 ([#14](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/14))
- Bump `mikepenz/action-junit-report` from 4 to 5 ([#17](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/17))
- Bump `net.researchgate.release` from 3.0.2 to 3.1.0 ([#22](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/22))
- Bump `org.jlleitschuh.gradle.ktlint` from 12.1.1 to 12.1.2 ([#21](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/21))

## 0.0.3 (2024-11-12)
- Create tmpDir before using it for compilation  ([#18](https://github.com/f-cramer/jasperreports-gradle-plugin/issues/18))

## 0.0.2 (2024-08-31)
- Make JasperReportsCompileTask cacheable ([#11](https://github.com/f-cramer/jasperreports-gradle-plugin/issues/11))

## 0.0.1 (2024-03-09)
- Initial release
