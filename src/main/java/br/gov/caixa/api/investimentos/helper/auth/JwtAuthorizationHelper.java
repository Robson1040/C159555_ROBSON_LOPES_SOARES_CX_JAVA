package br.gov.caixa.api.investimentos.helper.auth;

import br.gov.caixa.api.investimentos.exception.auth.AccessDeniedException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class JwtAuthorizationHelper {

    
    public void validarAcessoAoCliente(JsonWebToken jwt, Long clienteId) {
        if (jwt == null) {
            throw new AccessDeniedException("Token JWT não encontrado");
        }

        
        if (jwt.getGroups().contains("ADMIN")) {
            
            return;
        }

        if (jwt.getGroups().contains("USER")) {
            

            try
            {
                Long userIdJwt = Long.valueOf(jwt.getClaim("userId").toString());


                if (userIdJwt == null || !userIdJwt.equals(clienteId)) {
                    throw new AccessDeniedException("Acesso negado: usuário só pode acessar seus próprios dados");
                }
            }
            catch(Exception e)
            {
                throw new AccessDeniedException("Acesso negado: usuário só pode acessar seus próprios dados");
            }

            return;
        }

        
        throw new AccessDeniedException("Acesso negado: role não autorizada");
    }

    
    public boolean isAdmin(JsonWebToken jwt) {
        return jwt != null && jwt.getGroups().contains("ADMIN");
    }

    
    public boolean isUser(JsonWebToken jwt) {
        return jwt != null && jwt.getGroups().contains("USER");
    }

    
    public Long getUserId(JsonWebToken jwt) {
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("userId");
    }
}