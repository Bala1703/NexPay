pipeline {
    agent any

    stages {

        stage('Detect Changes') {
            steps {
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

                    echo "BUILD_COMMON = ${env.BUILD_COMMON}"
                    echo "BUILD_USER = ${env.BUILD_USER}"
                    echo "BUILD_CONNECTION = ${env.BUILD_CONNECTION}"
                    echo "BUILD_BANK = ${env.BUILD_BANK}"
                    echo "BUILD_TRANSACTION = ${env.BUILD_TRANSACTION}"
                    echo "BUILD_PAYMENT = ${env.BUILD_PAYMENT}"
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
                dir('common') {
                    bat 'mvnw.cmd clean install -DskipTests'
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
                dir('services/user-service') {
                    bat 'mvnw.cmd clean test'
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
                dir('services/nexpay-connection-service') {
                    bat 'mvnw.cmd clean test'
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
                dir('services/bank-account-service') {
                    bat 'mvnw.cmd clean test'
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
                dir('services/nexpay-transaction-service') {
                    bat 'mvnw.cmd clean test'
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
                dir('services/nexpay-payment-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        /*
         * TEMPORARY TEST STAGE
         *
         * This intentionally fails the pipeline so that
         * post -> failure can be tested.
         *
         * Remove this stage after log-file testing is complete.
         */
        stage('Test AI Failure') {
            steps {
                bat 'exit /b 1'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
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

    post {

        success {
            echo 'NexPay CI: Required builds/tests and SonarCloud analysis completed successfully.'
        }

        failure {
            echo 'NexPay CI: Build failed. Capturing Jenkins console log.'

            script {

                // Get up to 2,000 lines from the Jenkins console log
                def buildLog = currentBuild.rawBuild
                        .getLog(2000)
                        .join('\n')

                // Desired log directory
                def logDirectory =
                        'D:\\AI Powered CICD Optimization\\NexPay\\logs'

                // Create the directory if it does not exist
                bat """
                    if not exist "${logDirectory}" (
                        mkdir "${logDirectory}"
                    )
                """

                // Write Jenkins console log to the desired location
                writeFile(
                    file: 'ai-build-log.txt',
                    text: buildLog
                )

                bat """
                    copy /Y "ai-build-log.txt" "${logDirectory}\\ai-build-log.txt"
                """

                echo 'Jenkins console log saved successfully.'
                echo "Log location: ${logDirectory}\\ai-build-log.txt"
            }
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}