package br.gov.caixa.api.investimentos.service.autenticacao;

import br.gov.caixa.api.investimentos.dto.autenticacao.LoginRequest;
import br.gov.caixa.api.investimentos.dto.autenticacao.LoginResponse;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Mock
    private IPessoaRepository pessoaRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutenticarSucesso() {
        LoginRequest request = new LoginRequest("usuario", "senha123");

        Pessoa usuario = new Pessoa("Robson Lopes", "12345678901", "usuario", "hashedPass", "USER");
        usuario.setId(1L);

        when(pessoaRepository.findByUsername("usuario")).thenReturn(usuario);
        when(passwordService.verifyPassword("senha123", "hashedPass")).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("TOKEN123");

        LoginResponse response = autenticacaoService.autenticar(request);

        assertNotNull(response);
        assertEquals("TOKEN123", response.token());
        assertEquals("usuario", response.usuario());
        assertEquals("USER", response.role());

        verify(pessoaRepository).findByUsername("usuario");
        verify(passwordService).verifyPassword("senha123", "hashedPass");
        verify(jwtService).generateToken(usuario);
    }

    @Test
    void testAutenticarUsuarioNaoEncontrado() {
        LoginRequest request = new LoginRequest("usuario", "senha123");

        when(pessoaRepository.findByUsername("usuario")).thenReturn(null);

        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class,
                () -> autenticacaoService.autenticar(request));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(pessoaRepository).findByUsername("usuario");
        verifyNoInteractions(passwordService, jwtService);
    }

    @Test
    void testAutenticarSenhaInvalida() {
        LoginRequest request = new LoginRequest("usuario", "senhaErrada");

        Pessoa usuario = new Pessoa("Robson Lopes", "12345678901", "usuario", "hashedPass", "USER");
        usuario.setId(1L);

        when(pessoaRepository.findByUsername("usuario")).thenReturn(usuario);
        when(passwordService.verifyPassword("senhaErrada", "hashedPass")).thenReturn(false);

        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class,
                () -> autenticacaoService.autenticar(request));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(pessoaRepository).findByUsername("usuario");
        verify(passwordService).verifyPassword("senhaErrada", "hashedPass");
        verifyNoInteractions(jwtService);
    }
}