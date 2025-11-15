package br.gov.caixa.api.investimentos.service.autenticacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;

import java.time.LocalDateTime;

@ApplicationScoped
public class AutenticacaoService {

    @Inject
    JwtService jwtService;

    @Inject
    PasswordService passwordService;

    @Inject
    IPessoaRepository pessoaRepository;

    /**
     * Autentica um usuário com username e password
     */
    public LoginResponse autenticar(LoginRequest loginRequest) {
        // Buscar usuário por username
        Pessoa usuario = pessoaRepository.findByUsername(loginRequest.username());
        
        if (usuario == null) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        // Verificar password
        if (!passwordService.verifyPassword(loginRequest.password(), usuario.getPassword())) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(usuario);
        
        // Retornar resposta com token
        return new LoginResponse(
            token,
            "Bearer",
            LocalDateTime.now().plusHours(1), // Expira em 1 hora
            usuario.getUsername(),
            usuario.getRole()
        );
    }
}

