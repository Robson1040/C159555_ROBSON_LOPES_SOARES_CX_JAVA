package br.gov.caixa.api.investimentos.validation;

import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentabilidadeIndiceValidatorTest {

    private RentabilidadeIndiceValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new RentabilidadeIndiceValidator();

        // Mock do contexto
        context = mock(ConstraintValidatorContext.class);

        // Mock do ConstraintViolationBuilder
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                mock(ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        // Encadeamento de chamadas do builder
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        doNothing().when(context).disableDefaultConstraintViolation();
    }

    // ===== Record simulado =====
    private record SimulacaoRecord(TipoRentabilidade tipoRentabilidade, Indice indice) {}

    @Test
    void preFixadoWithNullOrNenhumIndiceShouldBeValid() {
        SimulacaoRecord recordNull = new SimulacaoRecord(TipoRentabilidade.PRE, null);
        SimulacaoRecord recordNenhum = new SimulacaoRecord(TipoRentabilidade.PRE, Indice.NENHUM);

        assertTrue(validator.isValid(recordNull, context));
        assertTrue(validator.isValid(recordNenhum, context));
    }

    @Test
    void posFixadoWithValidIndiceShouldBeValid() {
        SimulacaoRecord record = new SimulacaoRecord(TipoRentabilidade.POS, Indice.CDI);
        assertTrue(validator.isValid(record, context));
    }

    @Test
    void posFixadoWithNullOrNenhumIndiceShouldBeInvalid() {
        SimulacaoRecord recordNull = new SimulacaoRecord(TipoRentabilidade.POS, null);
        SimulacaoRecord recordNenhum = new SimulacaoRecord(TipoRentabilidade.POS, Indice.NENHUM);

        assertFalse(validator.isValid(recordNull, context));
        assertFalse(validator.isValid(recordNenhum, context));
    }

    @Test
    void shouldReturnTrueForNullObject() {
        assertTrue(validator.isValid(null, context));
    }

    // ===== POJO tradicional com getters =====
    private static class SimulacaoPojo {
        private final TipoRentabilidade tipoRentabilidade;
        private final Indice indice;

        SimulacaoPojo(TipoRentabilidade tipoRentabilidade, Indice indice) {
            this.tipoRentabilidade = tipoRentabilidade;
            this.indice = indice;
        }

        public TipoRentabilidade getTipoRentabilidade() { return tipoRentabilidade; }
        public Indice getIndice() { return indice; }
    }

    @Test
    void pojoPosFixadoWithValidIndiceShouldBeValid() {
        SimulacaoPojo pojo = new SimulacaoPojo(TipoRentabilidade.POS, Indice.SELIC);
        assertTrue(validator.isValid(pojo, context));
    }

    @Test
    void pojoPosFixadoWithNullOrNenhumIndiceShouldBeInvalid() {
        SimulacaoPojo pojoNull = new SimulacaoPojo(TipoRentabilidade.POS, null);
        SimulacaoPojo pojoNenhum = new SimulacaoPojo(TipoRentabilidade.POS, Indice.NENHUM);

        assertFalse(validator.isValid(pojoNull, context));
        assertFalse(validator.isValid(pojoNenhum, context));
    }

    @Test
    void pojoPreFixadoWithNullOrNenhumIndiceShouldBeValid() {
        SimulacaoPojo pojoNull = new SimulacaoPojo(TipoRentabilidade.PRE, null);
        SimulacaoPojo pojoNenhum = new SimulacaoPojo(TipoRentabilidade.PRE, Indice.NENHUM);

        assertTrue(validator.isValid(pojoNull, context));
        assertTrue(validator.isValid(pojoNenhum, context));
    }
}