package org.example.demo;

import org.example.dto.ProdutoResponse;
import org.example.mapper.ProdutoMapper;
import org.example.model.*;

import java.math.BigDecimal;

/**
 * Demonstração da funcionalidade de classificação de risco
 */
public class DemonstrativoRisco {

    public static void main(String[] args) {
        ProdutoMapper mapper = new ProdutoMapper();
        
        System.out.println("=== DEMONSTRAÇÃO DO SISTEMA DE CLASSIFICAÇÃO DE RISCO ===\n");

        // Exemplo 1: CDB com FGC - Risco Baixo
        Produto cdb = new Produto(
                "CDB Banco XYZ", 
                TipoProduto.CDB, 
                TipoRentabilidade.POS,
                new BigDecimal("5.5"), 
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI, 
                90, 
                180, 
                true // COM FGC
        );
        cdb.setId(1L);
        
        ProdutoResponse cdbResponse = mapper.toResponse(cdb);
        exibirProduto("CDB com FGC", cdbResponse);

        // Exemplo 2: Tesouro Direto sem FGC - Risco Médio
        Produto tesouro = new Produto(
                "Tesouro IPCA+ 2030", 
                TipoProduto.TESOURO_DIRETO, 
                TipoRentabilidade.POS,
                new BigDecimal("6.2"), 
                PeriodoRentabilidade.AO_ANO,
                Indice.IPCA, 
                1, 
                30, 
                false // SEM FGC
        );
        tesouro.setId(2L);
        
        ProdutoResponse tesouroResponse = mapper.toResponse(tesouro);
        exibirProduto("Tesouro Direto sem FGC", tesouroResponse);

        // Exemplo 3: Fundo de Investimento - Risco Alto
        Produto fundo = new Produto(
                "Fundo Multimercado Agressivo", 
                TipoProduto.FUNDO, 
                TipoRentabilidade.POS,
                new BigDecimal("12.8"), 
                PeriodoRentabilidade.AO_ANO,
                Indice.IBOVESPA, 
                -1, // sem liquidez
                0, 
                false // SEM FGC
        );
        fundo.setId(3L);
        
        ProdutoResponse fundoResponse = mapper.toResponse(fundo);
        exibirProduto("Fundo de Renda Variável", fundoResponse);
        
        System.out.println("\n=== RESUMO DAS REGRAS DE CLASSIFICAÇÃO ===");
        System.out.println("• RISCO BAIXO: Produtos garantidos pelo FGC");
        System.out.println("• RISCO MÉDIO: Produtos de renda fixa não garantidos pelo FGC");
        System.out.println("• RISCO ALTO: Produtos de renda variável/mista não garantidos pelo FGC");
        System.out.println("• RISCO INDETERMINADO: Produtos com dados incompletos");
    }

    private static void exibirProduto(String titulo, ProdutoResponse produto) {
        System.out.println("--- " + titulo + " ---");
        System.out.println("Nome: " + produto.nome());
        System.out.println("Tipo: " + produto.tipo() + " (" + produto.tipo().getTipoRenda() + ")");
        System.out.println("Rentabilidade: " + produto.rentabilidade() + "% " + produto.periodoRentabilidade());
        System.out.println("FGC: " + (produto.fgc() ? "SIM" : "NÃO"));
        System.out.println("RISCO CALCULADO: " + produto.risco());
        System.out.println();
    }
}