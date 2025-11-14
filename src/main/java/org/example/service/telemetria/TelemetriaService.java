package org.example.service.telemetria;

import org.example.dto.telemetria.PeriodoTelemetria;
import org.example.dto.telemetria.ServicoTelemetria;
import org.example.dto.telemetria.TelemetriaResponse;
import org.example.repository.telemetria.TelemetriaMetricaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TelemetriaService {
    
    @Inject
    MetricasManager metricasManager;
    
    @Inject
    TelemetriaMetricaRepository telemetriaRepository;

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
        
        // Se não houver métricas, retorna lista vazia
        if (servicos.isEmpty()) {
            System.out.println("=== Nenhuma métrica encontrada, retornando lista vazia");
        }

        // Período baseado nas datas reais do banco de dados
        PeriodoTelemetria periodo = obterPeriodoReal();

        return new TelemetriaResponse(servicos, periodo);
    }
    
    /**
     * Obtém o período real baseado nas datas do banco de dados
     * Retorna null se não houver dados
     */
    private PeriodoTelemetria obterPeriodoReal() {
        try {
            LocalDateTime dataInicio = telemetriaRepository.obterDataInicio();
            LocalDateTime dataFim = telemetriaRepository.obterDataFim();
            
            if (dataInicio != null && dataFim != null) {
                // Formatar as datas como strings no formato ISO (YYYY-MM-DD HH:mm:ss)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String inicioFormatado = dataInicio.format(formatter);
                String fimFormatado = dataFim.format(formatter);
                
                System.out.println("=== Período real: " + inicioFormatado + " até " + fimFormatado);
                return new PeriodoTelemetria(inicioFormatado, fimFormatado);
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter período do banco: " + e.getMessage());
        }
        
        // Se não houver dados no banco, retorna null
        System.out.println("=== Nenhum dado no banco, período será null");
        return null;
    }

}