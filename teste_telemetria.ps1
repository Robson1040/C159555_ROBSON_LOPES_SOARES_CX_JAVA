# Script para testar telemetria
Write-Host "Iniciando teste de telemetria..."

# Definir JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.2"

# Compilar projeto
Write-Host "Compilando projeto..."
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilação bem-sucedida!"
    
    # Iniciar aplicação em background
    Write-Host "Iniciando aplicação..."
    $process = Start-Process -FilePath "cmd" -ArgumentList "/c", "set `"JAVA_HOME=C:\Program Files\Java\jdk-21.0.2`" && mvn quarkus:dev" -WindowStyle Hidden -PassThru
    
    # Aguardar inicialização
    Write-Host "Aguardando inicialização da aplicação..."
    Start-Sleep -Seconds 45
    
    # Testar endpoint de telemetria
    Write-Host "Testando endpoint de telemetria..."
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:9090/telemetria" -Method GET
        Write-Host "Resposta da telemetria:"
        $response | ConvertTo-Json -Depth 3
    } catch {
        Write-Host "Erro ao acessar telemetria: $($_.Exception.Message)"
    }
    
    # Matar processo
    Write-Host "Finalizando aplicação..."
    $process | Stop-Process -Force
} else {
    Write-Host "Erro na compilação!"
}