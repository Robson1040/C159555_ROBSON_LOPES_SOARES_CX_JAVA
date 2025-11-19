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

    
    public LoginResponse autenticar(LoginRequest loginRequest) {
        
        Pessoa usuario = pessoaRepository.findByUsername(loginRequest.username());
        
        if (usuario == null) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        
        if (!passwordService.verifyPassword(loginRequest.password(), usuario.getPassword())) {
            throw new ClienteNotFoundException("Credenciais inválidas");
        }

        
        String token = jwtService.generateToken(usuario);
        
        
        return new LoginResponse(
            token,
            "Bearer",
            LocalDateTime.now().plusHours(1), 
            usuario.getUsername(),
            usuario.getRole()
        );
    }
}

