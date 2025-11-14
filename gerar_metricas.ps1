# Script para gerar mais métricas e testar telemetria
Write-Host "Gerando métricas de teste..." -ForegroundColor Green

# Fazer várias chamadas para acumular métricas
for ($i = 1; $i -le 5; $i++) {
    try {
        Write-Host "Chamada $i para /clientes..." -ForegroundColor Yellow
        Invoke-RestMethod -Uri "http://localhost:9090/clientes" -Method GET -TimeoutSec 2
    } catch {
        Write-Host "Erro esperado (sem autenticação): $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Start-Sleep -Milliseconds 500
}

# Aguardar processamento
Start-Sleep -Seconds 2

# Consultar telemetria
Write-Host "Consultando telemetria..." -ForegroundColor Green
try {
    $telemetria = Invoke-RestMethod -Uri "http://localhost:9090/telemetria" -Method GET -TimeoutSec 5
    Write-Host "=== TELEMETRIA COM MÉTRICAS REAIS ===" -ForegroundColor Green
    $telemetria | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Erro ao consultar telemetria: $($_.Exception.Message)" -ForegroundColor Red
}