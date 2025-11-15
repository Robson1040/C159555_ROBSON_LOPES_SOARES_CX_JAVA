package br.gov.caixa.api.investimentos.resource.investimento;

import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import br.gov.caixa.api.investimentos.service.investimento.InvestimentoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoResourceTest {

    private InvestimentoService service;
    private InvestimentoResource resource;

    @BeforeEach
    void setUp() {
        service = mock(InvestimentoService.class);
        resource = new InvestimentoResource();
        resource.investimentoService = service; // Injeta mock manualmente
    }

    @Test
    void criar_deveRetornarInvestimentoCriado() {
        InvestimentoRequest request = new InvestimentoRequest(
                1L,                  // clienteId
                1L,                  // produtoId
                new BigDecimal("1000.00"), // valor
                12,                  // prazoMeses
                0,                   // prazoDias
                1,                   // prazoAnos
                LocalDate.now()      // data
        );

        InvestimentoResponse responseMock = new InvestimentoResponse(
                1L, 1L, 1L, new BigDecimal("1000.00"),
                12, 0, 1,
                LocalDate.now(),
                TipoProduto.POUPANCA,
                TipoRentabilidade.PRE,
                new BigDecimal("5.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.SELIC,
                0,
                30,
                true
        );

        when(service.criar(request)).thenReturn(responseMock);

        Response response = resource.criar(request);

        assertEquals(201, response.getStatus());
        assertEquals(responseMock, response.getEntity());
    }

    @Test
    void buscarPorCliente_deveRetornarListaDeInvestimentos() {
        Long clienteId = 1L;
        InvestimentoResponse investimento1 = new InvestimentoResponse(
                1L, clienteId, 1L, new BigDecimal("1000.00"), 12, 0, 1,
                LocalDate.now(), TipoProduto.POUPANCA, TipoRentabilidade.POS,
                new BigDecimal("5.0"), PeriodoRentabilidade.AO_ANO, Indice.SELIC, 0, 30, true
        );
        InvestimentoResponse investimento2 = new InvestimentoResponse(
                2L, clienteId, 2L, new BigDecimal("2000.00"), 24, 0, 2,
                LocalDate.now(), TipoProduto.CDB, TipoRentabilidade.POS,
                new BigDecimal("6.0"), PeriodoRentabilidade.AO_ANO, Indice.IPCA, 0, 30, false
        );

        when(service.buscarPorCliente(clienteId)).thenReturn(List.of(investimento1, investimento2));

        Response response = resource.buscarPorCliente(clienteId);

        assertEquals(200, response.getStatus());
        List<?> result = (List<?>) response.getEntity();
        assertEquals(2, result.size());
        assertTrue(result.contains(investimento1));
        assertTrue(result.contains(investimento2));
    }
}