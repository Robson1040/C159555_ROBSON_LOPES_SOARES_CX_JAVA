package br.gov.caixa.api.investimentos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (inválido)
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int[] digits = cpf.chars().map(c -> c - '0').toArray();

            // Calcula primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += digits[i] * (10 - i);
            }
            int dv1 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
            if (digits[9] != dv1) return false;

            // Calcula segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += digits[i] * (11 - i);
            }
            int dv2 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
            return digits[10] == dv2;
        } catch (Exception e) {
            return false;
        }
    }
}
