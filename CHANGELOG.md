## [unreleased]
- Document that the plugin is tested with multiple Java versions
- Run tests for JasperReports 7.0.2 instead of 7.0.1
- Fix some warnings in build.gradle.kts
- Run tests for JasperReports 7.0.3 instead of 7.0.2
- Throw a better exception when the template might be for an older JasperReports version
- Run tests on Java 25 instead of 23
- Run tests for JasperReports 6.21.5 instead of 6.21.4
- Test plugin on multiple Gradle versions

### Dependencies
- Bump `actions/checkout` from 4 to 6 ([#33](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/33), [#41](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/41))
- Bump `actions/setup-java` from 4 to 5 ([#35](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/35))
- Bump `com.gradle.plugin-publish` from 1.3.1 to 2.0.0 ([#36](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/36))
- Bump `dangoslen/dependabot-changelog-helper` from 3 to 4 ([#27](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/27))
- Bump `gradle-wrapper` from 8.0.2 to 9.3.1 ([#45](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/45))
- Bump `gradle/actions/setup-gradle` from 4 to 5 ([#37](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/37))
- Bump `gradle/actions` from 4 to 5 ([#37](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/37))
- Bump `io.gitlab.arturbosch.detekt` from 1.23.7 to 1.23.8 ([#25](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/25))
- Bump `mikepenz/action-junit-report` from 5 to 6 ([#39](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/39))
- Bump `org.jlleitschuh.gradle.ktlint` from 12.1.2 to 14.0.1 ([#26](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/26), [#30](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/30), [#32](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/32), [#40](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/40))
- Bump `stefanzweifel/git-auto-commit-action` from 5 to 7 ([#38](https://github.com/f-cramer/jasperreports-gradle-plugin/pull/38))

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
