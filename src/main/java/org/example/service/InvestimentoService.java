package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.example.dto.InvestimentoRequest;
import org.example.dto.InvestimentoResponse;
import org.example.exception.ClienteNotFoundException;
import org.example.exception.ProdutoNotFoundException;
import org.example.model.Investimento;
import org.example.model.Pessoa;
import org.example.model.Produto;

@ApplicationScoped
public class InvestimentoService {

    @Transactional
    public InvestimentoResponse criar(InvestimentoRequest request) {
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

        Investimento investimento = Investimento.from(request, produto);
        investimento.persist();

        return new InvestimentoResponse(
                investimento.id,
                investimento.clienteId,
                investimento.produtoId,
                investimento.valor,
                investimento.prazoMeses,
                investimento.prazoDias,
                investimento.prazoAnos,
                investimento.data,
                investimento.tipo,
                investimento.tipoRentabilidade,
                investimento.rentabilidade,
                investimento.periodoRentabilidade,
                investimento.indice,
                investimento.liquidez,
                investimento.minimoDiasInvestimento,
                investimento.fgc
        );
    }
}
