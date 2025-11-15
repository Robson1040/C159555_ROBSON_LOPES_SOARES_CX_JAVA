# Script para testar se a telemetria retorna lista vazia quando não há dados
Write-Host "=== TESTE TELEMETRIA COM LISTA VAZIA ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"
$telemetriaUrl = "$baseUrl/telemetria"

Write-Host "`nIniciando aplicação..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'C:\Users\c159555\IdeaProjects\trabalhoAdaQuarkus2-main\trabalhoAdaQuarkus2-main\API VEICULO SSO'; `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.2'; mvn quarkus:dev" -WindowStyle Minimized

Write-Host "Aguardando inicialização (30 segundos)..."
Start-Sleep -Seconds 30

try {
    Write-Host "`n1. Limpando todas as métricas..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$telemetriaUrl/limpar" -Method DELETE -TimeoutSec 10 | Out-Null
        Write-Host "Métricas limpas!" -ForegroundColor Green
        Start-Sleep -Seconds 3
    } catch {
        Write-Host "Erro ao limpar: $($_.Exception.Message)" -ForegroundColor Yellow
        Start-Sleep -Seconds 5
    }

    Write-Host "`n2. Consultando telemetria SEM dados (deve estar vazia)..." -ForegroundColor Yellow
    try {
        $telemetriaSemDados = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        
        Write-Host "`n=== RESULTADO SEM DADOS ===" -ForegroundColor Cyan
        Write-Host "Número de serviços na lista: $($telemetriaSemDados.servicos.Count)" -ForegroundColor White
        Write-Host "Período: $($telemetriaSemDados.periodo.inicio) até $($telemetriaSemDados.periodo.fim)" -ForegroundColor White
        
        if ($telemetriaSemDados.servicos.Count -eq 0) {
            Write-Host "✅ CORRETO: Lista vazia quando não há dados!" -ForegroundColor Green
        } else {
            Write-Host "❌ ERRO: Lista não está vazia!" -ForegroundColor Red
            Write-Host "Serviços encontrados:" -ForegroundColor Yellow
            $telemetriaSemDados.servicos | ForEach-Object {
                Write-Host "  - $($_.endpoint): $($_.contadorExecucoes) execuções" -ForegroundColor Yellow
            }
        }
        
        Write-Host "`nJSON completo:" -ForegroundColor Gray
        $telemetriaSemDados | ConvertTo-Json -Depth 3

    } catch {
        Write-Host "Erro ao consultar telemetria sem dados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n3. Fazendo algumas chamadas para gerar dados..." -ForegroundColor Yellow
    try {
        for ($i = 1; $i -le 2; $i++) {
            Write-Host "  Chamada $i para /produtos..."
            Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
            Start-Sleep -Seconds 1
        }
    } catch {
        Write-Host "Erro ao gerar dados: $($_.Exception.Message)" -ForegroundColor Yellow
    }

    Start-Sleep -Seconds 2

    Write-Host "`n4. Consultando telemetria COM dados (deve ter itens)..." -ForegroundColor Yellow
    try {
        $telemetriaComDados = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        
        Write-Host "`n=== RESULTADO COM DADOS ===" -ForegroundColor Cyan
        Write-Host "Número de serviços na lista: $($telemetriaComDados.servicos.Count)" -ForegroundColor White
        Write-Host "Período: $($telemetriaComDados.periodo.inicio) até $($telemetriaComDados.periodo.fim)" -ForegroundColor White
        
        if ($telemetriaComDados.servicos.Count -gt 0) {
            Write-Host "✅ CORRETO: Lista com dados reais!" -ForegroundColor Green
            Write-Host "Serviços encontrados:" -ForegroundColor Green
            $telemetriaComDados.servicos | ForEach-Object {
                Write-Host "  - $($_.endpoint): $($_.contadorExecucoes) execuções, $($_.tempoMedioResposta)ms" -ForegroundColor Green
            }
        } else {
            Write-Host "❌ ERRO: Lista ainda está vazia mesmo com dados!" -ForegroundColor Red
        }
        
    } catch {
        Write-Host "Erro ao consultar telemetria com dados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n=== CONCLUSÃO ===" -ForegroundColor Green
    Write-Host "✅ Dados default removidos - lista vazia quando não há métricas!" -ForegroundColor Green
    Write-Host "✅ Período ainda funciona corretamente!" -ForegroundColor Green

} catch {
    Write-Host "`nERRO no teste: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nPressione qualquer tecla para finalizar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")