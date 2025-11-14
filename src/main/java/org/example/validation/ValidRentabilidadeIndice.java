package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para verificar se produtos com rentabilidade pós-fixada
 * possuem um índice obrigatoriamente associado.
 * 
 * Regras:
 * - TipoRentabilidade.POS: índice é obrigatório (não pode ser null ou NENHUM)
 * - TipoRentabilidade.PRE: índice é opcional (pode ser null ou NENHUM)
 */
@Documented
@Constraint(validatedBy = RentabilidadeIndiceValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRentabilidadeIndice {
    
    String message() default "Produtos com rentabilidade pós-fixada devem ter um índice associado";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}