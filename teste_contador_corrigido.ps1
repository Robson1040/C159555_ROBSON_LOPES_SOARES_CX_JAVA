# Script para testar se o contador foi corrigido (incrementa 1 em 1)
Write-Host "=== TESTE CORREÇÃO CONTADOR TELEMETRIA ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"
$telemetriaUrl = "$baseUrl/telemetria"

Write-Host "`nIniciando aplicação..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'C:\Users\c159555\IdeaProjects\trabalhoAdaQuarkus2-main\trabalhoAdaQuarkus2-main\API VEICULO SSO'; `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.2'; mvn quarkus:dev" -WindowStyle Minimized

Write-Host "Aguardando inicialização (30 segundos)..."
Start-Sleep -Seconds 30

try {
    Write-Host "`n1. Limpando métricas antigas..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$telemetriaUrl/limpar" -Method DELETE -TimeoutSec 10 | Out-Null
        Write-Host "Métricas limpas!" -ForegroundColor Green
    } catch {
        Write-Host "Erro ao limpar (aplicação pode não estar pronta ainda)" -ForegroundColor Yellow
    }
    
    Start-Sleep -Seconds 5

    Write-Host "`n2. Fazendo chamadas individuais para /produtos..." -ForegroundColor Yellow
    
    # Primeira chamada
    Write-Host "Chamada 1:"
    try {
        Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
        $telemetria = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        $contador = ($telemetria.endpoints | Where-Object { $_.endpoint -eq "produtos" } | Select-Object -First 1).contadorExecucoes
        Write-Host "  Contador após 1ª chamada: $contador (deve ser 1)" -ForegroundColor $(if($contador -eq 1) {"Green"} else {"Red"})
    } catch {
        Write-Host "  Erro na 1ª chamada: $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Seconds 2

    # Segunda chamada
    Write-Host "Chamada 2:"
    try {
        Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
        $telemetria = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        $contador = ($telemetria.endpoints | Where-Object { $_.endpoint -eq "produtos" } | Select-Object -First 1).contadorExecucoes
        Write-Host "  Contador após 2ª chamada: $contador (deve ser 2)" -ForegroundColor $(if($contador -eq 2) {"Green"} else {"Red"})
    } catch {
        Write-Host "  Erro na 2ª chamada: $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Seconds 2

    # Terceira chamada
    Write-Host "Chamada 3:"
    try {
        Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
        $telemetria = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        $contador = ($telemetria.endpoints | Where-Object { $_.endpoint -eq "produtos" } | Select-Object -First 1).contadorExecucoes
        Write-Host "  Contador após 3ª chamada: $contador (deve ser 3)" -ForegroundColor $(if($contador -eq 3) {"Green"} else {"Red"})
    } catch {
        Write-Host "  Erro na 3ª chamada: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n3. Consultando dados detalhados do banco:" -ForegroundColor Yellow
    try {
        $detalhado = Invoke-RestMethod -Uri "$telemetriaUrl/detalhado" -Method GET -TimeoutSec 10
        $produtosDB = $detalhado | Where-Object { $_.endpoint -eq "produtos" } | Select-Object -First 1
        if ($produtosDB) {
            Write-Host "  Endpoint: $($produtosDB.endpoint)" -ForegroundColor Cyan
            Write-Host "  Contador (banco): $($produtosDB.contadorExecucoes)" -ForegroundColor Cyan
            Write-Host "  Tempo médio: $($produtosDB.tempoMedioResposta)ms" -ForegroundColor Cyan
            Write-Host "  Última atualização: $($produtosDB.ultimaAtualizacao)" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "  Erro ao consultar dados detalhados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n=== RESULTADO ===" -ForegroundColor Green
    Write-Host "Se os contadores estão incrementando 1, 2, 3... a correção funcionou!" -ForegroundColor Green

} catch {
    Write-Host "`nERRO no teste: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nPressione qualquer tecla para finalizar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")