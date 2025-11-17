package br.gov.caixa.api.investimentos.service.autenticacao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService();

    @Test
    void testEncryptPasswordReturnsNonNull() {
        String password = "senha123";
        String hash = passwordService.encryptPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash); // hash não pode ser igual à senha
    }

    @Test
    void testVerifyPasswordCorrectPassword() {
        String password = "senha123";
        String hash = passwordService.encryptPassword(password);

        assertTrue(passwordService.verifyPassword(password, hash));
    }

    @Test
    void testVerifyPasswordIncorrectPassword() {
        String password = "senha123";
        String wrongPassword = "senhaErrada";
        String hash = passwordService.encryptPassword(password);

        assertFalse(passwordService.verifyPassword(wrongPassword, hash));
    }

    @Test
    void testVerifyPasswordWithTamperedHash() {
        String password = "senha123";
        String hash = passwordService.encryptPassword(password);

        // Modifica o hash
        String tampered = hash + "SJASHAJSHASHAJ";

        assertFalse(passwordService.verifyPassword(password, tampered));
    }

    @Test
    void testMultipleEncryptionsProduceDifferentHashes() {
        String password = "senha123";

        String hash1 = passwordService.encryptPassword(password);
        String hash2 = passwordService.encryptPassword(password);

        assertNotEquals(hash1, hash2); // Salt aleatório garante hashes diferentes
    }

    @Test
    void testVerifyPasswordWithEmptyOrNull() {
        assertFalse(passwordService.verifyPassword("senha123", null));
        assertFalse(passwordService.verifyPassword(null, "hash"));
        assertFalse(passwordService.verifyPassword(null, null));
    }
}