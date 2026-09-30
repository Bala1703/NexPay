pipeline {
    agent any

    stages {

        stage('Prepare AI Log') {
            steps {
                bat '''
                    if exist "%WORKSPACE%\\ai-build-log.txt" (
                        del /F /Q "%WORKSPACE%\\ai-build-log.txt"
                    )
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

                    env.CHANGED_FILES = changedFiles

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
                dir('common') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                dir('services/user-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                dir('services/nexpay-connection-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                dir('services/bank-account-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                dir('services/nexpay-transaction-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                dir('services/nexpay-payment-service') {
                    tee("${env.WORKSPACE}\\ai-build-log.txt") {
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
                    withSonarQubeEnv('SonarCloud') {
                        bat '''
                            common\\mvnw.cmd -f pom.xml verify ^
                            org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
                            -Dsonar.organization=bala1703 ^
                            -Dsonar.projectKey=Bala17_NexPay
                        '''
                    }
                }
            }
        }
    }

    post {

        failure {

            echo 'NexPay CI: Build failed.'

            bat '''
                echo.
                echo ===== AI BUILD LOG =====
                echo.

                if exist "%WORKSPACE%\\ai-build-log.txt" (
                    type "%WORKSPACE%\\ai-build-log.txt"
                ) else (
                    echo ERROR: ai-build-log.txt was not created.
                    exit /b 1
                )

                if not exist "D:\\AI Powered CICD Optimization\\NexPay\\logs" (
                    mkdir "D:\\AI Powered CICD Optimization\\NexPay\\logs"
                )

                copy /Y "%WORKSPACE%\\ai-build-log.txt" "D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt"

                echo.
                echo AI build log copied to:
                echo D:\\AI Powered CICD Optimization\\NexPay\\logs\\ai-build-log.txt
            '''

            echo 'Checking AI Analyzer connection...'

            /*
             * ONLY the connection to AI Analyzer has a 60-second limit.
             *
             * This does NOT limit:
             * - Maven builds
             * - Tests
             * - SonarCloud
             * - Ollama
             * - Qwen3 response generation
             */

            timeout(time: 1, unit: 'MINUTES') {

                powershell '''
                    $hostName = "localhost"
                    $port = 8200

                    Write-Host ""
                    Write-Host "===== AI ANALYZER CONNECTION CHECK ====="
                    Write-Host "Host: $hostName"
                    Write-Host "Port: $port"
                    Write-Host "Connection timeout: 60 seconds"
                    Write-Host ""

                    $client = New-Object System.Net.Sockets.TcpClient

                    try {

                        $asyncResult = $client.BeginConnect(
                            $hostName,
                            $port,
                            $null,
                            $null
                        )

                        $connected = $asyncResult.AsyncWaitHandle.WaitOne(
                            60000
                        )

                        if (-not $connected) {

                            Write-Host ""
                            Write-Host "===== AI ANALYZER CONNECTION TIMEOUT ====="
                            Write-Host "AI Analyzer did not accept a connection within 60 seconds."
                            Write-Host "Expected: http://localhost:8200"
                            Write-Host "==========================================="
                            Write-Host ""

                            $client.Close()
                            exit 1
                        }

                        $client.EndConnect($asyncResult)

                        Write-Host "AI Analyzer connection established."
                        Write-Host ""

                    }
                    catch {

                        Write-Host ""
                        Write-Host "===== AI ANALYZER CONNECTION FAILED ====="
                        Write-Host $_.Exception.Message
                        Write-Host "=========================================="
                        Write-Host ""

                        $client.Close()
                        exit 1
                    }
                    finally {

                        if ($client) {
                            $client.Close()
                        }
                    }
                '''

                echo 'AI Analyzer is reachable. Sending build log...'

                powershell '''
                    $logFile = "$env:WORKSPACE\\ai-build-log.txt"
                    $aiAnalyzerUrl = "http://localhost:8200/api/analyze"

                    if (-not (Test-Path $logFile)) {
                        Write-Error "AI build log not found: $logFile"
                        exit 1
                    }

                    $buildLog = Get-Content -Path $logFile -Raw

                    $requestBody = @{
                        serviceName = "NexPay"
                        buildNumber = [int]$env:BUILD_NUMBER
                        buildLog = $buildLog
                    } | ConvertTo-Json -Depth 10

                    Write-Host ""
                    Write-Host "===== SENDING LOG TO AI ANALYZER ====="
                    Write-Host "URL: $aiAnalyzerUrl"
                    Write-Host "AI processing timeout: NONE"
                    Write-Host ""

                    try {

                        $response = Invoke-RestMethod `
                            -Uri $aiAnalyzerUrl `
                            -Method POST `
                            -ContentType "application/json" `
                            -Body $requestBody

                        Write-Host ""
                        Write-Host "===== AI ANALYSIS RESULT ====="
                        $response | ConvertTo-Json -Depth 10
                        Write-Host "================================"
                        Write-Host ""

                    }
                    catch {

                        Write-Host ""
                        Write-Host "===== AI ANALYZER ERROR ====="
                        Write-Host $_.Exception.Message
                        Write-Host "=============================="
                        Write-Host ""

                        exit 1
                    }
                '''
            }
        }

        success {

            echo 'NexPay CI: Required builds/tests and SonarCloud analysis completed successfully.'
        }

        always {

            echo 'NexPay CI pipeline completed.'
        }
    }
}