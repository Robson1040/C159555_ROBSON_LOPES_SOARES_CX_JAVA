package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para garantir que pelo menos um campo de prazo seja informado
 * na SimulacaoRequest (prazoMeses, prazoDias ou prazoAnos)
 */
@Documented
@Constraint(validatedBy = ValidPrazoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrazo {
    String message() default "Pelo menos um prazo deve ser informado (prazoMeses, prazoDias ou prazoAnos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}