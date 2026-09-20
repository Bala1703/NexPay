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
                }
            }
        }

        stage('Build Common') {
            when {
                expression {
                    return env.CHANGED_FILES.contains('common/')
                }
            }
            steps {
                dir('common') {
                    bat 'mvnw.cmd clean install -DskipTests'
                }
            }
        }

        stage('Build Bank') {
            when {
                expression {
                    return env.CHANGED_FILES.contains('services/bank-account-service/')
                }
            }
            steps {
                dir('services/bank-account-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build User') {
            when {
                expression {
                    return env.CHANGED_FILES.contains('services/user-service/')
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
                    return env.CHANGED_FILES.contains('services/nexpay-connection-service/')
                }
            }
            steps {
                dir('services/nexpay-connection-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build Transaction') {
            when {
                expression {
                    return env.CHANGED_FILES.contains('services/nexpay-transaction-service/')
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
                    return env.CHANGED_FILES.contains('services/nexpay-payment-service/')
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
            echo 'NexPay CI: One or more builds/tests failed'
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}