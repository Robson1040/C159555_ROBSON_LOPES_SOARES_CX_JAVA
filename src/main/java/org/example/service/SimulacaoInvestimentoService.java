package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.*;
import org.example.mapper.ProdutoMapper;
import org.example.model.*;
import org.example.service.simulacao.SimuladorIndices;
import org.example.service.simulacao.SimuladorMercado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoInvestimentoService {

    @Inject
    ProdutoService produtoService;

    @Inject
    ProdutoMapper produtoMapper;

    @Inject
    SimuladorIndices simuladorIndices;

    @Inject
    SimuladorMercado simuladorMercado;

    /**
     * Realiza a simulação de investimento e persiste o resultado
     */
    @Transactional
    public SimulacaoResponse simularInvestimento(SimulacaoRequest request) {
        // 1. Encontrar o produto mais apropriado
        Produto produto = encontrarProdutoMaisApropriado(request);
        
        if (produto == null) {
            throw new RuntimeException("Nenhum produto encontrado com os critérios informados");
        }

        // 2. Calcular a simulação
        ResultadoSimulacao resultado = calcularSimulacao(request, produto);

        // 3. Persistir a simulação no banco de dados
        SimulacaoInvestimento simulacaoPersistida = persistirSimulacao(request, produto, resultado);

        // 4. Montar a resposta
        ProdutoResponse produtoResponse = produtoMapper.toResponse(produto);
        
        return new SimulacaoResponse(
                produtoResponse,
                resultado,
                LocalDateTime.now(),
                request.getClienteId(),
                simulacaoPersistida.id // Incluir ID da simulação persistida
        );
    }

    /**
     * Encontra o produto mais apropriado baseado nos filtros da request
     */
    private Produto encontrarProdutoMaisApropriado(SimulacaoRequest request) {
        List<Produto> produtos = Produto.listAll();

        // Aplica filtros se informados
        return produtos.stream()
                .filter(produto -> filtrarPorTipo(produto, request.getTipoProduto()))
                .filter(produto -> filtrarPorTipoRentabilidade(produto, request.getTipoRentabilidade()))
                .filter(produto -> filtrarPorIndice(produto, request.getIndice()))
                .filter(produto -> filtrarPorLiquidez(produto, request.getLiquidez()))
                .filter(produto -> filtrarPorFgc(produto, request.getFgc()))
                .filter(produto -> filtrarPorPrazoMinimo(produto, request.getPrazoEmDias()))
                // Ordena por melhor rentabilidade e menor risco
                .sorted((p1, p2) -> compararProdutos(p1, p2))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula o resultado da simulação financeira
     */
    private ResultadoSimulacao calcularSimulacao(SimulacaoRequest request, Produto produto) {
        BigDecimal valorInicial = request.getValor();
        int prazoMeses = request.getPrazoEmMeses();

        // Gera cenário de mercado para o período
        SimuladorMercado.CenarioMercado cenario = simuladorMercado.gerarCenario(produto.getTipo(), prazoMeses);
        
        // Calcula a rentabilidade efetiva com simulação dinâmica
        BigDecimal rentabilidadeEfetiva = calcularRentabilidadeEfetiva(produto, prazoMeses, cenario);
        
        // Calcula o valor final baseado no período de rentabilidade
        BigDecimal valorFinal = calcularValorFinal(valorInicial, rentabilidadeEfetiva, 
                                                  produto.getPeriodoRentabilidade(), prazoMeses);

        BigDecimal rendimento = valorFinal.subtract(valorInicial);

        return new ResultadoSimulacao(
                valorFinal.setScale(2, RoundingMode.HALF_UP),
                rentabilidadeEfetiva.setScale(4, RoundingMode.HALF_UP),
                request.getPrazoMeses(),
                request.getPrazoDias(),
                request.getPrazoAnos(),
                valorInicial,
                rendimento.setScale(2, RoundingMode.HALF_UP),
                true, // valorSimulado = true (valores são simulados dinamicamente)
                cenario.getDescricao() // descrição do cenário simulado
        );
    }

    /**
     * Calcula a rentabilidade efetiva considerando o índice e simulação dinâmica
     */
    private BigDecimal calcularRentabilidadeEfetiva(Produto produto, int prazoMeses, 
                                                   SimuladorMercado.CenarioMercado cenario) {
        BigDecimal rentabilidadeBase = produto.getRentabilidade();

        // Se for pós-fixado e tiver índice, usa simulação dinâmica
        if (TipoRentabilidade.POS.equals(produto.getTipoRentabilidade()) && 
            produto.getIndice() != null && !Indice.NENHUM.equals(produto.getIndice())) {
            
            // Para pós-fixado: usa simulação dinâmica do índice
            // Ex: 105% do CDI simulado = rentabilidade * taxa_simulada_do_cdi
            BigDecimal taxaIndiceSimulada = simuladorIndices.getTaxaSimulada(produto.getIndice(), prazoMeses);
            
            // Aplica o cenário de mercado à taxa simulada
            BigDecimal taxaAjustada = simuladorMercado.ajustarRentabilidadePorCenario(
                taxaIndiceSimulada, cenario, produto.getTipoRentabilidade()
            );
            
            return rentabilidadeBase.multiply(taxaAjustada).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        }

        // Para pré-fixado, ainda aplica o cenário de mercado
        return simuladorMercado.ajustarRentabilidadePorCenario(
            rentabilidadeBase, cenario, produto.getTipoRentabilidade()
        );
    }

    /**
     * Calcula o valor final baseado na rentabilidade e período
     */
    private BigDecimal calcularValorFinal(BigDecimal valorInicial, BigDecimal rentabilidade, 
                                        PeriodoRentabilidade periodo, int prazoMeses) {
        
        BigDecimal taxaDecimal = rentabilidade.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        
        switch (periodo) {
            case AO_DIA:
                // Juros compostos diários
                int dias = prazoMeses * 30;
                BigDecimal taxaDiaria = taxaDecimal.divide(new BigDecimal("365"), 8, RoundingMode.HALF_UP);
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaDiaria).pow(dias)
                );
                
            case AO_MES:
                // Juros compostos mensais
                BigDecimal taxaMensal = taxaDecimal.divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaMensal).pow(prazoMeses)
                );
                
            case AO_ANO:
                // Juros compostos anuais
                double anos = prazoMeses / 12.0;
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaDecimal).pow((int)Math.ceil(anos))
                );
                
            case PERIODO_TOTAL:
                // Rentabilidade sobre o período total
                return valorInicial.multiply(BigDecimal.ONE.add(taxaDecimal));
                
            default:
                return valorInicial.multiply(BigDecimal.ONE.add(taxaDecimal));
        }
    }

    // Métodos de filtro
    private boolean filtrarPorTipo(Produto produto, TipoProduto tipo) {
        return tipo == null || produto.getTipo().equals(tipo);
    }

    private boolean filtrarPorTipoRentabilidade(Produto produto, TipoRentabilidade tipo) {
        return tipo == null || produto.getTipoRentabilidade().equals(tipo);
    }

    private boolean filtrarPorIndice(Produto produto, Indice indice) {
        return indice == null || produto.getIndice().equals(indice);
    }

    private boolean filtrarPorLiquidez(Produto produto, Integer liquidez) {
        return liquidez == null || produto.getLiquidez().equals(liquidez);
    }

    private boolean filtrarPorFgc(Produto produto, Boolean fgc) {
        return fgc == null || produto.getFgc().equals(fgc);
    }

    private boolean filtrarPorPrazoMinimo(Produto produto, int prazoDias) {
        return produto.getMinimoDiasInvestimento() <= prazoDias;
    }

    /**
     * Compara produtos para encontrar o melhor (maior rentabilidade, menor risco)
     */
    private int compararProdutos(Produto p1, Produto p2) {
        // Prioridade: 1. Menor risco, 2. Maior rentabilidade
        int risco1 = p1.getRisco().ordinal();
        int risco2 = p2.getRisco().ordinal();
        
        if (risco1 != risco2) {
            return Integer.compare(risco1, risco2); // Menor risco primeiro
        }
        
        // Se mesmo risco, maior rentabilidade primeiro
        return p2.getRentabilidade().compareTo(p1.getRentabilidade());
    }

    /**
     * Persiste a simulação realizada no banco de dados
     */
    private SimulacaoInvestimento persistirSimulacao(SimulacaoRequest request, 
                                                   Produto produto, 
                                                   ResultadoSimulacao resultado) {
        
        SimulacaoInvestimento simulacao = SimulacaoInvestimento.fromSimulacao(
                request.getClienteId(),
                produto.getNome(),
                request.getValor(),
                resultado
        );
        
        // Persiste no banco
        simulacao.persist();
        
        return simulacao;
    }

    /**
     * Busca histórico de simulações de um cliente
     */
    public List<SimulacaoInvestimento> buscarSimulacoesPorCliente(Long clienteId) {
        return SimulacaoInvestimento.findByClienteIdOrderByDate(clienteId);
    }

    /**
     * Busca uma simulação específica por ID
     */
    public SimulacaoInvestimento buscarSimulacaoPorId(Long id) {
        return SimulacaoInvestimento.findById(id);
    }

    /**
     * Estatísticas de simulações por cliente
     */
    public record EstatisticasCliente(
            Long totalSimulacoes,
            BigDecimal totalInvestido,
            BigDecimal mediaValorInvestido,
            SimulacaoInvestimento ultimaSimulacao
    ) {}

    public EstatisticasCliente getEstatisticasCliente(Long clienteId) {
        long totalSimulacoes = SimulacaoInvestimento.countByClienteId(clienteId);
        BigDecimal totalInvestido = SimulacaoInvestimento.getTotalInvestidoByClienteId(clienteId);
        BigDecimal mediaValorInvestido = totalSimulacoes > 0 ? 
                totalInvestido.divide(BigDecimal.valueOf(totalSimulacoes), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        SimulacaoInvestimento ultimaSimulacao = SimulacaoInvestimento.findLastByClienteId(clienteId);

        return new EstatisticasCliente(totalSimulacoes, totalInvestido, mediaValorInvestido, ultimaSimulacao);
    }
}