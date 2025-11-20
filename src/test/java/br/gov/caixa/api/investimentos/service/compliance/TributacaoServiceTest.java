package br.gov.caixa.api.investimentos.service.compliance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TributacaoService - Testes unitários para cálculos de tributação")
class TributacaoServiceTest {

    private TributacaoService tributacaoService;

    @BeforeEach
    void setUp() {
        tributacaoService = new TributacaoService();
    }

    @Test
    @DisplayName("Deve calcular IR corretamente para prazo até 180 dias (22,5%)")
    void deveCalcularIRParaPrazoAte180Dias() {
        // Given
        BigDecimal rendimento = new BigDecimal("1000.00");
        int prazoDias = 180;
        BigDecimal irEsperado = new BigDecimal("225.00"); // 22,5%

        // When
        BigDecimal irCalculado = tributacaoService.calcularIR(rendimento, prazoDias);

        // Then
        assertEquals(0, irEsperado.compareTo(irCalculado));
    }

    @Test
    @DisplayName("Deve calcular IR corretamente para prazo entre 181 e 360 dias (20%)")
    void deveCalcularIRParaPrazoEntre181E360Dias() {
        // Given
        BigDecimal rendimento = new BigDecimal("1000.00");
        int prazoDias = 360;
        BigDecimal irEsperado = new BigDecimal("200.00"); // 20%

        // When
        BigDecimal irCalculado = tributacaoService.calcularIR(rendimento, prazoDias);

        // Then
        assertEquals(0, irEsperado.compareTo(irCalculado));
    }

    @Test
    @DisplayName("Deve calcular IR corretamente para prazo entre 361 e 720 dias (17,5%)")
    void deveCalcularIRParaPrazoEntre361E720Dias() {
        // Given
        BigDecimal rendimento = new BigDecimal("1000.00");
        int prazoDias = 720;
        BigDecimal irEsperado = new BigDecimal("175.00"); // 17,5%

        // When
        BigDecimal irCalculado = tributacaoService.calcularIR(rendimento, prazoDias);

        // Then
        assertEquals(0, irEsperado.compareTo(irCalculado));
    }

    @Test
    @DisplayName("Deve calcular IR corretamente para prazo acima de 720 dias (15%)")
    void deveCalcularIRParaPrazoAcima720Dias() {
        // Given
        BigDecimal rendimento = new BigDecimal("1000.00");
        int prazoDias = 721;
        BigDecimal irEsperado = new BigDecimal("150.00"); // 15%

        // When
        BigDecimal irCalculado = tributacaoService.calcularIR(rendimento, prazoDias);

        // Then
        assertEquals(0, irEsperado.compareTo(irCalculado));
    }

    @Test
    @DisplayName("Deve retornar zero para rendimento negativo ou zero")
    void deveRetornarZeroParaRendimentoNegativoOuZero() {
        // Given
        BigDecimal rendimentoZero = BigDecimal.ZERO;
        BigDecimal rendimentoNegativo = new BigDecimal("-100.00");
        int prazoDias = 100;

        // When
        BigDecimal irZero = tributacaoService.calcularIR(rendimentoZero, prazoDias);
        BigDecimal irNegativo = tributacaoService.calcularIR(rendimentoNegativo, prazoDias);

        // Then
        assertEquals(BigDecimal.ZERO, irZero);
        assertEquals(BigDecimal.ZERO, irNegativo);
    }

    @Test
    @DisplayName("Deve calcular IOF corretamente para 1 dia (96%)")
    void deveCalcularIOFPara1Dia() {
        // Given
        BigDecimal rendimento = new BigDecimal("100.00");
        int prazoDias = 1;
        BigDecimal iofEsperado = new BigDecimal("96.00"); // 96%

        // When
        BigDecimal iofCalculado = tributacaoService.calcularIOF(rendimento, prazoDias);

        // Then
        assertEquals(0, iofEsperado.compareTo(iofCalculado));
    }

    @Test
    @DisplayName("Deve calcular IOF corretamente para 15 dias (aproximadamente 49.38%)")
    void deveCalcularIOFPara15Dias() {
        // Given
        BigDecimal rendimento = new BigDecimal("100.00");
        int prazoDias = 15;

        // When
        BigDecimal iofCalculado = tributacaoService.calcularIOF(rendimento, prazoDias);

        // Then
        assertTrue(iofCalculado.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(iofCalculado.compareTo(new BigDecimal("60.00")) < 0); // Deve ser menor que 60%
        assertTrue(iofCalculado.compareTo(new BigDecimal("40.00")) > 0); // Deve ser maior que 40%
    }

    @Test
    @DisplayName("Deve retornar zero IOF para prazo igual ou superior a 30 dias")
    void deveRetornarZeroIOFParaPrazo30DiasOuMais() {
        // Given
        BigDecimal rendimento = new BigDecimal("1000.00");
        int prazo30Dias = 30;
        int prazo31Dias = 31;

        // When
        BigDecimal iof30Dias = tributacaoService.calcularIOF(rendimento, prazo30Dias);
        BigDecimal iof31Dias = tributacaoService.calcularIOF(rendimento, prazo31Dias);

        // Then
        assertEquals(BigDecimal.ZERO, iof30Dias);
        assertEquals(BigDecimal.ZERO, iof31Dias);
    }

    @Test
    @DisplayName("Deve retornar zero IOF para rendimento negativo ou zero")
    void deveRetornarZeroIOFParaRendimentoNegativoOuZero() {
        // Given
        BigDecimal rendimentoZero = BigDecimal.ZERO;
        BigDecimal rendimentoNegativo = new BigDecimal("-100.00");
        int prazoDias = 10;

        // When
        BigDecimal iofZero = tributacaoService.calcularIOF(rendimentoZero, prazoDias);
        BigDecimal iofNegativo = tributacaoService.calcularIOF(rendimentoNegativo, prazoDias);

        // Then
        assertEquals(BigDecimal.ZERO, iofZero);
        assertEquals(BigDecimal.ZERO, iofNegativo);
    }

    @Test
    @DisplayName("Deve calcular rendimento líquido corretamente")
    void deveCalcularRendimentoLiquidoCorretamente() {
        // Given
        BigDecimal rendimentoBruto = new BigDecimal("1000.00");
        int prazoDias = 10; // Período com IOF e IR

        // When
        BigDecimal rendimentoLiquido = tributacaoService.calcularRendimentoLiquido(rendimentoBruto, prazoDias);

        // Cálculo manual para verificação
        BigDecimal irEsperado = tributacaoService.calcularIR(rendimentoBruto, prazoDias);
        BigDecimal iofEsperado = tributacaoService.calcularIOF(rendimentoBruto, prazoDias);
        BigDecimal liquidoEsperado = rendimentoBruto.subtract(irEsperado).subtract(iofEsperado);

        // Then
        assertEquals(0, liquidoEsperado.compareTo(rendimentoLiquido));
        assertTrue(rendimentoLiquido.compareTo(rendimentoBruto) < 0); // Líquido deve ser menor que bruto
    }

    @Test
    @DisplayName("Deve calcular rendimento líquido para prazo longo (sem IOF)")
    void deveCalcularRendimentoLiquidoParaPrazoLongo() {
        // Given
        BigDecimal rendimentoBruto = new BigDecimal("1000.00");
        int prazoDias = 730; // Mais de 2 anos - sem IOF, IR 15%

        // When
        BigDecimal rendimentoLiquido = tributacaoService.calcularRendimentoLiquido(rendimentoBruto, prazoDias);

        // Cálculo manual
        BigDecimal liquidoEsperado = new BigDecimal("850.00"); // 1000 - 150 (IR 15%) - 0 (sem IOF)

        // Then
        assertEquals(0, liquidoEsperado.compareTo(rendimentoLiquido));
    }

    @Test
    @DisplayName("Deve calcular corretamente com valores decimais")
    void deveCalcularCorretamenteComValoresDecimais() {
        // Given
        BigDecimal rendimento = new BigDecimal("123.45");
        int prazoDias = 365;

        // When
        BigDecimal ir = tributacaoService.calcularIR(rendimento, prazoDias);
        BigDecimal iof = tributacaoService.calcularIOF(rendimento, prazoDias);
        BigDecimal liquido = tributacaoService.calcularRendimentoLiquido(rendimento, prazoDias);

        // Then
        assertNotNull(ir);
        assertNotNull(iof);
        assertNotNull(liquido);

        // Verifica se tem 2 casas decimais
        assertEquals(2, ir.scale());
        assertEquals(2, liquido.scale());

        // IOF deve ser zero para prazo > 30 dias
        assertEquals(BigDecimal.ZERO, iof);

        // IR deve ser 17,5% para prazo de 365 dias
        BigDecimal irEsperado = new BigDecimal("21.60"); // 17,5% de 123.45
        assertEquals(0, irEsperado.compareTo(ir));
    }
}