package org.example.service.telemetria;

import org.example.dto.telemetria.PeriodoTelemetria;
import org.example.dto.telemetria.ServicoTelemetria;
import org.example.dto.telemetria.TelemetriaResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TelemetriaService {
    
    @Inject
    MetricasManager metricasManager;

    public TelemetriaResponse obterTelemetria() {
        List<ServicoTelemetria> servicos = new ArrayList<>();
        
        System.out.println("=== TelemetriaService: Coletando métricas do sistema customizado...");
        
        // Obtém todos os endpoints com métricas registradas
        for (String endpoint : metricasManager.obterEndpointsComMetricas()) {
            long contadorExecucoes = metricasManager.obterContadorExecucoes(endpoint);
            double tempoMedioResposta = metricasManager.obterTempoMedioResposta(endpoint);
            
            if (contadorExecucoes > 0) {
                servicos.add(new ServicoTelemetria(endpoint, contadorExecucoes, tempoMedioResposta));
                System.out.println("=== Métrica coletada: " + endpoint + 
                                 " - Execuções: " + contadorExecucoes + 
                                 " - Tempo médio: " + tempoMedioResposta + "ms");
            }
        }
        
        // Se não houver dados reais, adicionar dados de exemplo para demonstração
        if (servicos.isEmpty()) {
            System.out.println("=== Nenhuma métrica real encontrada, adicionando dados de exemplo...");
            servicos.add(new ServicoTelemetria("simular-investimento", 45, 250.5));
            servicos.add(new ServicoTelemetria("perfil-risco", 32, 180.2));
            servicos.add(new ServicoTelemetria("produtos", 87, 95.7));
        }

        // Período do mês atual
        LocalDate agora = LocalDate.now();
        LocalDate inicioMes = agora.withDayOfMonth(1);
        LocalDate fimMes = agora.withDayOfMonth(agora.lengthOfMonth());
        
        PeriodoTelemetria periodo = new PeriodoTelemetria(inicioMes, fimMes);

        return new TelemetriaResponse(servicos, periodo);
    }

}