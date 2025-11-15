package br.gov.caixa.api.investimentos.service.autenticacao;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;

import java.util.Set;

@ApplicationScoped
public class JwtService {

    /**
     * Gera token usando automaticamente a PRIVATE KEY configurada no application.properties
     */
    public String generateToken(Pessoa usuario) {

        return Jwt.issuer("api-investimentos-caixa")
                .subject(usuario.getUsername())
                .groups(Set.of(usuario.getRole()))   // vira o campo "groups" -> usado no @RolesAllowed
                .claim("nome", usuario.getNome())
                .claim("cpf", usuario.getCpf())
                .claim("userId", usuario.getId())
                .expiresIn(3600) // 1 hora
                .sign();
    }

    /**
     * Gera token simples para testes
     */
    public String gerarToken(String email, String role) {

        return Jwt.issuer("api-investimentos-caixa")
                .subject(email)
                .groups(Set.of(role))
                .claim("email", email)
                .expiresIn(3600)
                .sign();
    }
}