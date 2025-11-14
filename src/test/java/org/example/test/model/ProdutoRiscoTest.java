package org.example.test.model;

import org.example.model.*;
import org.example.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Teste para validar a lógica de classificação de risco
 */
public class ProdutoRiscoTest {

    @Test
    public void testRiscoBaixo_ComFGC() {
        // Arrange
        Produto produto = new Produto();
        produto.setNome("CDB Teste");
        produto.setTipo(TipoProduto.CDB);
        produto.setTipoRentabilidade(TipoRentabilidade.POS);
        produto.setRentabilidade(new BigDecimal("5.5"));
        produto.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        produto.setIndice(Indice.CDI);
        produto.setLiquidez(0);
        produto.setMinimoDiasInvestimento(0);
        produto.setFgc(true); // COM FGC

        // Act
        NivelRisco risco = produto.getRisco();

        // Assert
        assertEquals(NivelRisco.BAIXO, risco, "Produtos com FGC devem ter risco baixo");
    }

    @Test
    public void testRiscoMedio_SemFGC_RendaFixa() {
        // Arrange
        Produto produto = new Produto();
        produto.setNome("Tesouro Direto Teste");
        produto.setTipo(TipoProduto.TESOURO_DIRETO);
        produto.setTipoRentabilidade(TipoRentabilidade.POS);
        produto.setRentabilidade(new BigDecimal("6.0"));
        produto.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        produto.setIndice(Indice.IPCA);
        produto.setLiquidez(1);
        produto.setMinimoDiasInvestimento(0);
        produto.setFgc(false); // SEM FGC

        // Act
        NivelRisco risco = produto.getRisco();

        // Assert
        assertEquals(NivelRisco.MEDIO, risco, "Produtos sem FGC de renda fixa devem ter risco médio");
    }

    @Test
    public void testRiscoAlto_SemFGC_RendaVariavel() {
        // Arrange
        Produto produto = new Produto();
        produto.setNome("Fundo Teste");
        produto.setTipo(TipoProduto.FUNDO);
        produto.setTipoRentabilidade(TipoRentabilidade.POS);
        produto.setRentabilidade(new BigDecimal("8.0"));
        produto.setPeriodoRentabilidade(PeriodoRentabilidade.AO_ANO);
        produto.setIndice(Indice.IBOVESPA);
        produto.setLiquidez(-1);
        produto.setMinimoDiasInvestimento(0);
        produto.setFgc(false); // SEM FGC

        // Act
        NivelRisco risco = produto.getRisco();

        // Assert
        assertEquals(NivelRisco.ALTO, risco, "Produtos sem FGC de renda variável devem ter risco alto");
    }

    @Test
    public void testRiscoIndeterminado_DadosIncompletos() {
        // Arrange
        Produto produto = new Produto();
        produto.setNome("Produto Incompleto");
        // Não define tipo nem FGC

        // Act
        NivelRisco risco = produto.getRisco();

        // Assert
        assertEquals(NivelRisco.INDETERMINADO, risco, "Produtos com dados incompletos devem ter risco indeterminado");
    }

    @Test
    public void testTipoRendaProdutos() {
        // Verificar se os tipos de produto têm o tipo de renda correto
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.CDB.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.LCI.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.LCA.getTipoRenda());
        assertEquals(TipoRenda.RENDA_VARIAVEL, TipoProduto.FUNDO.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.TESOURO_DIRETO.getTipoRenda());
        assertEquals(TipoRenda.RENDA_FIXA, TipoProduto.POUPANCA.getTipoRenda());
    }
}