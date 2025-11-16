package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoResponse;
import br.gov.caixa.api.investimentos.model.investimento.Investimento;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvestimentoMapperTest {

    private InvestimentoMapper investimentoMapper;

    @BeforeEach
    void setUp() {
        investimentoMapper = new InvestimentoMapper();
    }

    @Test
    void toResponse_WithValidInvestimento_ShouldReturnInvestimentoResponse() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        investimento.setClienteId(100L);
        investimento.setProdutoId(200L);
        investimento.setValor(new BigDecimal("10000.00"));
        investimento.setPrazoMeses(12);
        investimento.setPrazoDias(365);
        investimento.setPrazoAnos(1);
        investimento.setData(LocalDate.of(2024, 1, 15));
        investimento.setTipo(TipoProduto.CDB);
        investimento.setTipoRentabilidade(TipoRentabilidade.POS);
        investimento.setRentabilidade(new BigDecimal("12.5"));
        investimento.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        investimento.setIndice(Indice.CDI);
        investimento.setLiquidez(1);
        investimento.setMinimoDiasInvestimento(90);
        investimento.setFgc(true);

        // When
        InvestimentoResponse response = investimentoMapper.toResponse(investimento);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(100L, response.clienteId());
        assertEquals(200L, response.produtoId());
        assertEquals(new BigDecimal("10000.00"), response.valor());
        assertEquals(12, response.prazoMeses());
        assertEquals(365, response.prazoDias());
        assertEquals(1, response.prazoAnos());
        assertEquals(LocalDate.of(2024, 1, 15), response.data());
        assertEquals(TipoProduto.CDB, response.tipo());
        assertEquals(TipoRentabilidade.POS, response.tipoRentabilidade());
        assertEquals(new BigDecimal("12.5"), response.rentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, response.periodoRentabilidade());
        assertEquals(Indice.CDI, response.indice());
        assertEquals(1, response.liquidez());
        assertEquals(90, response.minimoDiasInvestimento());
        assertTrue(response.fgc());
    }

    @Test
    void toResponse_WithNullInvestimento_ShouldReturnNull() {
        // When
        InvestimentoResponse response = investimentoMapper.toResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    void toResponseList_WithValidListInvestimentos_ShouldReturnInvestimentoResponseList() {
        // Given
        Investimento investimento1 = new Investimento();
        investimento1.setId(1L);
        investimento1.setClienteId(100L);
        investimento1.setProdutoId(200L);
        investimento1.setValor(new BigDecimal("5000.00"));
        investimento1.setData(LocalDate.now());

        Investimento investimento2 = new Investimento();
        investimento2.setId(2L);
        investimento2.setClienteId(101L);
        investimento2.setProdutoId(201L);
        investimento2.setValor(new BigDecimal("15000.00"));
        investimento2.setData(LocalDate.now().minusDays(5));

        List<Investimento> investimentos = List.of(investimento1, investimento2);

        // When
        List<InvestimentoResponse> responses = investimentoMapper.toResponseList(investimentos);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        InvestimentoResponse response1 = responses.get(0);
        assertEquals(1L, response1.id());
        assertEquals(100L, response1.clienteId());
        assertEquals(new BigDecimal("5000.00"), response1.valor());

        InvestimentoResponse response2 = responses.get(1);
        assertEquals(2L, response2.id());
        assertEquals(101L, response2.clienteId());
        assertEquals(new BigDecimal("15000.00"), response2.valor());
    }

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // When
        List<InvestimentoResponse> responses = investimentoMapper.toResponseList(null);

        // Then
        assertNull(responses);
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        List<Investimento> investimentos = new ArrayList<>();

        // When
        List<InvestimentoResponse> responses = investimentoMapper.toResponseList(investimentos);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void toResponseList_WithListContainingNullElements_ShouldFilterNullElements() {
        // Given
        Investimento investimento1 = new Investimento();
        investimento1.setId(1L);
        investimento1.setClienteId(100L);

        List<Investimento> investimentos = new ArrayList<>();
        investimentos.add(investimento1);
        investimentos.add(null);
        investimentos.add(investimento1);

        // When
        List<InvestimentoResponse> responses = investimentoMapper.toResponseList(investimentos);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertAll(
            () -> assertEquals(1L, responses.get(0).id()),
            () -> assertEquals(1L, responses.get(1).id())
        );
    }

    @Test
    void toEntity_WithValidRequestAndProduto_ShouldReturnInvestimento() {
        // Given
        InvestimentoRequest request = new InvestimentoRequest(
                100L,
                200L,
                new BigDecimal("8000.00"),
                18,
                540,
                1,
                LocalDate.of(2024, 2, 20)
        );

        Produto produto = new Produto(
                "CDB Premium",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("13.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                1,
                180,
                true
        );

        // When
        Investimento investimento = investimentoMapper.toEntity(request, produto);

        // Then
        assertNotNull(investimento);
        assertNull(investimento.getId()); // ID não deve ser definido no mapper
        assertEquals(100L, investimento.getClienteId());
        assertEquals(200L, investimento.getProdutoId());
        assertEquals(new BigDecimal("8000.00"), investimento.getValor());
        assertEquals(18, investimento.getPrazoMeses());
        assertEquals(540, investimento.getPrazoDias());
        assertEquals(1, investimento.getPrazoAnos());
        assertEquals(LocalDate.of(2024, 2, 20), investimento.getData());
        
        // Dados do produto copiados
        assertEquals(TipoProduto.CDB, investimento.getTipo());
        assertEquals(TipoRentabilidade.POS, investimento.getTipoRentabilidade());
        assertEquals(new BigDecimal("13.0"), investimento.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, investimento.getPeriodoRentabilidade());
        assertEquals(Indice.CDI, investimento.getIndice());
        assertEquals(1, investimento.getLiquidez());
        assertEquals(180, investimento.getMinimoDiasInvestimento());
        assertTrue(investimento.getFgc());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        // Given
        Produto produto = new Produto();

        // When
        Investimento investimento = investimentoMapper.toEntity(null, produto);

        // Then
        assertNull(investimento);
    }

    @Test
    void toEntity_WithNullProduto_ShouldReturnNull() {
        // Given
        InvestimentoRequest request = new InvestimentoRequest(
                100L,
                200L,
                new BigDecimal("5000.00"),
                12,
                365,
                1,
                LocalDate.now()
        );

        // When
        Investimento investimento = investimentoMapper.toEntity(request, null);

        // Then
        assertNull(investimento);
    }

    @Test
    void toEntity_WithBothNull_ShouldReturnNull() {
        // When
        Investimento investimento = investimentoMapper.toEntity(null, null);

        // Then
        assertNull(investimento);
    }

    @Test
    void updateEntityFromRequest_WithValidParameters_ShouldUpdateInvestimento() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        investimento.setClienteId(100L);
        investimento.setProdutoId(200L);
        investimento.setValor(new BigDecimal("5000.00"));
        investimento.setData(LocalDate.of(2024, 1, 1));

        InvestimentoRequest request = new InvestimentoRequest(
                999L,
                888L,
                new BigDecimal("25000.00"),
                24,
                730,
                2,
                LocalDate.of(2024, 3, 15)
        );

        Produto novoProduto = new Produto(
                "LCI Gold",
                TipoProduto.LCI,
                TipoRentabilidade.POS,
                new BigDecimal("11.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA,
                30,
                365,
                true
        );

        // When
        investimentoMapper.updateEntityFromRequest(investimento, request, novoProduto);

        // Then
        // ID deve permanecer inalterado
        assertEquals(1L, investimento.getId());
        
        // Dados do request atualizados
        assertEquals(999L, investimento.getClienteId());
        assertEquals(888L, investimento.getProdutoId());
        assertEquals(new BigDecimal("25000.00"), investimento.getValor());
        assertEquals(24, investimento.getPrazoMeses());
        assertEquals(730, investimento.getPrazoDias());
        assertEquals(2, investimento.getPrazoAnos());
        assertEquals(LocalDate.of(2024, 3, 15), investimento.getData());
        
        // Dados do produto atualizados
        assertEquals(TipoProduto.LCI, investimento.getTipo());
        assertEquals(TipoRentabilidade.POS, investimento.getTipoRentabilidade());
        assertEquals(new BigDecimal("11.5"), investimento.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, investimento.getPeriodoRentabilidade());
        assertEquals(Indice.IPCA, investimento.getIndice());
        assertEquals(30, investimento.getLiquidez());
        assertEquals(365, investimento.getMinimoDiasInvestimento());
        assertTrue(investimento.getFgc());
    }

    @Test
    void updateEntityFromRequest_WithNullInvestimento_ShouldDoNothing() {
        // Given
        InvestimentoRequest request = new InvestimentoRequest(
                100L,
                200L,
                new BigDecimal("5000.00"),
                12,
                365,
                1,
                LocalDate.now()
        );
        Produto produto = new Produto();

        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> investimentoMapper.updateEntityFromRequest(null, request, produto));
    }

    @Test
    void updateEntityFromRequest_WithNullRequest_ShouldDoNothing() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        investimento.setClienteId(100L);
        investimento.setValor(new BigDecimal("5000.00"));
        
        Long clienteIdOriginal = investimento.getClienteId();
        BigDecimal valorOriginal = investimento.getValor();
        
        Produto produto = new Produto();

        // When
        investimentoMapper.updateEntityFromRequest(investimento, null, produto);

        // Then (investimento should remain unchanged)
        assertEquals(1L, investimento.getId());
        assertEquals(clienteIdOriginal, investimento.getClienteId());
        assertEquals(valorOriginal, investimento.getValor());
    }

    @Test
    void updateEntityFromRequest_WithNullProduto_ShouldDoNothing() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        investimento.setClienteId(100L);

        InvestimentoRequest request = new InvestimentoRequest(
                999L,
                200L,
                new BigDecimal("10000.00"),
                12,
                365,
                1,
                LocalDate.now()
        );
        
        Long clienteIdOriginal = investimento.getClienteId();

        // When
        investimentoMapper.updateEntityFromRequest(investimento, request, null);

        // Then (investimento should remain unchanged)
        assertEquals(1L, investimento.getId());
        assertEquals(clienteIdOriginal, investimento.getClienteId());
    }

    @Test
    void updateEntityFromRequest_WithAllNull_ShouldDoNothing() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> investimentoMapper.updateEntityFromRequest(null, null, null));
    }

    @Test
    void toEntity_WithRequestHavingNullValues_ShouldCreateInvestimentoWithNullValues() {
        // Given
        InvestimentoRequest request = new InvestimentoRequest(
                null, // clienteId null
                null, // produtoId null
                null, // valor null
                null, // prazoMeses null
                null, // prazoDias null
                null, // prazoAnos null
                null  // data null
        );

        Produto produto = new Produto(
                "Produto Teste",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                90,
                true
        );

        // When
        Investimento investimento = investimentoMapper.toEntity(request, produto);

        // Then
        assertNotNull(investimento);
        assertNull(investimento.getClienteId());
        assertNull(investimento.getProdutoId());
        assertNull(investimento.getValor());
        assertNull(investimento.getPrazoMeses());
        assertNull(investimento.getPrazoDias());
        assertNull(investimento.getPrazoAnos());
        assertNull(investimento.getData());
        
        // Dados do produto devem ser copiados mesmo com request null
        assertEquals(TipoProduto.CDB, investimento.getTipo());
        assertEquals(TipoRentabilidade.PRE, investimento.getTipoRentabilidade());
        assertEquals(new BigDecimal("10.0"), investimento.getRentabilidade());
    }

    @Test
    void toResponse_WithInvestimentoHavingNullValues_ShouldReturnResponseWithNullValues() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        // Todos os outros campos ficam null

        // When
        InvestimentoResponse response = investimentoMapper.toResponse(investimento);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertNull(response.clienteId());
        assertNull(response.produtoId());
        assertNull(response.valor());
        assertNull(response.prazoMeses());
        assertNull(response.prazoDias());
        assertNull(response.prazoAnos());
        assertNull(response.data());
        assertNull(response.tipo());
        assertNull(response.tipoRentabilidade());
        assertNull(response.rentabilidade());
        assertNull(response.periodoRentabilidade());
        assertNull(response.indice());
        assertNull(response.liquidez());
        assertNull(response.minimoDiasInvestimento());
        assertNull(response.fgc());
    }

    @Test
    void updateEntityFromRequest_WithRequestAndProdutoHavingNullValues_ShouldUpdateWithNullValues() {
        // Given
        Investimento investimento = new Investimento();
        investimento.setId(1L);
        investimento.setClienteId(100L);
        investimento.setTipo(TipoProduto.CDB);

        InvestimentoRequest request = new InvestimentoRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Produto produto = new Produto();
        // Produto com todos os campos null

        // When
        investimentoMapper.updateEntityFromRequest(investimento, request, produto);

        // Then
        assertEquals(1L, investimento.getId()); // ID deve permanecer
        
        // Campos atualizados devem ser null
        assertNull(investimento.getClienteId());
        assertNull(investimento.getProdutoId());
        assertNull(investimento.getValor());
        assertNull(investimento.getPrazoMeses());
        assertNull(investimento.getPrazoDias());
        assertNull(investimento.getPrazoAnos());
        assertNull(investimento.getData());
        
        // Dados do produto null
        assertNull(investimento.getTipo());
        assertNull(investimento.getTipoRentabilidade());
        assertNull(investimento.getRentabilidade());
        assertNull(investimento.getPeriodoRentabilidade());
        assertNull(investimento.getIndice());
        assertNull(investimento.getLiquidez());
        assertNull(investimento.getMinimoDiasInvestimento());
        assertNull(investimento.getFgc());
    }
}