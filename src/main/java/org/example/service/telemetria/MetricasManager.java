package org.example.service.telemetria;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@ApplicationScoped
public class MetricasManager {
    
    // Armazena contadores de execução por endpoint
    private final ConcurrentHashMap<String, AtomicLong> contadores = new ConcurrentHashMap<>();
    
    // Armazena tempos totais de resposta por endpoint
    private final ConcurrentHashMap<String, LongAdder> temposTotais = new ConcurrentHashMap<>();
    
    // Armazena número total de chamadas para calcular média
    private final ConcurrentHashMap<String, AtomicLong> totalChamadas = new ConcurrentHashMap<>();
    
    /**
     * Incrementa o contador de execução para um endpoint
     */
    public void incrementarContador(String endpoint) {
        if (endpoint != null && !endpoint.isEmpty()) {
            contadores.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            System.out.println("=== MetricasManager: Contador incrementado para " + endpoint + 
                             " -> Total: " + contadores.get(endpoint).get());
        }
    }
    
    /**
     * Registra o tempo de resposta para um endpoint
     */
    public void registrarTempoResposta(String endpoint, long tempoMs) {
        if (endpoint != null && !endpoint.isEmpty()) {
            temposTotais.computeIfAbsent(endpoint, k -> new LongAdder()).add(tempoMs);
            totalChamadas.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            long total = totalChamadas.get(endpoint).get();
            double media = (double) temposTotais.get(endpoint).sum() / total;
            
            System.out.println("=== MetricasManager: Tempo registrado para " + endpoint + 
                             " -> " + tempoMs + "ms, Média atual: " + media + "ms");
        }
    }
    
    /**
     * Obtém o contador de execuções para um endpoint
     */
    public long obterContadorExecucoes(String endpoint) {
        AtomicLong contador = contadores.get(endpoint);
        return contador != null ? contador.get() : 0;
    }
    
    /**
     * Obtém o tempo médio de resposta para um endpoint
     */
    public double obterTempoMedioResposta(String endpoint) {
        LongAdder tempoTotal = temposTotais.get(endpoint);
        AtomicLong chamadas = totalChamadas.get(endpoint);
        
        if (tempoTotal != null && chamadas != null && chamadas.get() > 0) {
            return (double) tempoTotal.sum() / chamadas.get();
        }
        
        return 0.0;
    }
    
    /**
     * Lista todos os endpoints que têm métricas registradas
     */
    public java.util.Set<String> obterEndpointsComMetricas() {
        return contadores.keySet();
    }
    
    /**
     * Limpa todas as métricas (útil para testes)
     */
    public void limparMetricas() {
        contadores.clear();
        temposTotais.clear();
        totalChamadas.clear();
        System.out.println("=== MetricasManager: Todas as métricas foram limpas");
    }
}