package org.example.test.validation;

import org.example.dto.produto.ProdutoRequest;
import org.example.model.*;
import org.example.enums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para validação customizada de rentabilidade e índice
 */
public class RentabilidadeIndiceValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testProdutoPosFiadoComIndiceValido_DevePassarValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "CDB Pós CDI",
                TipoProduto.CDB,
                TipoRentabilidade.POS, // PÓS-FIXADO
                new BigDecimal("5.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.CDI, // ÍNDICE VÁLIDO
                90,
                180,
                true
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Produto pós-fixado com índice válido deve passar na validação");
    }

    @Test
    public void testProdutoPosFiadoSemIndice_DeveFalharValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "CDB Pós sem índice",
                TipoProduto.CDB,
                TipoRentabilidade.POS, // PÓS-FIXADO
                new BigDecimal("5.5"),
                PeriodoRentabilidade.AO_ANO,
                null, // SEM ÍNDICE
                90,
                180,
                true
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Produto pós-fixado sem índice deve falhar na validação");
        
        boolean temViolacaoIndice = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("indice"));
        assertTrue(temViolacaoIndice, "Deve haver violação relacionada ao campo índice");
    }

    @Test
    public void testProdutoPosFiadoComIndiceNenhum_DeveFalharValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "CDB Pós com índice NENHUM",
                TipoProduto.CDB,
                TipoRentabilidade.POS, // PÓS-FIXADO
                new BigDecimal("5.5"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM, // ÍNDICE INVÁLIDO PARA PÓS
                90,
                180,
                true
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Produto pós-fixado com índice NENHUM deve falhar na validação");
        
        boolean temViolacaoIndice = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("indice"));
        assertTrue(temViolacaoIndice, "Deve haver violação relacionada ao campo índice");
    }

    @Test
    public void testProdutoPreFiadoSemIndice_DevePassarValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "CDB Pré-fixado",
                TipoProduto.CDB,
                TipoRentabilidade.PRE, // PRÉ-FIXADO
                new BigDecimal("8.5"),
                PeriodoRentabilidade.AO_ANO,
                null, // SEM ÍNDICE (PERMITIDO PARA PRÉ)
                90,
                180,
                true
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Produto pré-fixado sem índice deve passar na validação");
    }

    @Test
    public void testProdutoPreFiadoComIndiceNenhum_DevePassarValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "Tesouro Pré-fixado",
                TipoProduto.TESOURO_DIRETO,
                TipoRentabilidade.PRE, // PRÉ-FIXADO
                new BigDecimal("10.2"),
                PeriodoRentabilidade.AO_ANO,
                Indice.NENHUM, // PERMITIDO PARA PRÉ
                1,
                30,
                false
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Produto pré-fixado com índice NENHUM deve passar na validação");
    }

    @Test
    public void testProdutoPreFiadoComIndiceValido_DevePassarValidacao() {
        ProdutoRequest request = new ProdutoRequest(
                "LCI Pré com índice",
                TipoProduto.LCI,
                TipoRentabilidade.PRE, // PRÉ-FIXADO
                new BigDecimal("7.8"),
                PeriodoRentabilidade.AO_ANO,
                Indice.SELIC, // TAMBÉM PERMITIDO PARA PRÉ
                180,
                360,
                true
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Produto pré-fixado com índice válido deve passar na validação");
    }

    @Test
    public void testValidacaoComEntidadeProduto() {
        // Teste usando a entidade Produto diretamente
        Produto produto = new Produto(
                "Produto Pós sem índice",
                TipoProduto.FUNDO,
                TipoRentabilidade.POS,
                new BigDecimal("12.0"),
                PeriodoRentabilidade.AO_ANO,
                null, // SEM ÍNDICE
                -1,
                0,
                false
        );

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);
        assertFalse(violations.isEmpty(), "Entidade Produto com pós-fixado sem índice deve falhar na validação");
    }
}