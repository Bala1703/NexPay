pipeline {
    agent any

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
                dir('services/ai-cicd-analyzer') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                tee("${env.WORKSPACE}\\ai-build-log.txt") {
                    bat '''
                        common\\mvnw.cmd verify sonar:sonar ^
                        -Dsonar.organization=Bala1703 ^
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

            echo '================================================='
            echo 'BUILD FAILED'
            echo '================================================='

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

            echo 'AI build log copied to: D:/AI Powered CICD Optimization/NexPay/logs/ai-build-log.txt'
            echo 'Sending build log to AI Analyzer...'

            powershell '''
                Write-Host "STEP 1 - PowerShell started"

                $logFile = "$env:WORKSPACE\\ai-build-log.txt"

                Write-Host "STEP 2 - Log file: $logFile"

                if (-not (Test-Path $logFile)) {
                    Write-Host "STEP 3 - LOG FILE NOT FOUND"
                    exit 1
                }

                Write-Host "STEP 3 - Log file exists"

                $buildLog = Get-Content -Path $logFile -Raw

                Write-Host "STEP 4 - Log file read successfully"
                Write-Host "Log size: $($buildLog.Length) characters"

                Write-Host "STEP 5 - Creating request object"

                $requestObject = @{
                    serviceName = "NexPay"
                    buildNumber = [int]$env:BUILD_NUMBER
                    buildLog = $buildLog
                }

                Write-Host "STEP 6 - Request object created"

                Add-Type -AssemblyName System.Web

                $serializer = New-Object System.Web.Script.Serialization.JavaScriptSerializer

                $requestBody = $serializer.Serialize($requestObject)

                Write-Host "STEP 7 - JSON serialization completed"
                Write-Host "JSON size: $($requestBody.Length) characters"

                Write-Host "STEP 8 - Calling AI Analyzer..."
                Write-Host "URL: http://localhost:8200/api/analyze"

                $response = Invoke-RestMethod `
                    -Uri "http://localhost:8200/api/analyze" `
                    -Method POST `
                    -ContentType "application/json" `
                    -Body $requestBody

                Write-Host "STEP 9 - AI Analyzer responded"

                Write-Host "==============================================="
                Write-Host "AI ANALYSIS RESULT"
                Write-Host "==============================================="

                $response | ConvertTo-Json -Depth 10

                Write-Host "==============================================="
                Write-Host "AI ANALYSIS COMPLETED"
                Write-Host "==============================================="
            '''
        }
    }
}