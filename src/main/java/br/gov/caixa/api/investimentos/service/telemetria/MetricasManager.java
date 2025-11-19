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
    
    
    private final ConcurrentHashMap<String, AtomicLong> contadores = new ConcurrentHashMap<>();
    
    
    private final ConcurrentHashMap<String, LongAdder> temposTotais = new ConcurrentHashMap<>();
    
    
    private final ConcurrentHashMap<String, AtomicLong> totalChamadas = new ConcurrentHashMap<>();
    
    
    public void incrementarContador(String endpoint) {
        if (endpoint != null && !endpoint.isEmpty()) {
            
            contadores.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            
            try {
                telemetriaRepository.incrementarContador(endpoint);
                
            } catch (Exception e) {
                System.err.println("Erro ao persistir contador no banco: " + e.getMessage());
            }
        }
    }
    
    
    public void registrarTempoResposta(String endpoint, long tempoMs) {
        if (endpoint != null && !endpoint.isEmpty()) {
            
            temposTotais.computeIfAbsent(endpoint, k -> new LongAdder()).add(tempoMs);
            totalChamadas.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            long total = totalChamadas.get(endpoint).get();
            double media = (double) temposTotais.get(endpoint).sum() / total;
            
            
            try {
                telemetriaRepository.adicionarTempoExecucao(endpoint, tempoMs);
                
            } catch (Exception e) {
                System.err.println("Erro ao persistir tempo no banco: " + e.getMessage());
            }
        }
    }
    
    
    public long obterContadorExecucoes(String endpoint) {
        try {
            return telemetriaRepository.obterContadorExecucoes(endpoint);
        } catch (Exception e) {
            System.err.println("Erro ao buscar contador no banco, usando cache: " + e.getMessage());
            AtomicLong contador = contadores.get(endpoint);
            return contador != null ? contador.get() : 0;
        }
    }
    
    
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
    
    
    public java.util.Set<String> obterEndpointsComMetricas() {
        try {
            return telemetriaRepository.obterEndpointsComMetricas();
        } catch (Exception e) {
            System.err.println("Erro ao buscar endpoints no banco, usando cache: " + e.getMessage());
            return contadores.keySet();
        }
    }
    
    
    public void limparMetricas() {
        
        contadores.clear();
        temposTotais.clear();
        totalChamadas.clear();
        
        
        try {
            telemetriaRepository.limparTodasMetricas();
            
        } catch (Exception e) {
            System.err.println("Erro ao limpar métricas do banco: " + e.getMessage());
            
        }
    }
}