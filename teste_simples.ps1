# Script simplificado para testar telemetria
Write-Host "Testando telemetria..."

# Aguardar um pouco
Start-Sleep -Seconds 5

try {
    # Testar endpoint de telemetria diretamente
    $response = Invoke-RestMethod -Uri "http://localhost:9090/telemetria" -Method GET -TimeoutSec 10
    Write-Host "Resposta da telemetria:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Erro: $($_.Exception.Message)"
}