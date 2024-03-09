pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew assemble --refresh-dependencies --continue'
            }
        }
        stage('Test') {
            parallel {
                stage('Gradle Check') {
                    steps {
                        sh './gradlew check'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                            recordIssues enabledForFailure: true, tools: [spotBugs(pattern: '**/*-main.xml', reportEncoding: 'UTF-8', useRankAsPriority: true)]
                        }
                    }
                }
                stage('Publish Test') {
                    steps {
                        sh './gradlew sourceJar javadocJar'
                    }
                }
            }
        }
        stage('Publish') {
            when {
                branch 'master'
            }
            steps {
                sh './gradlew publish'
            }
        }
    }

    post {
        changed {
            postChanged()
        }
        failure {
            postFailure()
        }
    }
}
