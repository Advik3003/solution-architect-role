$ErrorActionPreference = "Stop"

Write-Host "=== Interview Mini Platform Stopper ===" -ForegroundColor Cyan

# Why this exists:
# Developers need one safe command to stop local services and infra cleanly.
# Interview talking point:
# "I treat local developer experience as part of system design quality."

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

function Stop-SpringBootProcessByPattern($pattern) {
    $processes = Get-CimInstance Win32_Process | Where-Object {
        $_.Name -match "java|mvn" -and $_.CommandLine -ne $null -and $_.CommandLine -like "*$pattern*"
    }

    foreach ($proc in $processes) {
        try {
            Write-Host "Stopping process PID=$($proc.ProcessId) pattern=$pattern" -ForegroundColor Yellow
            Stop-Process -Id $proc.ProcessId -ErrorAction SilentlyContinue
        } catch {
            Write-Host "Could not stop PID=$($proc.ProcessId): $($_.Exception.Message)" -ForegroundColor DarkYellow
        }
    }
}

function Stop-ProcessOnPort($port) {
    try {
        $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
        foreach ($conn in $conns) {
            if ($conn.OwningProcess -and $conn.OwningProcess -ne 0) {
                Write-Host "Stopping process PID=$($conn.OwningProcess) on port $port" -ForegroundColor Yellow
                Stop-Process -Id $conn.OwningProcess -ErrorAction SilentlyContinue
            }
        }
    } catch {
        Write-Host "Port cleanup skipped for ${port}: $($_.Exception.Message)" -ForegroundColor DarkYellow
    }
}

# Stop known service runs started by run-local.ps1 (mvn spring-boot:run)
Stop-SpringBootProcessByPattern "discovery-server/pom.xml"
Stop-SpringBootProcessByPattern "gateway/pom.xml"
Stop-SpringBootProcessByPattern "account-service/pom.xml"
Stop-SpringBootProcessByPattern "spring-boot:run"
Stop-ProcessOnPort 8761
Stop-ProcessOnPort 8080
Stop-ProcessOnPort 8081

Write-Host "Stopping Docker infrastructure..." -ForegroundColor Yellow
docker compose down

Write-Host "All local services and infrastructure are stopped." -ForegroundColor Green
