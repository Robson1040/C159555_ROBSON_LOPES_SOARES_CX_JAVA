package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPrazoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrazo {
    String message() default "Pelo menos um prazo deve ser informado (prazoMeses, prazoDias ou prazoAnos)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}