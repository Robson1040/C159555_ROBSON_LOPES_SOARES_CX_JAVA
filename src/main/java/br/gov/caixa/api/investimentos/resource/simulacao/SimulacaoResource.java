package br.gov.caixa.api.investimentos.resource.simulacao;

import br.gov.caixa.api.investimentos.mapper.SimulacaoInvestimentoMapper;
import br.gov.caixa.api.investimentos.service.simulacao.SimulacaoInvestimentoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoDataDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoMesDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoAnoDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponseDTO;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class SimulacaoResource {

    @Inject
    SimulacaoInvestimentoService simulacaoInvestimentoService;

    @Inject
    SimulacaoInvestimentoMapper simulacaoMapper;

    @GET
    public Response listarTodasSimulacoes() {
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.listarTodasSimulacoes();

        List<SimulacaoResponseDTO> response = simulacoes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    
    @GET
    @Path("/por-produto-dia")
    public Response agruparPorProdutoEDia() {
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.listarTodasSimulacoes();
        
        
        Map<String, Map<java.time.LocalDate, List<SimulacaoInvestimento>>> agrupamento = 
            simulacoes.stream()
                .filter(s -> s != null && s.getProduto() != null && s.getDataSimulacao() != null) 
                .collect(Collectors.groupingBy(
                    s -> s.getProduto(),
                    Collectors.groupingBy(s -> s.getDataSimulacao().toLocalDate())
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
                            .map(s -> s.getValorInvestido())
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal somaValorFinal = simulacoesDoDia.stream()
                            .map(s -> s.getValorFinal())
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
                
                int produtoComparison = a.produto().compareTo(b.produto());
                return produtoComparison != 0 ? produtoComparison : a.data().compareTo(b.data());
            })
            .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    
    @GET
    @Path("/por-produto-mes")
    public Response agruparPorProdutoEAnoMes() {
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.listarTodasSimulacoes();

        
        Map<String, Map<java.time.YearMonth, List<SimulacaoInvestimento>>> agrupamento =
                simulacoes.stream()
                        .filter(s -> s != null && s.getProduto() != null && s.getDataSimulacao() != null)
                        .collect(Collectors.groupingBy(
                                s -> s.getProduto(),
                                Collectors.groupingBy(s -> java.time.YearMonth.from(s.getDataSimulacao()))
                        ));

        List<AgrupamentoProdutoMesDTO> response = agrupamento.entrySet().stream()
                .flatMap(produtoEntry -> {
                    String produto = produtoEntry.getKey();
                    return produtoEntry.getValue().entrySet().stream()
                            .map(anoMesEntry -> {
                                java.time.YearMonth anoMes = anoMesEntry.getKey();
                                List<SimulacaoInvestimento> simulacoesDoPeriodo = anoMesEntry.getValue();

                                int quantidadeSimulacoes = simulacoesDoPeriodo.size();

                                java.math.BigDecimal somaValorInvestido = simulacoesDoPeriodo.stream()
                                        .map(s -> s.getValorInvestido())
                                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                                java.math.BigDecimal somaValorFinal = simulacoesDoPeriodo.stream()
                                        .map(s -> s.getValorFinal())
                                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                                java.math.BigDecimal mediaValorInvestido = quantidadeSimulacoes > 0 ?
                                        somaValorInvestido.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                                        java.math.BigDecimal.ZERO;

                                java.math.BigDecimal mediaValorFinal = quantidadeSimulacoes > 0 ?
                                        somaValorFinal.divide(java.math.BigDecimal.valueOf(quantidadeSimulacoes), 2, java.math.RoundingMode.HALF_UP) :
                                        java.math.BigDecimal.ZERO;

                                
                                return new AgrupamentoProdutoMesDTO(
                                        produto,
                                        anoMes,
                                        Long.valueOf(quantidadeSimulacoes),
                                        mediaValorInvestido,
                                        mediaValorFinal
                                );
                            });
                })
                .sorted((a, b) -> {
                    
                    int produtoComparison = a.produto().compareTo(b.produto());
                    if (produtoComparison != 0) return produtoComparison;
                    return a.mes().compareTo(b.mes()); 
                })
                .collect(Collectors.toList());

        return Response.ok(response).build();
    }

    
    @GET
    @Path("/por-produto-ano")
    public Response agruparPorProdutoEAno() {
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.listarTodasSimulacoes();
        
        
        Map<String, Map<java.time.Year, List<SimulacaoInvestimento>>> agrupamento = 
            simulacoes.stream()
                .filter(s -> s != null && s.getProduto() != null && s.getDataSimulacao() != null) 
                .collect(Collectors.groupingBy(
                    s -> s.getProduto(),
                    Collectors.groupingBy(s -> java.time.Year.from(s.getDataSimulacao()))
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
                            .map(s -> s.getValorInvestido())
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        java.math.BigDecimal somaValorFinal = simulacoesDoAno.stream()
                            .map(s -> s.getValorFinal())
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
                
                int produtoComparison = a.produto().compareTo(b.produto());
                return produtoComparison != 0 ? produtoComparison : a.ano().compareTo(b.ano());
            })
            .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }

    
    private SimulacaoResponseDTO toResponseDTO(SimulacaoInvestimento simulacao) {
        return new SimulacaoResponseDTO(
                simulacao.getId(),
                simulacao.getClienteId(),
                simulacao.getProduto(),
                simulacao.getValorInvestido(),
                simulacao.getValorFinal(),
                simulacao.getPrazoMeses(),
                simulacao.getPrazoDias(),
                simulacao.getPrazoAnos(),
                simulacao.getDataSimulacao()
        );
    }

}

