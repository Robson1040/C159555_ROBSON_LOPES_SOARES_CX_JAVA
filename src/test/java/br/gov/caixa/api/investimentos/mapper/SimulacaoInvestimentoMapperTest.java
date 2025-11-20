package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.simulacao.ResultadoSimulacao;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoInvestimentoResponse;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoInvestimentoMapperTest {

    private SimulacaoInvestimentoMapper simulacaoMapper;

    @BeforeEach
    void setUp() {
        simulacaoMapper = new SimulacaoInvestimentoMapper();
    }

    @Test
    void toResponse_WithValidSimulacao_ShouldReturnSimulacaoResponse() {
        // Given
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento(
                100L,
                200L,
                "CDB Premium",
                new BigDecimal("10000.00"),
                new BigDecimal("12500.00"),
                12,
                365,
                1
        );
        simulacao.setId(1L);
        simulacao.setDataSimulacao(LocalDateTime.of(2024, 2, 15, 14, 30));
        simulacao.setRentabilidadeEfetiva(new BigDecimal("25.00"));
        simulacao.setRendimento(new BigDecimal("2500.00"));
        simulacao.setValorSimulado(true);
        simulacao.setCenarioSimulacao("CONSERVADOR");

        // When
        SimulacaoInvestimentoResponse response = simulacaoMapper.toResponse(simulacao);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(200L, response.produtoId());
        assertEquals(100L, response.clienteId());
        assertEquals("CDB Premium", response.produto());
        assertEquals(new BigDecimal("10000.00"), response.valorInvestido());
        assertEquals(new BigDecimal("12500.00"), response.valorFinal());
        assertEquals(12, response.prazoMeses());
        assertEquals(365, response.prazoDias());
        assertEquals(1, response.prazoAnos());
        assertEquals(LocalDateTime.of(2024, 2, 15, 14, 30), response.dataSimulacao());
        assertEquals(new BigDecimal("25.00"), response.rentabilidadeEfetiva());
        assertEquals(new BigDecimal("2500.00"), response.rendimento());
        assertTrue(response.valorSimulado());
        assertEquals("CONSERVADOR", response.cenarioSimulacao());
    }

    @Test
    void toResponse_WithNullSimulacao_ShouldReturnNull() {
        // When
        SimulacaoInvestimentoResponse response = simulacaoMapper.toResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    void toResponseList_WithValidListSimulacoes_ShouldReturnResponseList() {
        // Given
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento(
                100L,
                200L,
                "CDB 1",
                new BigDecimal("5000.00"),
                new BigDecimal("6000.00"),
                6,
                180,
                0
        );
        simulacao1.setId(1L);

        SimulacaoInvestimento simulacao2 = new SimulacaoInvestimento(
                101L,
                201L,
                "LCI 1",
                new BigDecimal("15000.00"),
                new BigDecimal("18000.00"),
                18,
                540,
                1
        );
        simulacao2.setId(2L);

        List<SimulacaoInvestimento> simulacoes = List.of(simulacao1, simulacao2);

        // When
        List<SimulacaoInvestimentoResponse> responses = simulacaoMapper.toResponseList(simulacoes);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        SimulacaoInvestimentoResponse response1 = responses.get(0);
        assertEquals(1L, response1.id());
        assertEquals(100L, response1.clienteId());
        assertEquals("CDB 1", response1.produto());
        assertEquals(new BigDecimal("5000.00"), response1.valorInvestido());

        SimulacaoInvestimentoResponse response2 = responses.get(1);
        assertEquals(2L, response2.id());
        assertEquals(101L, response2.clienteId());
        assertEquals("LCI 1", response2.produto());
        assertEquals(new BigDecimal("15000.00"), response2.valorInvestido());
    }

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // When
        List<SimulacaoInvestimentoResponse> responses = simulacaoMapper.toResponseList(null);

        // Then
        assertNull(responses);
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        List<SimulacaoInvestimento> simulacoes = new ArrayList<>();

        // When
        List<SimulacaoInvestimentoResponse> responses = simulacaoMapper.toResponseList(simulacoes);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void toResponseList_WithListContainingNullElements_ShouldFilterNullElements() {
        // Given
        SimulacaoInvestimento simulacao1 = new SimulacaoInvestimento(
                100L,
                200L,
                "CDB Test",
                new BigDecimal("5000.00"),
                new BigDecimal("6000.00"),
                12,
                365,
                1
        );
        simulacao1.setId(1L);

        List<SimulacaoInvestimento> simulacoes = new ArrayList<>();
        simulacoes.add(simulacao1);
        simulacoes.add(null);
        simulacoes.add(simulacao1);

        // When
        List<SimulacaoInvestimentoResponse> responses = simulacaoMapper.toResponseList(simulacoes);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertAll(
                () -> assertEquals(1L, responses.get(0).id()),
                () -> assertEquals(1L, responses.get(1).id())
        );
    }

    @Test
    void toEntity_WithValidParametersAndResultado_ShouldReturnSimulacao() {
        // Given
        Long clienteId = 100L;
        Long produtoId = 200L;
        String nomeProduto = "CDB Excelente";
        BigDecimal valorInvestido = new BigDecimal("20000.00");

        ResultadoSimulacao resultado = new ResultadoSimulacao(
                new BigDecimal("25000.00"), // valorFinal
                new BigDecimal("25.00"), // rentabilidadeEfetiva
                18, // prazoMeses
                540, // prazoDias
                1, // prazoAnos
                new BigDecimal("20000.00"), // valorInvestido
                new BigDecimal("5000.00"), // rendimento
                true, // valorSimulado
                "MODERADO" // cenarioSimulacao
        );

        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(clienteId, produtoId, nomeProduto, valorInvestido, resultado);

        // Then
        assertNotNull(simulacao);
        assertNull(simulacao.getId()); // ID não deve ser definido no mapper
        assertEquals(100L, simulacao.getClienteId());
        assertEquals(200L, simulacao.getProdutoId());
        assertEquals("CDB Excelente", simulacao.getProduto());
        assertEquals(new BigDecimal("20000.00"), simulacao.getValorInvestido());
        assertEquals(new BigDecimal("25000.00"), simulacao.getValorFinal());
        assertEquals(18, simulacao.getPrazoMeses());
        assertEquals(540, simulacao.getPrazoDias());
        assertEquals(1, simulacao.getPrazoAnos());
        assertEquals(new BigDecimal("25.00"), simulacao.getRentabilidadeEfetiva());
        assertEquals(new BigDecimal("5000.00"), simulacao.getRendimento());
        assertTrue(simulacao.getValorSimulado());
        assertEquals("MODERADO", simulacao.getCenarioSimulacao());
    }

    @Test
    void toEntity_WithNullClienteId_ShouldReturnNull() {
        // Given
        ResultadoSimulacao resultado = new ResultadoSimulacao(
                new BigDecimal("10000.00"), // valorFinal
                new BigDecimal("10.0"), // rentabilidadeEfetiva
                12, // prazoMeses
                365, // prazoDias
                1, // prazoAnos
                new BigDecimal("5000.00"), // valorInvestido
                new BigDecimal("1000.00"), // rendimento
                true, // valorSimulado
                "TEST" // cenarioSimulacao
        );

        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(null, 200L, "Produto", new BigDecimal("5000.00"), resultado);

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithNullNomeProduto_ShouldReturnNull() {
        // Given
        ResultadoSimulacao resultado = new ResultadoSimulacao(
                new BigDecimal("10000.00"), // valorFinal
                new BigDecimal("10.0"), // rentabilidadeEfetiva
                12, // prazoMeses
                365, // prazoDias
                1, // prazoAnos
                new BigDecimal("5000.00"), // valorInvestido
                new BigDecimal("1000.00"), // rendimento
                true, // valorSimulado
                "TEST" // cenarioSimulacao
        );

        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(100L, 200L, null, new BigDecimal("5000.00"), resultado);

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithNullValorInvestido_ShouldReturnNull() {
        // Given
        ResultadoSimulacao resultado = new ResultadoSimulacao(
                new BigDecimal("10000.00"), // valorFinal
                new BigDecimal("10.0"), // rentabilidadeEfetiva
                12, // prazoMeses
                365, // prazoDias
                1, // prazoAnos
                new BigDecimal("5000.00"), // valorInvestido
                new BigDecimal("1000.00"), // rendimento
                true, // valorSimulado
                "TEST" // cenarioSimulacao
        );

        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(100L, 200L, "Produto", null, resultado);

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithNullResultado_ShouldReturnNull() {
        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(100L, 200L, "Produto", new BigDecimal("5000.00"), null);

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithBasicParameters_ShouldReturnSimulacao() {
        // Given
        Long clienteId = 100L;
        Long produtoId = 200L;
        String produto = "LCA Premium";
        BigDecimal valorInvestido = new BigDecimal("15000.00");
        BigDecimal valorFinal = new BigDecimal("18000.00");
        Integer prazoMeses = 24;
        Integer prazoDias = 730;
        Integer prazoAnos = 2;

        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                clienteId, produtoId, produto, valorInvestido,
                valorFinal, prazoMeses, prazoDias, prazoAnos
        );

        // Then
        assertNotNull(simulacao);
        assertNull(simulacao.getId()); // ID não deve ser definido no mapper
        assertEquals(100L, simulacao.getClienteId());
        assertEquals(200L, simulacao.getProdutoId());
        assertEquals("LCA Premium", simulacao.getProduto());
        assertEquals(new BigDecimal("15000.00"), simulacao.getValorInvestido());
        assertEquals(new BigDecimal("18000.00"), simulacao.getValorFinal());
        assertEquals(24, simulacao.getPrazoMeses());
        assertEquals(730, simulacao.getPrazoDias());
        assertEquals(2, simulacao.getPrazoAnos());

        // Campos não informados devem ser null
        assertNull(simulacao.getRentabilidadeEfetiva());
        assertNull(simulacao.getRendimento());
        assertNull(simulacao.getValorSimulado());
        assertNull(simulacao.getCenarioSimulacao());
    }

    @Test
    void toEntity_WithBasicParametersAndNullClienteId_ShouldReturnNull() {
        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                null, 200L, "Produto", new BigDecimal("5000.00"),
                new BigDecimal("6000.00"), 12, 365, 1
        );

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithBasicParametersAndNullProduto_ShouldReturnNull() {
        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                100L, 200L, null, new BigDecimal("5000.00"),
                new BigDecimal("6000.00"), 12, 365, 1
        );

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithBasicParametersAndNullValorInvestido_ShouldReturnNull() {
        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                100L, 200L, "Produto", null,
                new BigDecimal("6000.00"), 12, 365, 1
        );

        // Then
        assertNull(simulacao);
    }

    @Test
    void toEntity_WithBasicParametersAndNullValorFinal_ShouldReturnNull() {
        // When
        SimulacaoInvestimento simulacao = simulacaoMapper.toEntity(
                100L, 200L, "Produto", new BigDecimal("5000.00"),
                null, 12, 365, 1
        );

        // Then
        assertNull(simulacao);
    }

    @Test
    void updateEntityFromData_WithValidParameters_ShouldUpdateSimulacao() {
        // Given
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento(
                100L, 200L, "Produto Original", new BigDecimal("5000.00"),
                new BigDecimal("6000.00"), 6, 180, 0
        );
        simulacao.setId(1L);

        // When
        simulacaoMapper.updateEntityFromData(
                simulacao,
                999L, // novo clienteId
                "Produto Atualizado",
                new BigDecimal("25000.00"), // novo valorInvestido
                new BigDecimal("32000.00"), // novo valorFinal
                36, // novo prazoMeses
                1080, // novo prazoDias
                3, // novo prazoAnos
                new BigDecimal("28.00"), // rentabilidadeEfetiva
                new BigDecimal("7000.00"), // rendimento
                true, // valorSimulado
                "AGRESSIVO" // cenarioSimulacao
        );

        // Then
        // ID deve permanecer inalterado
        assertEquals(1L, simulacao.getId());

        // Outros campos devem ser atualizados
        assertEquals(999L, simulacao.getClienteId());
        assertEquals("Produto Atualizado", simulacao.getProduto());
        assertEquals(new BigDecimal("25000.00"), simulacao.getValorInvestido());
        assertEquals(new BigDecimal("32000.00"), simulacao.getValorFinal());
        assertEquals(36, simulacao.getPrazoMeses());
        assertEquals(1080, simulacao.getPrazoDias());
        assertEquals(3, simulacao.getPrazoAnos());
        assertEquals(new BigDecimal("28.00"), simulacao.getRentabilidadeEfetiva());
        assertEquals(new BigDecimal("7000.00"), simulacao.getRendimento());
        assertTrue(simulacao.getValorSimulado());
        assertEquals("AGRESSIVO", simulacao.getCenarioSimulacao());
    }

    @Test
    void updateEntityFromData_WithNullSimulacao_ShouldDoNothing() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> simulacaoMapper.updateEntityFromData(
                null, 100L, "Produto", new BigDecimal("5000.00"),
                new BigDecimal("6000.00"), 12, 365, 1,
                new BigDecimal("20.0"), new BigDecimal("1000.00"), true, "TEST"
        ));
    }

    @Test
    void updateEntityFromData_WithNullValues_ShouldUpdateWithNullValues() {
        // Given
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento(
                100L, 200L, "Produto Original", new BigDecimal("5000.00"),
                new BigDecimal("6000.00"), 6, 180, 0
        );
        simulacao.setId(1L);
        simulacao.setRentabilidadeEfetiva(new BigDecimal("10.0"));

        // When
        simulacaoMapper.updateEntityFromData(
                simulacao,
                null, // clienteId null
                null, // produto null
                null, // valorInvestido null
                null, // valorFinal null
                null, // prazoMeses null
                null, // prazoDias null
                null, // prazoAnos null
                null, // rentabilidadeEfetiva null
                null, // rendimento null
                null, // valorSimulado null
                null  // cenarioSimulacao null
        );

        // Then
        assertEquals(1L, simulacao.getId()); // ID deve permanecer

        // Campos atualizados devem ser null
        assertNull(simulacao.getClienteId());
        assertNull(simulacao.getProduto());
        assertNull(simulacao.getValorInvestido());
        assertNull(simulacao.getValorFinal());
        assertNull(simulacao.getPrazoMeses());
        assertNull(simulacao.getPrazoDias());
        assertNull(simulacao.getPrazoAnos());
        assertNull(simulacao.getRentabilidadeEfetiva());
        assertNull(simulacao.getRendimento());
        assertNull(simulacao.getValorSimulado());
        assertNull(simulacao.getCenarioSimulacao());
    }

    @Test
    void toResponse_WithSimulacaoHavingNullValues_ShouldReturnResponseWithNullValues() {
        // Given
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setId(1L);
        simulacao.setDataSimulacao(null); // Explicitamente definir como null para testar
        // Todos os outros campos ficam null

        // When
        SimulacaoInvestimentoResponse response = simulacaoMapper.toResponse(simulacao);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertNull(response.produtoId());
        assertNull(response.clienteId());
        assertNull(response.produto());
        assertNull(response.valorInvestido());
        assertNull(response.valorFinal());
        assertNull(response.prazoMeses());
        assertNull(response.prazoDias());
        assertNull(response.prazoAnos());
        assertNull(response.dataSimulacao());
        assertNull(response.rentabilidadeEfetiva());
        assertNull(response.rendimento());
        assertNull(response.valorSimulado());
        assertNull(response.cenarioSimulacao());
    }
}