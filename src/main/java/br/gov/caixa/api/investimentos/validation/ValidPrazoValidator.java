package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.investimento.InvestimentoRequest;


public class ValidPrazoValidator implements ConstraintValidator<ValidPrazo, Object> {

    @Override
    public void initialize(ValidPrazo constraintAnnotation) {
        
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }

        Integer prazoMeses = null;
        Integer prazoDias = null;
        Integer prazoAnos = null;

        if (value instanceof SimulacaoRequest req) {
            prazoMeses = req.prazoMeses();
            prazoDias = req.prazoDias();
            prazoAnos = req.prazoAnos();
        } else if (value instanceof InvestimentoRequest req) {
            prazoMeses = req.prazoMeses();
            prazoDias = req.prazoDias();
            prazoAnos = req.prazoAnos();
        } else {
            
            return true;
        }

        boolean hasPrazoMeses = prazoMeses != null && prazoMeses > 0;
        boolean hasPrazoDias = prazoDias != null && prazoDias > 0;
        boolean hasPrazoAnos = prazoAnos != null && prazoAnos > 0;

        boolean isValid = hasPrazoMeses || hasPrazoDias || hasPrazoAnos;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Informe pelo menos um prazo: 'prazoMeses' (1-600), 'prazoDias' (1-18250) ou 'prazoAnos' (1-50)"
            ).addConstraintViolation();
            return false;
        }

        int prazosInformados = 0;
        if (hasPrazoMeses) prazosInformados++;
        if (hasPrazoDias) prazosInformados++;
        if (hasPrazoAnos) prazosInformados++;

        if (prazosInformados > 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Informe apenas UM tipo de prazo (prazoMeses, prazoDias ou prazoAnos)"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}