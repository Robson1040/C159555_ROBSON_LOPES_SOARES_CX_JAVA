package br.gov.caixa.api.investimentos.service.simulacao;

import br.gov.caixa.api.investimentos.ml.GeradorRecomendacaoML;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.mapper.ProdutoMapper;
import br.gov.caixa.api.investimentos.mapper.SimulacaoInvestimentoMapper;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.repository.simulacao.ISimulacaoInvestimentoRepository;
import br.gov.caixa.api.investimentos.service.produto.ProdutoService;
import br.gov.caixa.api.investimentos.exception.produto.NenhumProdutoDisponivelException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class SimulacaoInvestimentoService {

    @Inject
    ProdutoService produtoService;

    @Inject
    ProdutoMapper produtoMapper;

    @Inject
    SimulacaoInvestimentoMapper simulacaoMapper;

    @Inject
    SimuladorIndices simuladorIndices;

    @Inject
    SimuladorMercado simuladorMercado;

    @Inject
    ISimulacaoInvestimentoRepository simulacaoRepository;

    @Inject
    IInvestimentoRepository investimentoRepository;

    @Inject
    IProdutoRepository produtoRepository;

    @Inject
    GeradorRecomendacaoML geradorRecomendacaoML;

    
    @Transactional
    public SimulacaoResponse simularInvestimento(SimulacaoRequest request)
    {
        
        validarRegrasNegocio(request);

        
        List<Produto> produtos = encontrarProdutoMaisApropriado(request);
        List<Produto> produtos_sugeridos = new ArrayList<Produto>();


        List<Investimento> investimentos = investimentoRepository.findByClienteId(request.clienteId());

        if (!investimentos.isEmpty())
        {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(investimentos, produtos);
        }

        if (produtos_sugeridos.isEmpty())
        {
            produtos_sugeridos = geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(investimentos, produtos);
        }

        if (produtos_sugeridos.isEmpty())
        {
            produtos_sugeridos = produtos;
        }

        if (produtos_sugeridos.isEmpty())
        {
            throw new NenhumProdutoDisponivelException("Nenhum produto encontrado com os critérios informados");
        }

        Produto produto = produtos_sugeridos.getFirst();

        
        ResultadoSimulacao resultado = calcularSimulacao(request, produto);

        
        SimulacaoInvestimento simulacaoPersistida = persistirSimulacao(request, produto, resultado);

        
        ProdutoResponse produtoResponse = produtoMapper.toResponse(produto);
        
        return new SimulacaoResponse(
                produtoResponse,
                resultado,
                LocalDateTime.now(),
                request.clienteId(),
                simulacaoPersistida.getId() 
        );
    }

    
    private List<Produto> encontrarProdutoMaisApropriado(SimulacaoRequest request)
    {
        
        if (request.produtoId() != null)
        {
            Produto produto = produtoRepository.findById(request.produtoId());



            if (produto != null) {
                return List.of(produto);
            }
        }

        List<Produto> produtos = produtoRepository.listAll();

        
        return produtos.stream()
                .filter(Objects::nonNull) 
                .filter(produto -> filtrarPorNome(produto, request.nome()))
                .filter(produto -> filtrarPorTipo(produto, request.tipoProduto()))
                .filter(produto -> filtrarPorTipoRentabilidade(produto, request.tipoRentabilidade()))
                .filter(produto -> filtrarPorIndice(produto, request.indice()))
                .filter(produto -> filtrarPorLiquidez(produto, request.liquidez()))
                .filter(produto -> filtrarPorFgc(produto, request.fgc()))
                .filter(produto -> filtrarPorPrazoMinimo(produto, request.getPrazoEmDias()))
                
                .sorted(this::compararProdutos)
                .toList();
    }

    
    private ResultadoSimulacao calcularSimulacao(SimulacaoRequest request, Produto produto) {
        BigDecimal valorInicial = request.valor();
        int prazoMeses = request.getPrazoEmMeses();

        
        SimuladorMercado.CenarioMercado cenario = simuladorMercado.gerarCenario(produto.getTipo(), prazoMeses);
        
        
        BigDecimal rentabilidadeEfetiva = calcularRentabilidadeEfetiva(produto, prazoMeses, cenario);
        
        
        BigDecimal valorFinal = calcularValorFinal(valorInicial, rentabilidadeEfetiva, 
                                                  produto.getPeriodoRentabilidade(), prazoMeses);

        BigDecimal rendimento = valorFinal.subtract(valorInicial);

        return new ResultadoSimulacao(
                valorFinal.setScale(2, RoundingMode.HALF_UP),
                rentabilidadeEfetiva.setScale(4, RoundingMode.HALF_UP),
                request.prazoMeses(),
                request.prazoDias(),
                request.prazoAnos(),
                valorInicial,
                rendimento.setScale(2, RoundingMode.HALF_UP),
                true, 
                cenario.getDescricao() 
        );
    }

    
    private BigDecimal calcularRentabilidadeEfetiva(Produto produto, int prazoMeses, 
                                                   SimuladorMercado.CenarioMercado cenario) {
        BigDecimal rentabilidadeBase = produto.getRentabilidade();

        
        if (TipoRentabilidade.POS.equals(produto.getTipoRentabilidade()) && 
            produto.getIndice() != null && !Indice.NENHUM.equals(produto.getIndice())) {
            
            
            
            BigDecimal taxaIndiceSimulada = simuladorIndices.getTaxaSimulada(produto.getIndice(), prazoMeses);
            
            
            BigDecimal taxaAjustada = simuladorMercado.ajustarRentabilidadePorCenario(
                taxaIndiceSimulada, cenario, produto.getTipoRentabilidade()
            );
            
            return rentabilidadeBase.multiply(taxaAjustada).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        }

        
        return simuladorMercado.ajustarRentabilidadePorCenario(
            rentabilidadeBase, cenario, produto.getTipoRentabilidade()
        );
    }

    
    private BigDecimal calcularValorFinal(BigDecimal valorInicial, BigDecimal rentabilidade,
                                          PeriodoRentabilidade periodo, int prazoMeses) {
        
        BigDecimal taxaDecimal = rentabilidade.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        
        switch (periodo) {
            case AO_DIA:
                
                int dias = prazoMeses * 30;
                BigDecimal taxaDiaria = taxaDecimal.divide(new BigDecimal("365"), 8, RoundingMode.HALF_UP);
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaDiaria).pow(dias)
                );
                
            case AO_MES:
                
                BigDecimal taxaMensal = taxaDecimal.divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaMensal).pow(prazoMeses)
                );
                
            case AO_ANO:
                
                double anos = prazoMeses / 12.0;
                return valorInicial.multiply(
                    BigDecimal.ONE.add(taxaDecimal).pow((int)Math.ceil(anos))
                );
                
            case PERIODO_TOTAL:
                
                return valorInicial.multiply(BigDecimal.ONE.add(taxaDecimal));
                
            default:
                return valorInicial.multiply(BigDecimal.ONE.add(taxaDecimal));
        }
    }

    
    private boolean filtrarPorNome(Produto produto, String nome)
    {
        if (produto == null) return false;
        return nome == null || produto.getNome().equals(nome);
    }

    private boolean filtrarPorTipo(Produto produto, TipoProduto tipo) {
        if (produto == null) return false;
        return tipo == null || produto.getTipo().equals(tipo);
    }

    private boolean filtrarPorTipoRentabilidade(Produto produto, TipoRentabilidade tipo) {
        if (produto == null) return false;
        return tipo == null || produto.getTipoRentabilidade().equals(tipo);
    }

    private boolean filtrarPorIndice(Produto produto, Indice indice) {
        if (produto == null) return false;
        return indice == null || produto.getIndice().equals(indice);
    }

    private boolean filtrarPorLiquidez(Produto produto, Integer liquidez) {
        if (produto == null) return false;
        return liquidez == null || produto.getLiquidez().equals(liquidez);
    }

    private boolean filtrarPorFgc(Produto produto, Boolean fgc) {
        if (produto == null) return false;
        return fgc == null || produto.getFgc().equals(fgc);
    }

    private boolean filtrarPorPrazoMinimo(Produto produto, int prazoDias) {
        if (produto == null) return false;
        return produto.getMinimoDiasInvestimento() <= prazoDias;
    }

    
    private int compararProdutos(Produto p1, Produto p2) {
        
        int risco1 = p1.getRisco().ordinal();
        int risco2 = p2.getRisco().ordinal();
        
        if (risco1 != risco2) {
            return Integer.compare(risco1, risco2); 
        }
        
        
        return p2.getRentabilidade().compareTo(p1.getRentabilidade());
    }

    
    private SimulacaoInvestimento persistirSimulacao(SimulacaoRequest request, 
                                                   Produto produto, 
                                                   ResultadoSimulacao resultado) {
        
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                request.clienteId(),
                produto.getId(),
                produto.getNome(),
                request.valor(),
                resultado
        );
        
        
        simulacaoRepository.persist(simulacao);
        
        return simulacao;
    }

    
    public List<SimulacaoInvestimento> buscarSimulacoesPorCliente(Long clienteId) {
        return simulacaoRepository.findByClienteIdOrderByDate(clienteId);
    }

    
    public SimulacaoInvestimento buscarSimulacaoPorId(Long id) {
        return simulacaoRepository.findById(id);
    }

    
    public record EstatisticasCliente(
            Long totalSimulacoes,
            BigDecimal totalInvestido,
            BigDecimal mediaValorInvestido,
            SimulacaoInvestimento ultimaSimulacao
    ) {}

    public EstatisticasCliente getEstatisticasCliente(Long clienteId) {
        long totalSimulacoes = simulacaoRepository.countByClienteId(clienteId);
        BigDecimal totalInvestido = simulacaoRepository.getTotalInvestidoByClienteId(clienteId);
        BigDecimal mediaValorInvestido = totalSimulacoes > 0 ? 
                totalInvestido.divide(BigDecimal.valueOf(totalSimulacoes), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        SimulacaoInvestimento ultimaSimulacao = simulacaoRepository.findLastByClienteId(clienteId);

        return new EstatisticasCliente(totalSimulacoes, totalInvestido, mediaValorInvestido, ultimaSimulacao);
    }

    
    private void validarRegrasNegocio(SimulacaoRequest request) {
        
        if (request.liquidez() != null && request.liquidez() < -1) {
            throw new RuntimeException("Liquidez deve ser -1 (sem liquidos) ou o número de dias desejado.");
        }



        
        int prazoMeses = request.getPrazoEmMeses();
        if (prazoMeses > 240) { 
            throw new RuntimeException("Prazo muito longo para simulação precisa. Máximo recomendado: 20 anos (240 meses)");
        }

        
        if (request.tipoRentabilidade() != null && request.indice() != null) {
            if (request.tipoRentabilidade() == TipoRentabilidade.PRE &&
                    request.indice() != Indice.NENHUM) {
                throw new RuntimeException("Produtos pré-fixados não devem ter índice específico. Use 'NENHUM' como índice.");
            }

            if (request.tipoRentabilidade() == TipoRentabilidade.POS &&
                    request.indice() == Indice.NENHUM) {
                throw new RuntimeException("Produtos pós-fixados devem ter um índice específico (CDI, SELIC, IPCA, etc.)");
            }
        }
    }
}
