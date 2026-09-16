pipeline {
    agent any

    stages {

        stage('Build & Test - Bank Account') {
            steps {
                dir('services/bank-account-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build & Test - User') {
            steps {
                dir('services/user-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build & Test - Connection') {
            steps {
                dir('services/nexpay-connection-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build & Test - Transaction') {
            steps {
                dir('services/nexpay-transaction-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Build & Test - Payment') {
            steps {
                dir('services/nexpay-payment-service') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }
    }

    post {
        success {
            echo 'NexPay CI: All services passed successfully.'
        }

        failure {
            echo 'NexPay CI: One or more services failed.'
        }

        always {
            echo 'NexPay CI pipeline completed.'
        }
    }
}