package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.exception.ClienteNotFoundException;
import org.example.model.Pessoa;

import java.time.LocalDateTime;

@ApplicationScoped
public class AutenticacaoService {

    @Inject
    JwtService jwtService;

    @Inject
    PasswordService passwordService;

    /**
     * Autentica um usuário com username e password
     */
    public LoginResponse autenticar(LoginRequest loginRequest) {
        // Buscar usuário por username
        Pessoa usuario = Pessoa.findByUsername(loginRequest.username());
        
        if (usuario == null) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        // Verificar password
        if (!passwordService.verifyPassword(loginRequest.password(), usuario.password)) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(usuario);
        
        // Retornar resposta com token
        return new LoginResponse(
            token,
            "Bearer",
            LocalDateTime.now().plusHours(1), // Expira em 1 hora
            usuario.username,
            usuario.role
        );
    }
}