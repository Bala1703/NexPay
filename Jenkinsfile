pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonarcloud-token')
        FAILED_SERVICE = 'NexPay'
    }

    stages {

        stage('Prepare AI Log') {
            steps {
                bat '''
                    if exist "%WORKSPACE%\\ai-build-log.txt" del /f /q "%WORKSPACE%\\ai-build-log.txt"
                '''
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = bat(
                        script: 'git diff --name-only HEAD~1 HEAD',
                        returnStdout: true
                    ).trim()

                    echo "Changed files:"
                    echo changedFiles

                    env.BUILD_COMMON = 'false'
                    env.BUILD_USER = 'false'
                    env.BUILD_CONNECTION = 'false'
                    env.BUILD_BANK = 'false'
                    env.BUILD_TRANSACTION = 'false'
                    env.BUILD_PAYMENT = 'false'
                    env.BUILD_AI_ANALYZER = 'false'

                    if (changedFiles.contains('common/')) {
                        env.BUILD_COMMON = 'true'
                        env.BUILD_USER = 'true'
                        env.BUILD_CONNECTION = 'true'
                    }

                    if (changedFiles.contains('services/user-service/')) {
                        env.BUILD_USER = 'true'
                    }

                    if (changedFiles.contains('services/nexpay-connection-service/')) {
                        env.BUILD_CONNECTION = 'true'
                    }

                    if (changedFiles.contains('services/bank-account-service/')) {
                        env.BUILD_BANK = 'true'
                    }

                    if (changedFiles.contains('services/nexpay-transaction-service/')) {
                        env.BUILD_TRANSACTION = 'true'
                    }

                    if (changedFiles.contains('services/nexpay-payment-service/')) {
                        env.BUILD_PAYMENT = 'true'
                    }

                    if (changedFiles.contains('services/ai-cicd-analyzer/')) {
                        env.BUILD_AI_ANALYZER = 'true'
                    }

                    echo "BUILD_COMMON       = ${env.BUILD_COMMON}"
                    echo "BUILD_USER         = ${env.BUILD_USER}"
                    echo "BUILD_CONNECTION   = ${env.BUILD_CONNECTION}"
                    echo "BUILD_BANK         = ${env.BUILD_BANK}"
                    echo "BUILD_TRANSACTION  = ${env.BUILD_TRANSACTION}"
                    echo "BUILD_PAYMENT      = ${env.BUILD_PAYMENT}"
                    echo "BUILD_AI_ANALYZER  = ${env.BUILD_AI_ANALYZER}"
                }
            }
        }

        stage('Build Common') {
            when {
                expression {
                    env.BUILD_COMMON == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'common'
                }
                dir('common') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build User Service') {
            when {
                expression {
                    env.BUILD_USER == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'user-service'
                }
                dir('services/user-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Connection Service') {
            when {
                expression {
                    env.BUILD_CONNECTION == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'nexpay-connection-service'
                }
                dir('services/nexpay-connection-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Bank Account Service') {
            when {
                expression {
                    env.BUILD_BANK == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'bank-account-service'
                }
                dir('services/bank-account-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Transaction Service') {
            when {
                expression {
                    env.BUILD_TRANSACTION == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'nexpay-transaction-service'
                }
                dir('services/nexpay-transaction-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Payment Service') {
            when {
                expression {
                    env.BUILD_PAYMENT == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'nexpay-payment-service'
                }
                dir('services/nexpay-payment-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build AI CICD Analyzer') {
            when {
                expression {
                    env.BUILD_AI_ANALYZER == 'true'
                }
            }
            steps {
                script {
                    env.FAILED_SERVICE = 'ai-cicd-analyzer'
                }
                dir('services/ai-cicd-analyzer') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                script {
                    env.FAILED_SERVICE = 'SonarCloud Analysis'
                }
                tee("${env.WORKSPACE}\\ai-build-log.txt") {
                    bat '''
                        common\\mvnw.cmd verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
                        -Dsonar.organization=bala1703 ^
                        -Dsonar.projectKey=Bala1703_NexPay ^
                        -Dsonar.host.url=https://sonarcloud.io ^
                        -Dsonar.token=%SONAR_TOKEN%
                    '''
                }
            }
        }
    }

    post {

        failure {
            script {
                echo '================================================='
                echo 'BUILD FAILED'
                echo "Failed stage/service: ${env.FAILED_SERVICE}"
                echo '================================================='

                def aiLogFile = "${env.WORKSPACE}\\ai-build-log.txt"

                bat '''
                    echo ===== AI BUILD LOG =====

                    if exist "%WORKSPACE%\\ai-build-log.txt" (
                        type "%WORKSPACE%\\ai-build-log.txt"
                    ) else (
                        echo AI build log file NOT FOUND
                    )

                    echo.
                    echo ===== COPYING AI LOG =====

                    if not exist "D:\\AI Powered CICD Optimization\\NexPay\\logs" (
                        mkdir "D:\\AI Powered CICD Optimization\\NexPay\\logs"
                    )

                    if exist "%WORKSPACE%\\ai-build-log.txt" (
                        copy /Y "%WORKSPACE%\\ai-build-log.txt" "D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt"
                    )
                '''

                if (!fileExists(aiLogFile)) {
                    echo "AI build log not found: ${aiLogFile}. Skipping AI analysis."
                } else {
                    echo "AI build log found: ${aiLogFile}"

                    /*
                     * Agentic AI Investigation
                     */
                    try {
                        echo 'Starting Agentic AI build investigation...'

                        def buildLog = readFile(file: aiLogFile)

                        if (buildLog.trim().isEmpty()) {
                            echo 'Build log is empty. Skipping Agentic AI investigation.'
                        } else {
                            // Agent API accepts a maximum of 200,000 characters.
                            if (buildLog.length() > 200000) {
                                buildLog = '[Earlier log content truncated to fit the 200,000-character limit]\n' +
                                    buildLog.substring(buildLog.length() - 199900)
                            }

                            def agentRequest = [
                                serviceName: env.FAILED_SERVICE ?: 'NexPay',
                                buildNumber: "${env.BUILD_NUMBER}",
                                buildLog: buildLog
                            ]

                            def agentResponse = httpRequest(
                                httpMode: 'POST',
                                url: 'http://localhost:8410/api/agent/investigate',
                                contentType: 'APPLICATION_JSON',
                                acceptType: 'APPLICATION_JSON',
                                requestBody: groovy.json.JsonOutput.toJson(agentRequest),
                                timeout: 600,
                                validResponseCodes: '200'
                            )

                            echo '================================================='
                            echo 'AGENTIC AI INVESTIGATION REPORT'
                            echo '================================================='
                            echo agentResponse.content
                            echo '================================================='
                            echo 'AGENTIC AI INVESTIGATION COMPLETED'
                            echo '================================================='
                        }
                    } catch (Exception agentException) {
                        echo "Agentic AI investigation failed: ${agentException.getMessage()}"
                        echo 'No fallback to the old AI-CICD Analyzer is configured.'
                    }
                }
            }
        }
    }
}