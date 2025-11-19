package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = RentabilidadeIndiceValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRentabilidadeIndice {
    
    String message() default "Produtos com rentabilidade pós-fixada devem ter um índice associado";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}