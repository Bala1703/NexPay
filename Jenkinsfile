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
    }

    post {
        success {
            echo 'NexPay CI: Required builds/tests passed successfully.'
        }

        failure {
            echo 'NexPay CI: One or more builds/tests failed.'
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}