package br.gov.caixa.api.investimentos.service.autenticacao;

import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class JwtService {

    public String generateToken(Pessoa usuario) {

        return Jwt.issuer("api-investimentos-caixa")
                .subject(usuario.getUsername())
                .groups(Set.of(usuario.getRole()))
                .claim("nome", usuario.getNome())
                .claim("cpf", usuario.getCpf())
                .claim("userId", usuario.getId())
                .expiresIn(3600)
                .sign();
    }

    public String gerarToken(String email, String role) {

        return Jwt.issuer("api-investimentos-caixa")
                .subject(email)
                .groups(Set.of(role))
                .claim("email", email)
                .claim("userId", 9999)
                .expiresIn(3600)
                .sign();
    }

    public String gerarToken(String email, String role, Long userId) {

        return Jwt.issuer("api-investimentos-caixa")
                .subject(email)
                .groups(Set.of(role))
                .claim("email", email)
                .claim("userId", userId)
                .expiresIn(3600)
                .sign();
    }
}