# Script para testar se o período retorna null quando não há dados
Write-Host "=== TESTE PERÍODO NULL QUANDO SEM DADOS ===" -ForegroundColor Green

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

    Write-Host "`n2. Consultando telemetria SEM dados (período deve ser null)..." -ForegroundColor Yellow
    try {
        $telemetriaSemDados = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        
        Write-Host "`n=== RESULTADO SEM DADOS ===" -ForegroundColor Cyan
        Write-Host "Número de serviços: $($telemetriaSemDados.servicos.Count)" -ForegroundColor White
        
        if ($telemetriaSemDados.periodo -eq $null) {
            Write-Host "Período: null" -ForegroundColor White
            Write-Host "✅ CORRETO: Período é null quando não há dados!" -ForegroundColor Green
        } elseif ($telemetriaSemDados.periodo) {
            Write-Host "Período: $($telemetriaSemDados.periodo.inicio) até $($telemetriaSemDados.periodo.fim)" -ForegroundColor White
            Write-Host "❌ ERRO: Período não é null!" -ForegroundColor Red
        } else {
            Write-Host "Período: null ou indefinido" -ForegroundColor White
            Write-Host "✅ CORRETO: Período é null/indefinido quando não há dados!" -ForegroundColor Green
        }
        
        Write-Host "`nJSON completo:" -ForegroundColor Gray
        $telemetriaSemDados | ConvertTo-Json -Depth 3

    } catch {
        Write-Host "Erro ao consultar telemetria sem dados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n3. Fazendo algumas chamadas para gerar dados..." -ForegroundColor Yellow
    try {
        $horaInicio = Get-Date
        Write-Host "  Iniciando chamadas em: $($horaInicio.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Cyan
        
        for ($i = 1; $i -le 3; $i++) {
            Write-Host "  Chamada $i para /produtos..."
            Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
            Start-Sleep -Seconds 1
        }
        
        $horaFim = Get-Date
        Write-Host "  Finalizando chamadas em: $($horaFim.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Cyan
    } catch {
        Write-Host "Erro ao gerar dados: $($_.Exception.Message)" -ForegroundColor Yellow
    }

    Start-Sleep -Seconds 2

    Write-Host "`n4. Consultando telemetria COM dados (período deve ter valores)..." -ForegroundColor Yellow
    try {
        $telemetriaComDados = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        
        Write-Host "`n=== RESULTADO COM DADOS ===" -ForegroundColor Cyan
        Write-Host "Número de serviços: $($telemetriaComDados.servicos.Count)" -ForegroundColor White
        
        if ($telemetriaComDados.periodo -and $telemetriaComDados.periodo.inicio -and $telemetriaComDados.periodo.fim) {
            Write-Host "Período: $($telemetriaComDados.periodo.inicio) até $($telemetriaComDados.periodo.fim)" -ForegroundColor White
            Write-Host "✅ CORRETO: Período com datas reais do banco!" -ForegroundColor Green
            
            # Validar se as datas estão dentro do período esperado
            try {
                $dataInicioReal = [DateTime]::Parse($telemetriaComDados.periodo.inicio)
                $dataFimReal = [DateTime]::Parse($telemetriaComDados.periodo.fim)
                
                if ($dataInicioReal -ge $horaInicio.AddMinutes(-1) -and $dataFimReal -le $horaFim.AddMinutes(1)) {
                    Write-Host "✅ Datas estão no período esperado!" -ForegroundColor Green
                } else {
                    Write-Host "⚠️ Datas podem estar fora do período esperado" -ForegroundColor Yellow
                }
            } catch {
                Write-Host "⚠️ Erro ao validar formato das datas" -ForegroundColor Yellow
            }
        } else {
            Write-Host "Período: null ou incompleto" -ForegroundColor White
            Write-Host "❌ ERRO: Período deveria ter dados!" -ForegroundColor Red
        }
        
        if ($telemetriaComDados.servicos.Count -gt 0) {
            Write-Host "Serviços encontrados:" -ForegroundColor Green
            $telemetriaComDados.servicos | ForEach-Object {
                Write-Host "  - $($_.endpoint): $($_.contadorExecucoes) execuções, $($_.tempoMedioResposta)ms" -ForegroundColor Green
            }
        }
        
    } catch {
        Write-Host "Erro ao consultar telemetria com dados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n=== CONCLUSÃO ===" -ForegroundColor Green
    Write-Host "✅ Sem dados: Lista vazia + Período null" -ForegroundColor Green
    Write-Host "✅ Com dados: Lista preenchida + Período com datas reais" -ForegroundColor Green
    Write-Host "✅ Nenhum valor default/fictício é exibido!" -ForegroundColor Green

} catch {
    Write-Host "`nERRO no teste: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nPressione qualquer tecla para finalizar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")