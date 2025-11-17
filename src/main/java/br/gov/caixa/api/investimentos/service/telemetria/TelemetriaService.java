package br.gov.caixa.api.investimentos.service.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.PeriodoTelemetria;
import br.gov.caixa.api.investimentos.dto.telemetria.ServicoTelemetria;
import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
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
        
        
        
        // Obtém todos os endpoints com métricas registradas
        for (String endpoint : metricasManager.obterEndpointsComMetricas()) {
            long contadorExecucoes = metricasManager.obterContadorExecucoes(endpoint);
            double tempoMedioResposta = metricasManager.obterTempoMedioResposta(endpoint);
            
            if (contadorExecucoes > 0) {
                servicos.add(new ServicoTelemetria(endpoint, contadorExecucoes, tempoMedioResposta));
                
            }
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
                
                
                return new PeriodoTelemetria(inicioFormatado, fimFormatado);
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter período do banco: " + e.getMessage());
        }
        
        
        return null;
    }

}