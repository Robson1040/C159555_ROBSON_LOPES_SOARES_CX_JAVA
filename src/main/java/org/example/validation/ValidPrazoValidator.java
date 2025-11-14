package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.dto.simulacao.SimulacaoRequest;
import org.example.dto.investimento.InvestimentoRequest;

/**
 * Implementação da validação customizada para prazo.
 * Agora reutilizável para SimulacaoRequest e InvestimentoRequest.
 */
public class ValidPrazoValidator implements ConstraintValidator<ValidPrazo, Object> {

    @Override
    public void initialize(ValidPrazo constraintAnnotation) {
        // Nenhuma inicialização necessária
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull deve tratar null quando aplicável
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
            // Tipo não suportado, consideramos válido para não quebrar outros usos
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