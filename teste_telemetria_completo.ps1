# Script para testar telemetria em ambiente real
Write-Host "=== Teste de Telemetria - Aplicação Real ===" -ForegroundColor Green

# Definir JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.2"
Write-Host "JAVA_HOME definido: $env:JAVA_HOME" -ForegroundColor Yellow

# Iniciar aplicação em background
Write-Host "Iniciando aplicação Quarkus..." -ForegroundColor Yellow
$quarkusProcess = Start-Process -FilePath "cmd" -ArgumentList "/c", "set `"JAVA_HOME=C:\Program Files\Java\jdk-21.0.2`" && mvn quarkus:dev" -WindowStyle Minimized -PassThru

# Aguardar inicialização
Write-Host "Aguardando inicialização da aplicação (60 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 60

try {
    # Testar se aplicação está rodando
    Write-Host "Testando conectividade..." -ForegroundColor Yellow
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:9090/q/health" -Method GET -TimeoutSec 5
    Write-Host "✅ Aplicação está rodando!" -ForegroundColor Green
    
    # Fazer algumas chamadas para gerar métricas
    Write-Host "Gerando métricas através de chamadas aos endpoints..." -ForegroundColor Yellow
    
    # Simular chamadas (algumas podem falhar por autenticação, mas isso é esperado)
    $headers = @{ "Content-Type" = "application/json" }
    
    try {
        Invoke-RestMethod -Uri "http://localhost:9090/produtos" -Method GET -TimeoutSec 5 -Headers $headers
        Write-Host "✅ Chamada para /produtos" -ForegroundColor Green
    } catch {
        Write-Host "⚠️ Erro esperado em /produtos (autenticação): $($_.Exception.Message)" -ForegroundColor Yellow
    }
    
    try {
        Invoke-RestMethod -Uri "http://localhost:9090/perfil-risco/1" -Method GET -TimeoutSec 5 -Headers $headers
        Write-Host "✅ Chamada para /perfil-risco/1" -ForegroundColor Green
    } catch {
        Write-Host "⚠️ Erro esperado em /perfil-risco/1 (autenticação): $($_.Exception.Message)" -ForegroundColor Yellow
    }
    
    # Aguardar processamento das métricas
    Start-Sleep -Seconds 3
    
    # Testar endpoint de telemetria
    Write-Host "Consultando endpoint de telemetria..." -ForegroundColor Yellow
    $telemetria = Invoke-RestMethod -Uri "http://localhost:9090/telemetria" -Method GET -TimeoutSec 10
    
    Write-Host "=== RESULTADO DA TELEMETRIA ===" -ForegroundColor Green
    $telemetria | ConvertTo-Json -Depth 3
    
    if ($telemetria.servicos -and $telemetria.servicos.Count -gt 0) {
        Write-Host "✅ Telemetria funcionando! Encontrados $($telemetria.servicos.Count) serviços." -ForegroundColor Green
        foreach ($servico in $telemetria.servicos) {
            Write-Host "  📊 $($servico.nome): $($servico.contador_execucao) chamadas, tempo médio: $($servico.tempo_medio_resposta)ms" -ForegroundColor Cyan
        }
    } else {
        Write-Host "❌ Nenhum serviço encontrado na telemetria" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ Erro ao testar aplicação: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    # Parar aplicação
    Write-Host "Parando aplicação..." -ForegroundColor Yellow
    if ($quarkusProcess -and !$quarkusProcess.HasExited) {
        $quarkusProcess | Stop-Process -Force
        Write-Host "✅ Aplicação parada." -ForegroundColor Green
    }
}

Write-Host "=== Teste finalizado ===" -ForegroundColor Green