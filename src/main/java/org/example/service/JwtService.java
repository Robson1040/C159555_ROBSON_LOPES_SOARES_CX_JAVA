package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.model.Pessoa;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@ApplicationScoped
public class JwtService {

    private static final String SECRET_KEY = "minha-chave-secreta-super-segura-para-jwt-tokens-1234567890";
    private static final String ALGORITHM = "HmacSHA256";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gera um token JWT para o usuário autenticado
     */
    public String generateToken(Pessoa usuario) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plus(1, ChronoUnit.HOURS);

            // Header
            Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
            );

            // Payload
            Map<String, Object> payload = Map.of(
                "sub", usuario.username,
                "nome", usuario.nome,
                "cpf", usuario.cpf,
                "userId", usuario.id,
                "role", usuario.role,
                "iat", now.getEpochSecond(),
                "exp", expiry.getEpochSecond(),
                "iss", "api-veiculo-sso"
            );

            // Encode header and payload
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(header));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(payload));

            // Create signature
            String data = encodedHeader + "." + encodedPayload;
            String signature = createSignature(data);

            return data + "." + signature;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    private String createSignature(String data) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKeySpec);
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar assinatura JWT", e);
        }
    }

    /**
     * Extrai informações do token (para uso futuro)
     */
    public String extractUsername(String token) {
        // Implementação futura para validar e extrair dados do token
        // Por enquanto retorna null, será implementado quando necessário
        return null;
    }

    /**
     * Valida se o token é válido (para uso futuro)
     */
    public boolean isTokenValid(String token) {
        // Implementação futura para validar token
        // Por enquanto retorna false, será implementado quando necessário
        return false;
    }
}