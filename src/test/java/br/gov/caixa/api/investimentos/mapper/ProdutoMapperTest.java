package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.produto.ProdutoRequest;
import br.gov.caixa.api.investimentos.dto.produto.ProdutoResponse;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.PeriodoRentabilidade;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMapperTest {

    private ProdutoMapper produtoMapper;

    @BeforeEach
    void setUp() {
        produtoMapper = new ProdutoMapper();
    }

    @Test
    void toResponse_WithValidProduto_ShouldReturnProdutoResponse() {
        // Given
        Produto produto = new Produto(
                "CDB Teste",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("12.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                1,
                90,
                true
        );
        produto.setId(1L);

        // When
        ProdutoResponse response = produtoMapper.toResponse(produto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("CDB Teste", response.nome());
        assertEquals(TipoProduto.CDB, response.tipo());
        assertEquals(TipoRentabilidade.POS, response.tipoRentabilidade());
        assertEquals(new BigDecimal("12.5"), response.rentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, response.periodoRentabilidade());
        assertEquals(Indice.CDI, response.indice());
        assertEquals(1, response.liquidez());
        assertEquals(90, response.minimoDiasInvestimento());
        assertTrue(response.fgc());
        assertNotNull(response.risco());
    }

    @Test
    void toResponse_WithNullProduto_ShouldReturnNull() {
        // When
        ProdutoResponse response = produtoMapper.toResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    void toResponseList_WithValidListProdutos_ShouldReturnProdutoResponseList() {
        // Given
        Produto produto1 = new Produto(
                "CDB 1", 
                TipoProduto.CDB, 
                TipoRentabilidade.PRE, 
                new BigDecimal("10.0"), 
                PeriodoRentabilidade.AO_ANO, 
                null, // PRE-fixado não usa índice
                0, 
                90, 
                true
        );
        produto1.setId(1L);

        Produto produto2 = new Produto(
                "LCI 1", 
                TipoProduto.LCI, 
                TipoRentabilidade.POS, 
                new BigDecimal("11.0"), 
                PeriodoRentabilidade.AO_ANO, 
                Indice.CDI, 
                1, 
                180, 
                true
        );
        produto2.setId(2L);

        List<Produto> produtos = List.of(produto1, produto2);

        // When
        List<ProdutoResponse> responses = produtoMapper.toResponseList(produtos);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        ProdutoResponse response1 = responses.get(0);
        assertEquals(1L, response1.id());
        assertEquals("CDB 1", response1.nome());
        assertEquals(NivelRisco.BAIXO, response1.risco());

        ProdutoResponse response2 = responses.get(1);
        assertEquals(2L, response2.id());
        assertEquals("LCI 1", response2.nome());
        assertEquals(NivelRisco.MEDIO, response2.risco());
    }

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // When
        List<ProdutoResponse> responses = produtoMapper.toResponseList(null);

        // Then
        assertNull(responses);
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        List<Produto> produtos = new ArrayList<>();

        // When
        List<ProdutoResponse> responses = produtoMapper.toResponseList(produtos);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void toResponseList_WithListContainingNullElements_ShouldFilterNullElements() {
        // Given
        Produto produto1 = new Produto(
                "CDB 1", 
                TipoProduto.CDB, 
                TipoRentabilidade.PRE, 
                new BigDecimal("10.0"), 
                PeriodoRentabilidade.AO_ANO, 
                null, 
                0, 
                90, 
                true
        );
        produto1.setId(1L);

        List<Produto> produtos = new ArrayList<>();
        produtos.add(produto1);
        produtos.add(null);
        produtos.add(produto1);

        // When
        List<ProdutoResponse> responses = produtoMapper.toResponseList(produtos);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertAll(
            () -> assertEquals(1L, responses.get(0).id()),
            () -> assertEquals(1L, responses.get(1).id())
        );
    }

    @Test
    void toEntity_WithValidProdutoRequest_ShouldReturnProduto() {
        // Given
        ProdutoRequest request = new ProdutoRequest(
                "LCA Teste",
                TipoProduto.LCA,
                TipoRentabilidade.POS,
                new BigDecimal("13.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA,
                3,
                365,
                true
        );

        // When
        Produto produto = produtoMapper.toEntity(request);

        // Then
        assertNotNull(produto);
        assertNull(produto.getId()); // ID não deve ser definido no mapper
        assertEquals("LCA Teste", produto.getNome());
        assertEquals(TipoProduto.LCA, produto.getTipo());
        assertEquals(TipoRentabilidade.POS, produto.getTipoRentabilidade());
        assertEquals(new BigDecimal("13.0"), produto.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, produto.getPeriodoRentabilidade());
        assertEquals(Indice.IPCA, produto.getIndice());
        assertEquals(3, produto.getLiquidez());
        assertEquals(365, produto.getMinimoDiasInvestimento());
        assertTrue(produto.getFgc());
    }

    @Test
    void toEntity_WithNullProdutoRequest_ShouldReturnNull() {
        // When
        Produto produto = produtoMapper.toEntity(null);

        // Then
        assertNull(produto);
    }

    @Test
    void updateEntityFromRequest_WithValidParameters_ShouldUpdateProduto() {
        // Given
        Produto produto = new Produto(
                "Nome Antigo", 
                TipoProduto.CDB, 
                TipoRentabilidade.PRE, 
                new BigDecimal("5.0"), 
                PeriodoRentabilidade.AO_MES, 
                null, 
                0, 
                30, 
                false
        );
        produto.setId(1L);

        ProdutoRequest request = new ProdutoRequest(
                "Nome Novo",
                TipoProduto.LCI,
                TipoRentabilidade.POS,
                new BigDecimal("15.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                1,
                180,
                true
        );

        // When
        produtoMapper.updateEntityFromRequest(produto, request);

        // Then
        // ID deve permanecer inalterado
        assertEquals(1L, produto.getId());
        
        // Outros campos devem ser atualizados
        assertEquals("Nome Novo", produto.getNome());
        assertEquals(TipoProduto.LCI, produto.getTipo());
        assertEquals(TipoRentabilidade.POS, produto.getTipoRentabilidade());
        assertEquals(new BigDecimal("15.0"), produto.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, produto.getPeriodoRentabilidade());
        assertEquals(Indice.CDI, produto.getIndice());
        assertEquals(1, produto.getLiquidez());
        assertEquals(180, produto.getMinimoDiasInvestimento());
        assertTrue(produto.getFgc());
    }

    @Test
    void updateEntityFromRequest_WithNullProduto_ShouldDoNothing() {
        // Given
        ProdutoRequest request = new ProdutoRequest(
                "Teste",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("10.0"),
                PeriodoRentabilidade.AO_ANO,
                null,
                0,
                90,
                true
        );

        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> produtoMapper.updateEntityFromRequest(null, request));
    }

    @Test
    void updateEntityFromRequest_WithNullRequest_ShouldDoNothing() {
        // Given
        Produto produto = new Produto(
                "Nome Original", 
                TipoProduto.CDB, 
                TipoRentabilidade.PRE, 
                new BigDecimal("5.0"), 
                PeriodoRentabilidade.AO_MES, 
                null, 
                0, 
                30, 
                false
        );
        produto.setId(1L);

        String nomeOriginal = produto.getNome();
        TipoProduto tipoOriginal = produto.getTipo();
        
        // When
        produtoMapper.updateEntityFromRequest(produto, null);

        // Then (produto should remain unchanged)
        assertEquals(1L, produto.getId());
        assertEquals(nomeOriginal, produto.getNome());
        assertEquals(tipoOriginal, produto.getTipo());
    }

    @Test
    void updateEntityFromRequest_WithBothNull_ShouldDoNothing() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> produtoMapper.updateEntityFromRequest(null, null));
    }

    @Test
    void toEntity_WithProdutoRequestHavingNullValues_ShouldCreateProductWithNullValues() {
        // Given
        ProdutoRequest request = new ProdutoRequest(
                null, // nome null
                null, // tipo null
                TipoRentabilidade.POS,
                null, // rentabilidade null
                PeriodoRentabilidade.AO_ANO,
                null, // indice null
                1,
                null, // minimoDias null
                null  // fgc null
        );

        // When
        Produto produto = produtoMapper.toEntity(request);

        // Then
        assertNotNull(produto);
        assertNull(produto.getNome());
        assertNull(produto.getTipo());
        assertEquals(TipoRentabilidade.POS, produto.getTipoRentabilidade());
        assertNull(produto.getRentabilidade());
        assertEquals(PeriodoRentabilidade.AO_ANO, produto.getPeriodoRentabilidade());
        assertNull(produto.getIndice());
        assertEquals(1, produto.getLiquidez());
        assertNull(produto.getMinimoDiasInvestimento());
        assertNull(produto.getFgc());
    }

    @Test
    void toResponse_WithProdutoHavingNullValues_ShouldReturnResponseWithNullValues() {
        // Given
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setTipo(TipoProduto.POUPANCA);
        // Todos os outros campos ficam null

        // When
        ProdutoResponse response = produtoMapper.toResponse(produto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertNull(response.nome());
        assertNull(response.tipoRentabilidade());
        assertNull(response.rentabilidade());
        assertNull(response.periodoRentabilidade());
        assertNull(response.indice());
        assertNull(response.liquidez());
        assertNull(response.minimoDiasInvestimento());
        assertNull(response.fgc());
        assertNotNull(response.risco()); // getRisco() não retorna null, calcula baseado nos campos
    }

    @Test
    void updateEntityFromRequest_WithRequestHavingNullValues_ShouldUpdateWithNullValues() {
        // Given
        Produto produto = new Produto(
                "Nome", 
                TipoProduto.CDB, 
                TipoRentabilidade.PRE, 
                new BigDecimal("10.0"), 
                PeriodoRentabilidade.AO_ANO, 
                null, 
                0, 
                90, 
                true
        );
        produto.setId(1L);

        ProdutoRequest request = new ProdutoRequest(
                null, // nome null
                null, // tipo null
                null, // tipoRentabilidade null
                null, // rentabilidade null
                null, // periodoRentabilidade null
                null, // indice null
                null, // liquidez null
                null, // minimoDias null
                null  // fgc null
        );

        // When
        produtoMapper.updateEntityFromRequest(produto, request);

        // Then
        assertEquals(1L, produto.getId()); // ID deve permanecer
        
        // Campos atualizados devem ser null
        assertNull(produto.getNome());
        assertNull(produto.getTipo());
        assertNull(produto.getTipoRentabilidade());
        assertNull(produto.getRentabilidade());
        assertNull(produto.getPeriodoRentabilidade());
        assertNull(produto.getIndice());
        assertNull(produto.getLiquidez());
        assertNull(produto.getMinimoDiasInvestimento());
        assertNull(produto.getFgc());
    }

    @Test
    void toResponse_WithLowRiskProduct_ShouldReturnBaixoRisco() {
        // Given - Produto com baixo risco (FGC=true, rentabilidade baixa, liquidez imediata)
        Produto produto = new Produto(
                "CDB Conservador",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("5.0"), // baixa rentabilidade
                PeriodoRentabilidade.AO_ANO,
                null, // PRE-fixado não usa índice
                0, // liquidez imediata
                0, // sem carência
                true // com FGC
        );
        produto.setId(1L);

        // When
        ProdutoResponse response = produtoMapper.toResponse(produto);

        // Then
        assertEquals(NivelRisco.BAIXO, response.risco());
    }

    @Test
    void toResponse_WithHighRiskProduct_ShouldReturnAltoRisco() {
        // Given - Produto com alto risco (sem FGC, alta rentabilidade, sem liquidez)
        Produto produto = new Produto(
                "Debênture Arriscada",
                TipoProduto.DEBENTURE,
                TipoRentabilidade.POS,
                new BigDecimal("25.0"), // alta rentabilidade
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                -1, // sem liquidez
                720, // carência longa
                false // sem FGC
        );
        produto.setId(1L);

        // When
        ProdutoResponse response = produtoMapper.toResponse(produto);

        // Then
        assertEquals(NivelRisco.ALTO, response.risco());
    }
}