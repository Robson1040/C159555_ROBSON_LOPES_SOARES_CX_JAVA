# Script de teste da telemetria com banco de dados
# Executa algumas chamadas para gerar métricas e consulta os dados

Write-Host "=== TESTE SISTEMA DE TELEMETRIA COM BANCO DE DADOS ===" -ForegroundColor Green

# Configurar URLs base
$baseUrl = "http://localhost:8080"
$telemetriaUrl = "$baseUrl/telemetria"

Write-Host "`n1. Iniciando aplicação Quarkus..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'C:\Users\c159555\IdeaProjects\trabalhoAdaQuarkus2-main\trabalhoAdaQuarkus2-main\API VEICULO SSO'; `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.2'; mvn quarkus:dev" -WindowStyle Minimized

Write-Host "Aguardando inicialização da aplicação (45 segundos)..."
Start-Sleep -Seconds 45

Write-Host "`n2. Gerando métricas com chamadas aos endpoints..." -ForegroundColor Yellow

try {
    # Fazer várias chamadas para gerar métricas
    Write-Host "Chamando /produtos..."
    for ($i = 1; $i -le 5; $i++) {
        try {
            Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
            Write-Host "Chamada $i/5 para /produtos: OK" -ForegroundColor Green
        } catch {
            Write-Host "Chamada $i/5 para /produtos: ERRO" -ForegroundColor Red
        }
        Start-Sleep -Seconds 1
    }

    Write-Host "`nChamando /clientes..."
    for ($i = 1; $i -le 3; $i++) {
        try {
            Invoke-RestMethod -Uri "$baseUrl/clientes" -Method GET -TimeoutSec 10 | Out-Null
            Write-Host "Chamada $i/3 para /clientes: OK" -ForegroundColor Green
        } catch {
            Write-Host "Chamada $i/3 para /clientes: ERRO" -ForegroundColor Red
        }
        Start-Sleep -Seconds 1
    }

    Write-Host "`n3. Consultando métricas básicas..." -ForegroundColor Yellow
    $telemetriaBasica = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
    Write-Host "Telemetria básica:" -ForegroundColor Cyan
    $telemetriaBasica | ConvertTo-Json -Depth 3

    Write-Host "`n4. Consultando métricas detalhadas do banco..." -ForegroundColor Yellow
    $telemetriaDetalhada = Invoke-RestMethod -Uri "$telemetriaUrl/detalhado" -Method GET -TimeoutSec 10
    Write-Host "Métricas detalhadas (do banco de dados):" -ForegroundColor Cyan
    $telemetriaDetalhada | ConvertTo-Json -Depth 3

    Write-Host "`n5. Consultando endpoints mais acessados..." -ForegroundColor Yellow
    $maisAcessados = Invoke-RestMethod -Uri "$telemetriaUrl/mais-acessados/10" -Method GET -TimeoutSec 10
    Write-Host "Endpoints mais acessados:" -ForegroundColor Cyan
    $maisAcessados | ConvertTo-Json -Depth 3

    Write-Host "`n=== TESTE CONCLUÍDO COM SUCESSO ===" -ForegroundColor Green
    Write-Host "Sistema de telemetria com persistência no banco de dados funcionando!" -ForegroundColor Green

} catch {
    Write-Host "`nERRO durante o teste: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Verifique se a aplicação está rodando corretamente." -ForegroundColor Yellow
}

Write-Host "`nPressione qualquer tecla para finalizar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")