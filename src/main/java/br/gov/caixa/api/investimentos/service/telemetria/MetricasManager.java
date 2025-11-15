package br.gov.caixa.api.investimentos.service.telemetria;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@ApplicationScoped
public class MetricasManager {
    
    @Inject
    TelemetriaMetricaRepository telemetriaRepository;
    
    // Armazena contadores de execução por endpoint
    private final ConcurrentHashMap<String, AtomicLong> contadores = new ConcurrentHashMap<>();
    
    // Armazena tempos totais de resposta por endpoint
    private final ConcurrentHashMap<String, LongAdder> temposTotais = new ConcurrentHashMap<>();
    
    // Armazena número total de chamadas para calcular média
    private final ConcurrentHashMap<String, AtomicLong> totalChamadas = new ConcurrentHashMap<>();
    
    /**
     * Incrementa o contador de execução para um endpoint
     * Persiste tanto em memória (cache) quanto no banco de dados
     */
    public void incrementarContador(String endpoint) {
        if (endpoint != null && !endpoint.isEmpty()) {
            // Mantém cache em memória para performance
            contadores.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            // Persiste no banco de dados
            try {
                telemetriaRepository.incrementarContador(endpoint);
                System.out.println("=== MetricasManager: Contador incrementado para " + endpoint + 
                                 " -> Total em cache: " + contadores.get(endpoint).get());
            } catch (Exception e) {
                System.err.println("Erro ao persistir contador no banco: " + e.getMessage());
            }
        }
    }
    
    /**
     * Registra o tempo de resposta para um endpoint
     * Persiste tanto em memória (cache) quanto no banco de dados
     * NÃO incrementa contador (isso é feito separadamente)
     */
    public void registrarTempoResposta(String endpoint, long tempoMs) {
        if (endpoint != null && !endpoint.isEmpty()) {
            // Mantém cache em memória para performance
            temposTotais.computeIfAbsent(endpoint, k -> new LongAdder()).add(tempoMs);
            totalChamadas.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            long total = totalChamadas.get(endpoint).get();
            double media = (double) temposTotais.get(endpoint).sum() / total;
            
            // Persiste no banco de dados (SEM incrementar contador)
            try {
                telemetriaRepository.adicionarTempoExecucao(endpoint, tempoMs);
                System.out.println("=== MetricasManager: Tempo registrado para " + endpoint + 
                                 " -> " + tempoMs + "ms, Média em cache: " + media + "ms");
            } catch (Exception e) {
                System.err.println("Erro ao persistir tempo no banco: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtém o contador de execuções para um endpoint
     * Prioriza dados do banco de dados, fallback para cache em memória
     */
    public long obterContadorExecucoes(String endpoint) {
        try {
            return telemetriaRepository.obterContadorExecucoes(endpoint);
        } catch (Exception e) {
            System.err.println("Erro ao buscar contador no banco, usando cache: " + e.getMessage());
            AtomicLong contador = contadores.get(endpoint);
            return contador != null ? contador.get() : 0;
        }
    }
    
    /**
     * Obtém o tempo médio de resposta para um endpoint
     * Prioriza dados do banco de dados, fallback para cache em memória
     */
    public double obterTempoMedioResposta(String endpoint) {
        try {
            return telemetriaRepository.obterTempoMedioResposta(endpoint);
        } catch (Exception e) {
            System.err.println("Erro ao buscar tempo médio no banco, usando cache: " + e.getMessage());
            LongAdder tempoTotal = temposTotais.get(endpoint);
            AtomicLong chamadas = totalChamadas.get(endpoint);
            
            if (tempoTotal != null && chamadas != null && chamadas.get() > 0) {
                return (double) tempoTotal.sum() / chamadas.get();
            }
            
            return 0.0;
        }
    }
    
    /**
     * Lista todos os endpoints que têm métricas registradas
     * Prioriza dados do banco de dados, fallback para cache em memória
     */
    public java.util.Set<String> obterEndpointsComMetricas() {
        try {
            return telemetriaRepository.obterEndpointsComMetricas();
        } catch (Exception e) {
            System.err.println("Erro ao buscar endpoints no banco, usando cache: " + e.getMessage());
            return contadores.keySet();
        }
    }
    
    /**
     * Limpa todas as métricas (útil para testes)
     * Remove dados tanto do cache quanto do banco de dados
     */
    public void limparMetricas() {
        // Limpa cache em memória
        contadores.clear();
        temposTotais.clear();
        totalChamadas.clear();
        
        // Limpa banco de dados
        try {
            telemetriaRepository.limparTodasMetricas();
            System.out.println("=== MetricasManager: Todas as métricas foram limpas (cache e banco)");
        } catch (Exception e) {
            System.err.println("Erro ao limpar métricas do banco: " + e.getMessage());
            System.out.println("=== MetricasManager: Cache limpo, mas erro no banco");
        }
    }
}