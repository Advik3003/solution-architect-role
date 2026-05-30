Param(
    [switch]$NoBuild
)

$ErrorActionPreference = "Stop"

Write-Host "=== Interview Mini Platform Local Runner ===" -ForegroundColor Cyan

# Why this exists:
# A single command runner helps interview demos by removing manual startup mistakes.
# Interview talking point:
# "I automated developer onboarding and local reproducibility with one script."

function Assert-Command($name) {
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        throw "Required command not found: $name"
    }
}

Assert-Command "docker"
Assert-Command "mvn"
Assert-Command "java"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

# Ensure stale local processes do not cause port collisions.
powershell -ExecutionPolicy Bypass -File "$root/stop-local.ps1" | Out-Null

$logsDir = Join-Path $root "logs"
if (-not (Test-Path $logsDir)) {
    New-Item -ItemType Directory -Path $logsDir | Out-Null
}

Write-Host "[1/4] Starting infra with docker compose..." -ForegroundColor Yellow
docker compose up -d
if ($LASTEXITCODE -ne 0) {
    throw "docker compose up failed. Fix infra errors before starting services."
}

if (-not $NoBuild) {
    Write-Host "[2/4] Building services..." -ForegroundColor Yellow
    mvn -f "$root/discovery-server/pom.xml" clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Build failed for discovery-server." }
    mvn -f "$root/gateway/pom.xml" clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Build failed for gateway." }
    mvn -f "$root/account-service/pom.xml" clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Build failed for account-service." }
} else {
    Write-Host "[2/4] Skipping build because -NoBuild was provided." -ForegroundColor Yellow
}

Write-Host "[3/4] Starting discovery-server..." -ForegroundColor Yellow
Start-Process -FilePath "mvn" `
    -ArgumentList "-f `"$root/discovery-server/pom.xml`" spring-boot:run -Dspring-boot.run.jvmArguments=-Duser.timezone=UTC" `
    -WorkingDirectory $root `
    -RedirectStandardOutput "$logsDir/discovery-server.out.log" `
    -RedirectStandardError "$logsDir/discovery-server.err.log"

Start-Sleep -Seconds 12

Write-Host "[4/4] Starting gateway and account-service..." -ForegroundColor Yellow
Start-Process -FilePath "mvn" `
    -ArgumentList "-f `"$root/gateway/pom.xml`" spring-boot:run -Dspring-boot.run.jvmArguments=-Duser.timezone=UTC" `
    -WorkingDirectory $root `
    -RedirectStandardOutput "$logsDir/gateway.out.log" `
    -RedirectStandardError "$logsDir/gateway.err.log"

Start-Process -FilePath "mvn" `
    -ArgumentList "-f `"$root/account-service/pom.xml`" spring-boot:run -Dspring-boot.run.jvmArguments=-Duser.timezone=UTC" `
    -WorkingDirectory $root `
    -RedirectStandardOutput "$logsDir/account-service.out.log" `
    -RedirectStandardError "$logsDir/account-service.err.log"

Write-Host ""
Write-Host "Services startup triggered." -ForegroundColor Green
Write-Host "Check logs under: $logsDir"
Write-Host ""
Write-Host "Useful URLs:"
Write-Host "- Eureka:  http://localhost:8761"
Write-Host "- Gateway: http://localhost:8080"
Write-Host "- Account: http://localhost:8081/actuator/health"
Write-Host ""
Write-Host "Interview talking point:"
Write-Host "- This script makes environment setup deterministic and reduces setup time for new engineers."
