pipeline {
    agent any

    stages {

        stage('Detect Changes') {
            steps {
                tee('ai-build-log.txt') {
                    script {

                        def changedFiles = bat(
                            script: 'git diff --name-only HEAD~1 HEAD',
                            returnStdout: true
                        ).trim()

                        echo "Changed files:"
                        echo changedFiles

                        env.CHANGED_FILES = changedFiles

                        // Default: nothing
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
        }

        stage('Build Common') {
            when {
                expression {
                    env.BUILD_COMMON == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('common') {
                        bat 'mvnw.cmd clean install -DskipTests'
                    }
                }
            }
        }

        stage('Build User') {
            when {
                expression {
                    env.BUILD_USER == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/user-service') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Connection') {
            when {
                expression {
                    env.BUILD_CONNECTION == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/nexpay-connection-service') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Bank') {
            when {
                expression {
                    env.BUILD_BANK == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/bank-account-service') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Transaction') {
            when {
                expression {
                    env.BUILD_TRANSACTION == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/nexpay-transaction-service') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build Payment') {
            when {
                expression {
                    env.BUILD_PAYMENT == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/nexpay-payment-service') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Build AI Analyzer') {
            when {
                expression {
                    env.BUILD_AI_ANALYZER == 'true'
                }
            }
            steps {
                tee('ai-build-log.txt') {
                    dir('services/ai-cicd-analyzer') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        /*
         * TEMPORARY FAILURE TEST
         *
         * Remove this after testing log capture.
         */
        stage('Test AI Failure') {
            steps {
                tee('ai-build-log.txt') {
                    bat 'echo This is a test failure'
                    bat 'exit /b 1'
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                tee('ai-build-log.txt') {
                    withSonarQubeEnv('SonarCloud') {
                        bat '''
                            common\\mvnw.cmd -f pom.xml verify ^
                            org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
                            -Dsonar.organization=bala1703 ^
                            -Dsonar.projectKey=Bala1703_NexPay
                        '''
                    }
                }
            }
        }
    }

    post {

        success {
            echo 'NexPay CI: Required builds/tests and SonarCloud analysis completed successfully.'
        }

        failure {
            echo 'NexPay CI: Build failed. Checking captured AI log.'

            bat '''
                if not exist "D:\\AI Powered CICD Optimization\\NexPay\\logs" (
                    mkdir "D:\\AI Powered CICD Optimization\\NexPay\\logs"
                )

                copy /Y "ai-build-log.txt" "D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt"
            '''

            echo 'Jenkins build log copied successfully.'
            echo 'Log location: D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt'
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}