package br.gov.caixa.api.investimentos.service.investimento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.exception.produto.ProdutoNotFoundException;
import br.gov.caixa.api.investimentos.mapper.InvestimentoMapper;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.model.produto.Produto;

import java.util.List;

@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoMapper investimentoMapper;

    @Transactional
    public InvestimentoResponse criar(InvestimentoRequest request)
    {
        if (request == null) {
            throw new IllegalArgumentException("Dados do investimento não podem ser nulos");
        }

        Produto produto = Produto.findById(request.produtoId());
        if (produto == null) {
            throw new ProdutoNotFoundException("Produto não encontrado com ID: " + request.produtoId());
        }

        Pessoa cliente = Pessoa.findById(request.clienteId());
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + request.clienteId());
        }

        // Validação de prazo mínimo vs produto
        int prazoDias = request.getPrazoEmDias();
        if (prazoDias > 0 && prazoDias < produto.getMinimoDiasInvestimento()) {
            throw new IllegalArgumentException("Prazo informado é menor que o mínimo do produto: " + produto.getMinimoDiasInvestimento() + " dias");
        }

        Investimento investimento = investimentoMapper.toEntity(request, produto);
        investimento.persist();

        return investimentoMapper.toResponse(investimento);
    }

    /**
     * Busca todos os investimentos de um cliente
     */
    public List<InvestimentoResponse> buscarPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        // Verifica se o cliente existe
        Pessoa cliente = Pessoa.findById(clienteId);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + clienteId);
        }

        List<Investimento> investimentos = Investimento.find("clienteId", clienteId).list();
        
        return investimentoMapper.toResponseList(investimentos);
    }
}


