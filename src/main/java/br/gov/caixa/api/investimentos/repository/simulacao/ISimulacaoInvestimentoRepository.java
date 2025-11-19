package br.gov.caixa.api.investimentos.repository.simulacao;

import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface ISimulacaoInvestimentoRepository extends PanacheRepository<SimulacaoInvestimento> {

    
    List<SimulacaoInvestimento> findByClienteId(Long clienteId);

    
    List<SimulacaoInvestimento> findByClienteIdOrderByDate(Long clienteId);

    
    List<SimulacaoInvestimento> findByProduto(String produto);

    
    List<SimulacaoInvestimento> findByValorInvestidoRange(BigDecimal minValue, BigDecimal maxValue);

    
    List<SimulacaoInvestimento> findByDataRange(LocalDateTime inicio, LocalDateTime fim);

    
    List<SimulacaoInvestimento> findSimulacoesComValoresSimulados();

    
    long countByClienteId(Long clienteId);

    
    SimulacaoInvestimento findLastByClienteId(Long clienteId);

    
    BigDecimal getTotalInvestidoByClienteId(Long clienteId);
}