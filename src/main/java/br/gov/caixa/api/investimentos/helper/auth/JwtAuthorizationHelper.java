package br.gov.caixa.api.investimentos.helper.auth;

import br.gov.caixa.api.investimentos.exception.auth.AccessDeniedException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Helper para validações de autorização baseadas em JWT
 */
@ApplicationScoped
public class JwtAuthorizationHelper {

    /**
     * Valida se o usuário logado tem acesso aos dados do cliente solicitado
     * - ADMIN: pode acessar qualquer cliente
     * - USER: pode acessar apenas seus próprios dados
     *
     * @param jwt o token JWT do usuário logado
     * @param clienteId o ID do cliente que se deseja acessar
     * @throws AccessDeniedException se o acesso for negado
     */
    public void validarAcessoAoCliente(JsonWebToken jwt, Long clienteId) {
        if (jwt == null) {
            throw new AccessDeniedException("Token JWT não encontrado");
        }

        // Obtém as roles do usuário logado
        if (jwt.getGroups().contains("ADMIN")) {
            // Admin pode acessar qualquer cliente
            return;
        }

        if (jwt.getGroups().contains("USER")) {
            // Obtém o userId do token JWT
            Long userIdJwt = jwt.getClaim("userId");

            if (userIdJwt == null || !userIdJwt.equals(clienteId)) {
                throw new AccessDeniedException("Acesso negado: usuário só pode acessar seus próprios dados");
            }
            return;
        }

        // Se não tem role válida, nega acesso
        throw new AccessDeniedException("Acesso negado: role não autorizada");
    }

    /**
     * Verifica se o usuário logado é um administrador
     *
     * @param jwt o token JWT do usuário logado
     * @return true se for admin, false caso contrário
     */
    public boolean isAdmin(JsonWebToken jwt) {
        return jwt != null && jwt.getGroups().contains("ADMIN");
    }

    /**
     * Verifica se o usuário logado é um usuário comum
     *
     * @param jwt o token JWT do usuário logado
     * @return true se for user, false caso contrário
     */
    public boolean isUser(JsonWebToken jwt) {
        return jwt != null && jwt.getGroups().contains("USER");
    }

    /**
     * Obtém o ID do usuário a partir do token JWT
     *
     * @param jwt o token JWT do usuário logado
     * @return o ID do usuário ou null se não encontrado
     */
    public Long getUserId(JsonWebToken jwt) {
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("userId");
    }
}