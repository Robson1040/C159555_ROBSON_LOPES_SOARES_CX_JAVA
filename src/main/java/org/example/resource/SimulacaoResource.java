package org.example.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.AgrupamentoProdutoDataDTO;
import org.example.dto.AgrupamentoProdutoMesDTO;
import org.example.dto.AgrupamentoProdutoAnoDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.model.SimulacaoInvestimento;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resource para consultar simulações de investimentos realizadas
 */
@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    /**
     * GET /simulacoes
     * Retorna todas as simulações de investimento realizadas
     */
    @GET
    public Response listarTodasSimulacoes() {
        List<SimulacaoInvestimento> simulacoes = SimulacaoInvestimento.listAll();
        
        List<SimulacaoResponseDTO> response = simulacoes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    /**
     * GET /simulacoes/por-produto-dia
     * Retorna agrupamento de simulações por produto e data
     */
    @GET
    @Path("/por-produto-dia")
    public Response agruparPorProdutoEDia() {
        List<SimulacaoInvestimento> simulacoes = SimulacaoInvestimento.listAll();
        
        // Agrupa por produto e data (sem horário)
        Map<String, Map<java.time.LocalDate, List<SimulacaoInvestimento>>> agrupamento = 
            simulacoes.stream()
                .filter(s -> s != null && s.produto != null && s.dataSimulacao != null) // Filtra simulações nulas, sem produto ou data
                .collect(Collectors.groupingBy(
                    s -> s.produto,
                    Collectors.groupingBy(s -> s.dataSimulacao.toLocalDate())
                ));
        
        List<AgrupamentoProdutoDataDTO> response = agrupamento.entrySet().stream()
            .flatMap(produtoEntry -> {
                String produto = produtoEntry.getKey();
                return produtoEntry.getValue().entrySet().stream()
                    .map(dataEntry -> {
                        java.time.LocalDate data = dataEntry.getKey();
                        List<SimulacaoInvestimento> simulacoesDoDia = dataEntry.getValue();
                        
                        int quantidadeSimulacoes = simulacoesDoDia.size();
                        
                        java.math.BigDecimal somaValorInvestido = simulacoesDoDia.stream()
                            .map(s -> s.valorInvestido)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal somaValorFinal = simulacoesDoDia.stream()
                            .map(s -> s.valorFinal)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal mediaValorInvestido = quantidadeSimulacoes > 0 ? 
                            somaValorInvestido.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                            
                        java.math.BigDecimal mediaValorFinal = quantidadeSimulacoes > 0 ?
                            somaValorFinal.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                        
                        return new AgrupamentoProdutoDataDTO(
                            produto, 
                            data, 
                            Long.valueOf(quantidadeSimulacoes), 
                            mediaValorInvestido,
                            mediaValorFinal
                        );
                    });
            })
            .sorted((a, b) -> {
                // Ordena por produto e depois por data
                int produtoComparison = a.produto().compareTo(b.produto());
                return produtoComparison != 0 ? produtoComparison : a.data().compareTo(b.data());
            })
            .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    /**
     * GET /simulacoes/por-produto-mes
     * Retorna agrupamento de simulações por produto e mês
     */
    @GET
    @Path("/por-produto-mes")
    public Response agruparPorProdutoEMes() {
        List<SimulacaoInvestimento> simulacoes = SimulacaoInvestimento.listAll();
        
        // Agrupa por produto e mês
        Map<String, Map<java.time.YearMonth, List<SimulacaoInvestimento>>> agrupamento = 
            simulacoes.stream()
                .filter(s -> s != null && s.produto != null && s.dataSimulacao != null) // Filtra simulações nulas, sem produto ou data
                .collect(Collectors.groupingBy(
                    s -> s.produto,
                    Collectors.groupingBy(s -> java.time.YearMonth.from(s.dataSimulacao))
                ));
        
        List<AgrupamentoProdutoMesDTO> response = agrupamento.entrySet().stream()
            .flatMap(produtoEntry -> {
                String produto = produtoEntry.getKey();
                return produtoEntry.getValue().entrySet().stream()
                    .map(mesEntry -> {
                        java.time.YearMonth mes = mesEntry.getKey();
                        List<SimulacaoInvestimento> simulacoesDoMes = mesEntry.getValue();
                        
                        int quantidadeSimulacoes = simulacoesDoMes.size();
                        
                        java.math.BigDecimal somaValorInvestido = simulacoesDoMes.stream()
                            .map(s -> s.valorInvestido)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal somaValorFinal = simulacoesDoMes.stream()
                            .map(s -> s.valorFinal)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal mediaValorInvestido = quantidadeSimulacoes > 0 ? 
                            somaValorInvestido.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                            
                        java.math.BigDecimal mediaValorFinal = quantidadeSimulacoes > 0 ?
                            somaValorFinal.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                        
                        return new AgrupamentoProdutoMesDTO(
                            produto, 
                            mes, 
                            Long.valueOf(quantidadeSimulacoes), 
                            mediaValorInvestido,
                            mediaValorFinal
                        );
                    });
            })
            .sorted((a, b) -> {
                // Ordena por produto e depois por mês
                int produtoComparison = a.produto().compareTo(b.produto());
                return produtoComparison != 0 ? produtoComparison : a.mes().compareTo(b.mes());
            })
            .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    /**
     * GET /simulacoes/por-produto-ano
     * Retorna agrupamento de simulações por produto e ano
     */
    @GET
    @Path("/por-produto-ano")
    public Response agruparPorProdutoEAno() {
        List<SimulacaoInvestimento> simulacoes = SimulacaoInvestimento.listAll();
        
        // Agrupa por produto e ano
        Map<String, Map<java.time.Year, List<SimulacaoInvestimento>>> agrupamento = 
            simulacoes.stream()
                .filter(s -> s != null && s.produto != null && s.dataSimulacao != null) // Filtra simulações nulas, sem produto ou data
                .collect(Collectors.groupingBy(
                    s -> s.produto,
                    Collectors.groupingBy(s -> java.time.Year.from(s.dataSimulacao))
                ));
        
        List<AgrupamentoProdutoAnoDTO> response = agrupamento.entrySet().stream()
            .flatMap(produtoEntry -> {
                String produto = produtoEntry.getKey();
                return produtoEntry.getValue().entrySet().stream()
                    .map(anoEntry -> {
                        java.time.Year ano = anoEntry.getKey();
                        List<SimulacaoInvestimento> simulacoesDoAno = anoEntry.getValue();
                        
                        int quantidadeSimulacoes = simulacoesDoAno.size();
                        
                        java.math.BigDecimal somaValorInvestido = simulacoesDoAno.stream()
                            .map(s -> s.valorInvestido)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal somaValorFinal = simulacoesDoAno.stream()
                            .map(s -> s.valorFinal)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal mediaValorInvestido = quantidadeSimulacoes > 0 ? 
                            somaValorInvestido.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                            
                        java.math.BigDecimal mediaValorFinal = quantidadeSimulacoes > 0 ?
                            somaValorFinal.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                            java.math.BigDecimal.ZERO;
                        
                        return new AgrupamentoProdutoAnoDTO(
                            produto, 
                            ano, 
                            Long.valueOf(quantidadeSimulacoes), 
                            mediaValorInvestido,
                            mediaValorFinal
                        );
                    });
            })
            .sorted((a, b) -> {
                // Ordena por produto e depois por ano
                int produtoComparison = a.produto().compareTo(b.produto());
                return produtoComparison != 0 ? produtoComparison : a.ano().compareTo(b.ano());
            })
            .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    /**
     * Converte SimulacaoInvestimento para o formato de resposta
     */
    private SimulacaoResponseDTO toResponseDTO(SimulacaoInvestimento simulacao) {
        return new SimulacaoResponseDTO(
                simulacao.id,
                simulacao.clienteId,
                simulacao.produto,
                simulacao.valorInvestido,
                simulacao.valorFinal,
                simulacao.prazoMeses,
                simulacao.prazoDias,
                simulacao.prazoAnos,
                simulacao.dataSimulacao
        );
    }

}