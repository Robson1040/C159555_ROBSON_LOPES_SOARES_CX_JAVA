package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.enums.Indice;
import org.example.enums.TipoRentabilidade;

/**
 * Implementação do validator para verificar se a rentabilidade e índice estão consistentes.
 * 
 * A validação funciona através de reflection, verificando se o objeto possui os métodos
 * getTipoRentabilidade() e getIndice() para fazer a validação.
 */
public class RentabilidadeIndiceValidator implements ConstraintValidator<ValidRentabilidadeIndice, Object> {

    @Override
    public void initialize(ValidRentabilidadeIndice constraintAnnotation) {
        // Inicialização se necessária
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // null values são tratadas por @NotNull se necessário
        }

        try {
            // Usa reflection para obter os valores dos campos
            TipoRentabilidade tipoRentabilidade = getTipoRentabilidade(object);
            Indice indice = getIndice(object);

            // Se o tipo de rentabilidade é pós-fixada
            if (TipoRentabilidade.POS.equals(tipoRentabilidade)) {
                // Índice não pode ser null nem NENHUM
                if (indice == null || Indice.NENHUM.equals(indice)) {
                    // Personaliza a mensagem de erro
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        "Produtos com rentabilidade pós-fixada devem ter um índice válido (diferente de NENHUM)"
                    ).addPropertyNode("indice").addConstraintViolation();
                    return false;
                }
            }
            
            // Para PRE ou outros casos, não há restrição
            return true;
            
        } catch (Exception e) {
            // Se houver erro na reflection, considera válido para não quebrar outras validações
            return true;
        }
    }

    /**
     * Obtém o tipo de rentabilidade do objeto usando reflection
     */
    private TipoRentabilidade getTipoRentabilidade(Object object) throws Exception {
        try {
            // Tenta primeiro o padrão record
            return (TipoRentabilidade) object.getClass().getMethod("tipoRentabilidade").invoke(object);
        } catch (NoSuchMethodException e) {
            // Se não encontrar, tenta o padrão tradicional (entidades JPA)
            return (TipoRentabilidade) object.getClass().getMethod("getTipoRentabilidade").invoke(object);
        }
    }

    /**
     * Obtém o índice do objeto usando reflection
     */
    private Indice getIndice(Object object) throws Exception {
        try {
            // Tenta primeiro o padrão record
            return (Indice) object.getClass().getMethod("indice").invoke(object);
        } catch (NoSuchMethodException e) {
            // Se não encontrar, tenta o padrão tradicional (entidades JPA)
            return (Indice) object.getClass().getMethod("getIndice").invoke(object);
        }
    }
}