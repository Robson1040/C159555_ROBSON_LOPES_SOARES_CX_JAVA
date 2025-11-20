package br.gov.caixa.api.investimentos.validation;

import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RentabilidadeIndiceValidator implements ConstraintValidator<ValidRentabilidadeIndice, Object> {

    @Override
    public void initialize(ValidRentabilidadeIndice constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        try {

            TipoRentabilidade tipoRentabilidade = getTipoRentabilidade(object);
            Indice indice = getIndice(object);

            if (TipoRentabilidade.POS.equals(tipoRentabilidade)) {

                if (indice == null || Indice.NENHUM.equals(indice)) {

                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Produtos com rentabilidade pós-fixada devem ter um índice válido (diferente de NENHUM)"
                    ).addPropertyNode("indice").addConstraintViolation();
                    return false;
                }
            }

            return true;

        } catch (Exception e) {

            return true;
        }
    }

    private TipoRentabilidade getTipoRentabilidade(Object object) throws Exception {
        try {

            return (TipoRentabilidade) object.getClass().getMethod("tipoRentabilidade").invoke(object);
        } catch (NoSuchMethodException e) {

            return (TipoRentabilidade) object.getClass().getMethod("getTipoRentabilidade").invoke(object);
        }
    }

    private Indice getIndice(Object object) throws Exception {
        try {

            return (Indice) object.getClass().getMethod("indice").invoke(object);
        } catch (NoSuchMethodException e) {

            return (Indice) object.getClass().getMethod("getIndice").invoke(object);
        }
    }
}
