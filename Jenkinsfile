pipeline {
    agent any

    stages {

        stage('Prepare AI Log') {
            steps {
                bat '''
                    if exist "ai-build-log.txt" (
                        del /F /Q "ai-build-log.txt"
                    )

                    echo ======================================== > "ai-build-log.txt"
                    echo NexPay Jenkins CI Build Log >> "ai-build-log.txt"
                    echo ======================================== >> "ai-build-log.txt"
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

                    bat """
                        echo. >> "ai-build-log.txt"
                        echo ===== Detect Changes ===== >> "ai-build-log.txt"
                        echo ${changedFiles.replace('"', '""')} >> "ai-build-log.txt"
                    """

                    env.CHANGED_FILES = changedFiles

                    env.BUILD_COMMON = 'false'
                    env.BUILD_USER = 'false'
                    env.BUILD_CONNECTION = 'false'
                    env.BUILD_BANK = 'false'
                    env.BUILD_TRANSACTION = 'false'
                    env.BUILD_PAYMENT = 'false'
                    env.BUILD_AI_ANALYZER = 'false'

                    // Common affects User and Connection
                    if (changedFiles.contains('common/')) {
                        env.BUILD_COMMON = 'true'
                        env.BUILD_USER = 'true'
                        env.BUILD_CONNECTION = 'true'
                    }

                    // Individual service changes
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

                    // AI CI/CD Analyzer changes
                    if (changedFiles.contains('services/ai-cicd-analyzer/')) {
                        env.BUILD_AI_ANALYZER = 'true'
                    }

                    echo "BUILD_COMMON = ${env.BUILD_COMMON}"
                    echo "BUILD_USER = ${env.BUILD_USER}"
                    echo "BUILD_CONNECTION = ${env.BUILD_CONNECTION}"
                    echo "BUILD_BANK = ${env.BUILD_BANK}"
                    echo "BUILD_TRANSACTION = ${env.BUILD_TRANSACTION}"
                    echo "BUILD_PAYMENT = ${env.BUILD_PAYMENT}"
                    echo "BUILD_AI_ANALYZER = ${env.BUILD_AI_ANALYZER}"
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
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build Common ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    call common\\mvnw.cmd clean install -DskipTests >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build User') {
            when {
                expression {
                    env.BUILD_USER == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build User Service ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\user-service

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build Connection') {
            when {
                expression {
                    env.BUILD_CONNECTION == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build Connection Service ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\nexpay-connection-service

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build Bank') {
            when {
                expression {
                    env.BUILD_BANK == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build Bank Account Service ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\bank-account-service

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build Transaction') {
            when {
                expression {
                    env.BUILD_TRANSACTION == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build Transaction Service ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\nexpay-transaction-service

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build Payment') {
            when {
                expression {
                    env.BUILD_PAYMENT == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build Payment Service ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\nexpay-payment-service

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build AI Analyzer') {
            when {
                expression {
                    env.BUILD_AI_ANALYZER == 'true'
                }
            }
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== Build AI CI/CD Analyzer ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    cd services\\ai-cicd-analyzer

                    call mvnw.cmd clean test >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                    if %ERRORLEVEL% NEQ 0 (
                        type "%WORKSPACE%\\ai-build-log.txt"
                        exit /b 1
                    )
                '''
            }
        }

        /*
         * TEMPORARY FAILURE TEST
         *
         * Keep this only while testing AI failure analysis.
         */
        stage('Test AI Failure') {
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== TEST FAILURE ===== >> "%WORKSPACE%\\ai-build-log.txt"

                    echo This is a test failure >> "%WORKSPACE%\\ai-build-log.txt"

                    type "%WORKSPACE%\\ai-build-log.txt"

                    exit /b 1
                '''
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                bat '''
                    echo. >> "%WORKSPACE%\\ai-build-log.txt"
                    echo ===== SonarCloud Analysis ===== >> "%WORKSPACE%\\ai-build-log.txt"
                '''

                withSonarQubeEnv('SonarCloud') {
                    bat '''
                        common\\mvnw.cmd -f pom.xml verify ^
                        org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
                        -Dsonar.organization=bala1703 ^
                        -Dsonar.projectKey=Bala1703_NexPay >> "%WORKSPACE%\\ai-build-log.txt" 2>&1

                        if %ERRORLEVEL% NEQ 0 (
                            type "%WORKSPACE%\\ai-build-log.txt"
                            exit /b 1
                        )
                    '''
                }
            }
        }
    }

    post {

        failure {
            echo 'NexPay CI: Build failed.'

            bat '''
                echo.
                echo ===== Final AI Build Log =====
                type "%WORKSPACE%\\ai-build-log.txt"

                if not exist "D:\\AI Powered CICD Optimization\\NexPay\\logs" (
                    mkdir "D:\\AI Powered CICD Optimization\\NexPay\\logs"
                )

                copy /Y "%WORKSPACE%\\ai-build-log.txt" "D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt"

                if %ERRORLEVEL% NEQ 0 (
                    echo ERROR: Failed to copy AI build log.
                    exit /b 1
                )

                echo.
                echo AI build log copied successfully.
                echo Location:
                echo D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt
            '''
        }

        success {
            echo 'NexPay CI: Required builds/tests and SonarCloud analysis completed successfully.'
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}