package br.gov.caixa.api.investimentos.service.compliance;

import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LimitesRegulatóriosService - Testes unitários para validação de limites FGC")
class LimitesRegulatóriosServiceTest {

    private LimitesRegulatóriosService limitesService;

    @Mock
    private IInvestimentoRepository investimentoRepository;

    private static final String CPF_TESTE = "12345678901";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        limitesService = new LimitesRegulatóriosService();
        Mockito.reset(investimentoRepository);
    }

    @Test
    @DisplayName("Deve validar limite FGC para CDB - dentro do limite")
    void deveValidarLimiteFGCParaCDBDentroDoLimite() {
        // Given
        TipoProduto tipoProduto = TipoProduto.CDB;
        BigDecimal valorNovo = new BigDecimal("100000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar limite FGC para LCI - dentro do limite")
    void deveValidarLimiteFGCParaLCIDentroDoLimite() {
        // Given
        TipoProduto tipoProduto = TipoProduto.LCI;
        BigDecimal valorNovo = new BigDecimal("200000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar limite FGC para LCA - dentro do limite")
    void deveValidarLimiteFGCParaLCADentroDoLimite() {
        // Given
        TipoProduto tipoProduto = TipoProduto.LCA;
        BigDecimal valorNovo = new BigDecimal("250000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar limite FGC para Poupança - dentro do limite")
    void deveValidarLimiteFGCParaPoupancaDentroDoLimite() {
        // Given
        TipoProduto tipoProduto = TipoProduto.POUPANCA;
        BigDecimal valorNovo = new BigDecimal("150000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - Tesouro Direto")
    void devePermitirInvestimentoEmProdutosSemFGCTesouroDireto() {
        // Given
        TipoProduto tipoProduto = TipoProduto.TESOURO_DIRETO;
        BigDecimal valorNovo = new BigDecimal("1000000.00"); // Valor alto

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - Fundo")
    void devePermitirInvestimentoEmProdutosSemFGCFundo() {
        // Given
        TipoProduto tipoProduto = TipoProduto.FUNDO;
        BigDecimal valorNovo = new BigDecimal("2000000.00"); // Valor alto

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - FII")
    void devePermitirInvestimentoEmProdutosSemFGCFII() {
        // Given
        TipoProduto tipoProduto = TipoProduto.FII;
        BigDecimal valorNovo = new BigDecimal("500000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - Ação")
    void devePermitirInvestimentoEmProdutosSemFGCAcao() {
        // Given
        TipoProduto tipoProduto = TipoProduto.ACAO;
        BigDecimal valorNovo = new BigDecimal("800000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - ETF")
    void devePermitirInvestimentoEmProdutosSemFGCETF() {
        // Given
        TipoProduto tipoProduto = TipoProduto.ETF;
        BigDecimal valorNovo = new BigDecimal("300000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - Debênture")
    void devePermitirInvestimentoEmProdutosSemFGCDebenture() {
        // Given
        TipoProduto tipoProduto = TipoProduto.DEBENTURE;
        BigDecimal valorNovo = new BigDecimal("400000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve permitir investimento em produtos sem FGC - CRI")
    void devePermitirInvestimentoEmProdutosSemFGCCRI() {
        // Given
        TipoProduto tipoProduto = TipoProduto.CRI;
        BigDecimal valorNovo = new BigDecimal("600000.00");

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorNovo);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve calcular valor disponível FGC corretamente para produtos com proteção")
    void deveCalcularValorDisponivelFGCCorretamente() {
        // Given
        TipoProduto tipoProduto = TipoProduto.CDB;
        BigDecimal limiteEsperado = new BigDecimal("250000.00");

        // When
        BigDecimal valorDisponivel = limitesService.calcularValorDisponivelFGC(CPF_TESTE, tipoProduto);

        // Then
        assertEquals(0, limiteEsperado.compareTo(valorDisponivel));
    }

    @Test
    @DisplayName("Deve calcular valor disponível FGC para produtos sem proteção")
    void deveCalcularValorDisponivelFGCParaProdutosSemProtecao() {
        // Given
        TipoProduto tipoProduto = TipoProduto.TESOURO_DIRETO;
        BigDecimal valorAlto = new BigDecimal("999999999.99");

        // When
        BigDecimal valorDisponivel = limitesService.calcularValorDisponivelFGC(CPF_TESTE, tipoProduto);

        // Then
        assertEquals(0, valorAlto.compareTo(valorDisponivel));
    }

    @Test
    @DisplayName("Deve retornar valor disponível para LCI/LCA")
    void deveRetornarValorDisponivelParaLCILCA() {
        // Given
        TipoProduto tipoLCI = TipoProduto.LCI;
        TipoProduto tipoLCA = TipoProduto.LCA;
        BigDecimal limiteEsperado = new BigDecimal("250000.00");

        // When
        BigDecimal valorDisponivelLCI = limitesService.calcularValorDisponivelFGC(CPF_TESTE, tipoLCI);
        BigDecimal valorDisponivelLCA = limitesService.calcularValorDisponivelFGC(CPF_TESTE, tipoLCA);

        // Then
        assertEquals(0, limiteEsperado.compareTo(valorDisponivelLCI));
        assertEquals(0, limiteEsperado.compareTo(valorDisponivelLCA));
    }

    @Test
    @DisplayName("Deve retornar valor disponível para Poupança")
    void deveRetornarValorDisponivelParaPoupanca() {
        // Given
        TipoProduto tipoProduto = TipoProduto.POUPANCA;
        BigDecimal limiteEsperado = new BigDecimal("250000.00");

        // When
        BigDecimal valorDisponivel = limitesService.calcularValorDisponivelFGC(CPF_TESTE, tipoProduto);

        // Then
        assertEquals(0, limiteEsperado.compareTo(valorDisponivel));
    }

    @Test
    @DisplayName("Deve validar investimento com valor zero")
    void deveValidarInvestimentoComValorZero() {
        // Given
        TipoProduto tipoProduto = TipoProduto.CDB;
        BigDecimal valorZero = BigDecimal.ZERO;

        // When
        boolean resultado = limitesService.validarLimiteFGC(CPF_TESTE, tipoProduto, valorZero);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve calcular valor disponível corretamente quando não há limite negativo")
    void deveCalcularValorDisponivelSemLimiteNegativo() {
        // Given - todos os produtos com FGC devem ter valor disponível >= 0
        TipoProduto[] produtosComFGC = {TipoProduto.CDB, TipoProduto.LCI, TipoProduto.LCA, TipoProduto.POUPANCA};

        for (TipoProduto produto : produtosComFGC) {
            // When
            BigDecimal valorDisponivel = limitesService.calcularValorDisponivelFGC(CPF_TESTE, produto);

            // Then
            assertTrue(valorDisponivel.compareTo(BigDecimal.ZERO) >= 0,
                    "Valor disponível deve ser >= 0 para " + produto);
        }
    }
}