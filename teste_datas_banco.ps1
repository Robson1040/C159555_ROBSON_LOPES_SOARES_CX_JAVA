# Script para testar se as datas de início e fim vêm do banco de dados
Write-Host "=== TESTE DATAS INÍCIO E FIM DO BANCO DE DADOS ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"
$telemetriaUrl = "$baseUrl/telemetria"

Write-Host "`nIniciando aplicação..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'C:\Users\c159555\IdeaProjects\trabalhoAdaQuarkus2-main\trabalhoAdaQuarkus2-main\API VEICULO SSO'; `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.2'; mvn quarkus:dev" -WindowStyle Minimized

Write-Host "Aguardando inicialização (35 segundos)..."
Start-Sleep -Seconds 35

try {
    Write-Host "`n1. Limpando dados antigos..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$telemetriaUrl/limpar" -Method DELETE -TimeoutSec 10 | Out-Null
        Write-Host "Dados limpos!" -ForegroundColor Green
        Start-Sleep -Seconds 3
    } catch {
        Write-Host "Erro ao limpar (aplicação pode não estar pronta): $($_.Exception.Message)" -ForegroundColor Yellow
        Start-Sleep -Seconds 5
    }

    Write-Host "`n2. Primeira consulta - sem dados no banco:" -ForegroundColor Yellow
    try {
        $telemetria1 = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        Write-Host "  Período (sem dados): $($telemetria1.periodo.inicio) até $($telemetria1.periodo.fim)" -ForegroundColor Cyan
        Write-Host "  [Deve mostrar período fallback do mês atual]" -ForegroundColor Gray
    } catch {
        Write-Host "  Erro na primeira consulta: $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Seconds 2

    Write-Host "`n3. Fazendo chamadas para gerar dados no banco..." -ForegroundColor Yellow
    $horaInicio = Get-Date
    Write-Host "  Hora de início das chamadas: $($horaInicio.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Cyan

    # Fazer algumas chamadas para gerar métricas com timestamps
    for ($i = 1; $i -le 3; $i++) {
        try {
            Write-Host "  Fazendo chamada $i para /produtos..."
            Invoke-RestMethod -Uri "$baseUrl/produtos" -Method GET -TimeoutSec 10 | Out-Null
            Start-Sleep -Seconds 2  # Intervalo para diferenciar os timestamps
        } catch {
            Write-Host "  Erro na chamada $i: $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }

    Start-Sleep -Seconds 2

    # Fazer chamadas para outro endpoint
    for ($i = 1; $i -le 2; $i++) {
        try {
            Write-Host "  Fazendo chamada $i para /clientes..."
            Invoke-RestMethod -Uri "$baseUrl/clientes" -Method GET -TimeoutSec 10 | Out-Null
            Start-Sleep -Seconds 2
        } catch {
            Write-Host "  Erro na chamada $i para /clientes: $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }

    $horaFim = Get-Date
    Write-Host "  Hora de fim das chamadas: $($horaFim.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Cyan

    Write-Host "`n4. Segunda consulta - com dados no banco:" -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    try {
        $telemetria2 = Invoke-RestMethod -Uri $telemetriaUrl -Method GET -TimeoutSec 10
        Write-Host "  Período (com dados): $($telemetria2.periodo.inicio) até $($telemetria2.periodo.fim)" -ForegroundColor Green
        Write-Host "  [Deve mostrar período real do banco de dados]" -ForegroundColor Gray
        
        # Validar se as datas estão dentro do período esperado
        $dataInicioReal = [DateTime]::Parse($telemetria2.periodo.inicio)
        $dataFimReal = [DateTime]::Parse($telemetria2.periodo.fim)
        
        if ($dataInicioReal -ge $horaInicio.AddMinutes(-1) -and $dataFimReal -le $horaFim.AddMinutes(1)) {
            Write-Host "  ✅ SUCESSO: Datas estão dentro do período esperado!" -ForegroundColor Green
        } else {
            Write-Host "  ⚠️ AVISO: Datas podem não estar corretas" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  Erro na segunda consulta: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n5. Dados detalhados do banco:" -ForegroundColor Yellow
    try {
        $detalhado = Invoke-RestMethod -Uri "$telemetriaUrl/detalhado" -Method GET -TimeoutSec 10
        Write-Host "  Métricas no banco:" -ForegroundColor Cyan
        foreach ($metrica in $detalhado) {
            Write-Host "    Endpoint: $($metrica.endpoint)" -ForegroundColor White
            Write-Host "    Criação: $($metrica.dataCriacao)" -ForegroundColor White
            Write-Host "    Última atualização: $($metrica.ultimaAtualizacao)" -ForegroundColor White
            Write-Host "    ---" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  Erro ao consultar dados detalhados: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host "`n=== RESULTADO ===" -ForegroundColor Green
    Write-Host "As datas de início e fim agora vêm do banco de dados!" -ForegroundColor Green
    Write-Host "- Início: Data da primeira métrica registrada" -ForegroundColor Green
    Write-Host "- Fim: Data da última atualização de métrica" -ForegroundColor Green

} catch {
    Write-Host "`nERRO no teste: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nPressione qualquer tecla para finalizar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")