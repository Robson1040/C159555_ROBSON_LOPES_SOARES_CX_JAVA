package org.example.demo;

import org.example.dto.produto.ProdutoRequest;
import org.example.model.*;
import org.example.enums.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Demonstração da validação de rentabilidade e índice
 */
public class DemonstrativoValidacao {

    public static void main(String[] args) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        System.out.println("=== DEMONSTRAÇÃO DA VALIDAÇÃO RENTABILIDADE x ÍNDICE ===\n");

        // Caso 1: PÓS-FIXADO COM ÍNDICE (VÁLIDO)
        System.out.println("1. Produto PÓS-FIXADO com índice CDI (VÁLIDO)");
        ProdutoRequest cdbPos = new ProdutoRequest(
                "CDB Pós CDI",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("5.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI,
                90, 180, true
        );
        validarEExibir(validator, cdbPos);

        // Caso 2: PÓS-FIXADO SEM ÍNDICE (INVÁLIDO)
        System.out.println("2. Produto PÓS-FIXADO sem índice (INVÁLIDO)");
        ProdutoRequest posSemIndice = new ProdutoRequest(
                "CDB Pós sem índice",
                TipoProduto.CDB,
                TipoRentabilidade.POS,
                new BigDecimal("5.5"),
                PeriodoRentabilidade.AO_ANO,
                null, // SEM ÍNDICE
                90, 180, true
        );
        validarEExibir(validator, posSemIndice);

        // Caso 3: PÓS-FIXADO COM ÍNDICE "NENHUM" (INVÁLIDO)
        System.out.println("3. Produto PÓS-FIXADO com índice NENHUM (INVÁLIDO)");
        ProdutoRequest posComNenhum = new ProdutoRequest(
                "Tesouro Pós com NENHUM",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.POS,
                new BigDecimal("6.0"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM, // INVÁLIDO PARA PÓS
                1, 30, false
        );
        validarEExibir(validator, posComNenhum);

        // Caso 4: PRÉ-FIXADO SEM ÍNDICE (VÁLIDO)
        System.out.println("4. Produto PRÉ-FIXADO sem índice (VÁLIDO)");
        ProdutoRequest preSemIndice = new ProdutoRequest(
                "CDB Pré-fixado",
                TipoProduto.CDB,
                TipoRentabilidade.PRE,
                new BigDecimal("8.5"),
                PeriodoRentabilidade.AO_ANO,
                null, // SEM ÍNDICE - OK PARA PRÉ
                90, 180, true
        );
        validarEExibir(validator, preSemIndice);

        // Caso 5: PRÉ-FIXADO COM ÍNDICE "NENHUM" (VÁLIDO)
        System.out.println("5. Produto PRÉ-FIXADO com índice NENHUM (VÁLIDO)");
        ProdutoRequest preComNenhum = new ProdutoRequest(
                "LCI Pré-fixada",
                TipoProduto.LCI,
                TipoRentabilidade.PRE,
                new BigDecimal("7.8"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM, // OK PARA PRÉ
                180, 360, true
        );
        validarEExibir(validator, preComNenhum);

        // Caso 6: PRÉ-FIXADO COM ÍNDICE VÁLIDO (VÁLIDO)
        System.out.println("6. Produto PRÉ-FIXADO com índice SELIC (VÁLIDO)");
        ProdutoRequest preComIndice = new ProdutoRequest(
                "Tesouro Selic",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.PRE,
                new BigDecimal("10.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.SELIC, // OK PARA PRÉ TAMBÉM
                1, 30, false
        );
        validarEExibir(validator, preComIndice);

        System.out.println("\n=== RESUMO DAS REGRAS ===");
        System.out.println("• RENTABILIDADE PÓS: índice obrigatório (não pode ser null ou NENHUM)");
        System.out.println("• RENTABILIDADE PRÉ: índice opcional (pode ser null, NENHUM ou qualquer índice válido)");
        factory.close();
    }

    private static void validarEExibir(Validator validator, ProdutoRequest request) {
        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        
        System.out.println("   Produto: " + request.nome());
        System.out.println("   Tipo Rentabilidade: " + request.tipoRentabilidade());
        System.out.println("   Índice: " + request.indice());
        
        if (violations.isEmpty()) {
            System.out.println("   ✅ VALIDAÇÃO PASSOU");
        } else {
            System.out.println("   ❌ VALIDAÇÃO FALHOU:");
            violations.forEach(v -> 
                System.out.println("      - " + v.getMessage())
            );
        }
        System.out.println();
    }
}