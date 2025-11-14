package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.dto.SimulacaoRequest;

/**
 * Implementação da validação customizada para prazo
 * Verifica se pelo menos um dos campos de prazo foi informado
 */
public class ValidPrazoValidator implements ConstraintValidator<ValidPrazo, SimulacaoRequest> {

    @Override
    public void initialize(ValidPrazo constraintAnnotation) {
        // Inicialização se necessária
    }

    @Override
    public boolean isValid(SimulacaoRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // @NotNull deve lidar com null
        }

        boolean hasPrazoMeses = request.prazoMeses() != null && request.prazoMeses() > 0;
        boolean hasPrazoDias = request.prazoDias() != null && request.prazoDias() > 0;
        boolean hasPrazoAnos = request.prazoAnos() != null && request.prazoAnos() > 0;

        // Pelo menos um prazo deve estar preenchido
        boolean isValid = hasPrazoMeses || hasPrazoDias || hasPrazoAnos;

        if (!isValid) {
            // Personaliza a mensagem de erro
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Informe pelo menos um prazo: 'prazoMeses' (1-600), 'prazoDias' (1-18250) ou 'prazoAnos' (1-50)"
            ).addConstraintViolation();
        }

        // Valida se apenas um prazo foi informado (opcional - remove se quiser permitir múltiplos)
        int prazosInformados = 0;
        if (hasPrazoMeses) prazosInformados++;
        if (hasPrazoDias) prazosInformados++;
        if (hasPrazoAnos) prazosInformados++;

        if (prazosInformados > 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Informe apenas UM tipo de prazo por simulação (prazoMeses, prazoDias ou prazoAnos)"
            ).addConstraintViolation();
            return false;
        }

        return isValid;
    }
}