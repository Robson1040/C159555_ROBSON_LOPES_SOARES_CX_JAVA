package br.gov.caixa.api.investimentos.service.cliente;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.exception.cliente.ClienteNotFoundException;
import br.gov.caixa.api.investimentos.mapper.PessoaMapper;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import br.gov.caixa.api.investimentos.repository.cliente.IPessoaRepository;
import br.gov.caixa.api.investimentos.service.autenticacao.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para ClienteService")
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private PessoaMapper pessoaMapper;

    @Mock
    private IPessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void deveListarTodosOsClientesComSucesso() {
        // Given
        List<Pessoa> pessoas = List.of(createPessoa(1L, "12345678901"), createPessoa(2L, "12345678902"));
        List<ClienteResponse> responsesEsperadas = List.of(
                createClienteResponse(1L, "João Silva"),
                createClienteResponse(2L, "Maria Santos")
        );

        when(pessoaRepository.listAll()).thenReturn(pessoas);
        when(pessoaMapper.toClienteResponseList(pessoas)).thenReturn(responsesEsperadas);

        // When
        List<ClienteResponse> result = clienteService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(responsesEsperadas, result);
        verify(pessoaRepository, times(1)).listAll();
        verify(pessoaMapper, times(1)).toClienteResponseList(pessoas);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Given
        Long clienteId = 1L;
        Pessoa pessoa = createPessoa(clienteId, "12345678901");
        ClienteResponse responseEsperada = createClienteResponse(clienteId, "João Silva");

        when(pessoaRepository.findById(clienteId)).thenReturn(pessoa);
        when(pessoaMapper.toClienteResponse(pessoa)).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.buscarPorId(clienteId);

        // Then
        assertNotNull(result);
        assertEquals(responseEsperada, result);
        verify(pessoaRepository, times(1)).findById(clienteId);
        verify(pessoaMapper, times(1)).toClienteResponse(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por ID inexistente")
    void deveLancarExcecaoAoBuscarClientePorIdInexistente() {
        // Given
        Long clienteId = 999L;
        when(pessoaRepository.findById(clienteId)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.buscarPorId(clienteId)
        );

        assertEquals("Cliente não encontrado com ID: " + clienteId, exception.getMessage());
        verify(pessoaRepository, times(1)).findById(clienteId);
        verifyNoInteractions(pessoaMapper);
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        // Given
        ClienteRequest request = new ClienteRequest(
                "João Silva", "12345678901", "joao.silva", "senha123", "USER"
        );
        Pessoa pessoa = createPessoa(null, request.cpf());

        ClienteResponse responseEsperada = createClienteResponse(1L, request.nome());

        when(pessoaRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(pessoaRepository.existsByUsername(request.username())).thenReturn(false);
        when(pessoaMapper.toEntity(request)).thenReturn(pessoa);
        when(passwordService.encryptPassword(request.password())).thenReturn("senhaEncriptada");
        when(pessoaMapper.toClienteResponse(any(Pessoa.class))).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.criar(request);

        // Then
        assertNotNull(result);
        assertEquals(responseEsperada, result);
        verify(pessoaRepository, times(1)).existsByCpf(request.cpf());
        verify(pessoaRepository, times(1)).existsByUsername(request.username());
        verify(pessoaMapper, times(1)).toEntity(request);
        verify(passwordService, times(1)).encryptPassword(request.password());
        verify(pessoaRepository, times(1)).persist(any(Pessoa.class));
        verify(pessoaMapper, times(1)).toClienteResponse(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF duplicado")
    void deveLancarExcecaoAoCriarClienteComCpfDuplicado() {
        // Given
        ClienteRequest request = new ClienteRequest(
                "João Silva", "12345678901", "joao.silva", "senha123", "USER"
        );
        when(pessoaRepository.existsByCpf(request.cpf())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.criar(request)
        );

        assertEquals("Já existe um cliente com este CPF", exception.getMessage());
        verify(pessoaRepository, times(1)).existsByCpf(request.cpf());
        verify(pessoaRepository, never()).existsByUsername(anyString());
        verifyNoInteractions(pessoaMapper, passwordService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com username duplicado")
    void deveLancarExcecaoAoCriarClienteComUsernameDuplicado() {
        // Given
        ClienteRequest request = new ClienteRequest(
                "João Silva", "12345678901", "joao.silva", "senha123", "USER"
        );
        when(pessoaRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(pessoaRepository.existsByUsername(request.username())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.criar(request)
        );

        assertEquals("Já existe um cliente com este username", exception.getMessage());
        verify(pessoaRepository, times(1)).existsByCpf(request.cpf());
        verify(pessoaRepository, times(1)).existsByUsername(request.username());
        verifyNoInteractions(pessoaMapper, passwordService);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Given
        Long clienteId = 1L;
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "João Silva Atualizado", "joao.silva.novo", "novaSenha123"
        );
        Pessoa pessoa = createPessoa(clienteId, "12345678901");
        ClienteResponse responseEsperada = createClienteResponse(clienteId, request.nome());

        when(pessoaRepository.findById(clienteId)).thenReturn(pessoa);
        when(pessoaRepository.findByUsername(request.username())).thenReturn(null);
        when(passwordService.encryptPassword(request.password())).thenReturn("novaSenhaEncriptada");
        when(pessoaMapper.toClienteResponse(pessoa)).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.atualizar(clienteId, request);

        // Then
        assertNotNull(result);
        assertEquals(responseEsperada, result);
        verify(pessoaRepository, times(1)).findById(clienteId);
        verify(pessoaRepository, times(1)).findByUsername(request.username());
        verify(pessoaMapper, times(1)).updateEntityFromRequest(pessoa, request);
        verify(passwordService, times(1)).encryptPassword(request.password());
        verify(pessoaRepository, times(1)).persist(pessoa);
        verify(pessoaMapper, times(1)).toClienteResponse(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Given
        Long clienteId = 999L;
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "Nome", "username", "senha"
        );
        when(pessoaRepository.findById(clienteId)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.atualizar(clienteId, request)
        );

        assertEquals("Cliente não encontrado com ID: " + clienteId, exception.getMessage());
        verify(pessoaRepository, times(1)).findById(clienteId);
        verifyNoMoreInteractions(pessoaRepository);
        verifyNoInteractions(pessoaMapper, passwordService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com username já existente em outro cliente")
    void deveLancarExcecaoAoAtualizarComUsernameJaExistenteEmOutroCliente() {
        // Given
        Long clienteId = 1L;
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "Nome", "username.existente", null
        );
        Pessoa cliente = createPessoa(clienteId, "12345678901");
        Pessoa outroCliente = createPessoa(2L, "12345678902");

        when(pessoaRepository.findById(clienteId)).thenReturn(cliente);
        when(pessoaRepository.findByUsername(request.username())).thenReturn(outroCliente);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.atualizar(clienteId, request)
        );

        assertEquals("Já existe um cliente com este username", exception.getMessage());
        verify(pessoaRepository, times(1)).findById(clienteId);
        verify(pessoaRepository, times(1)).findByUsername(request.username());
        verifyNoMoreInteractions(pessoaRepository);
        verifyNoInteractions(pessoaMapper, passwordService);
    }

    @Test
    @DisplayName("Deve atualizar cliente sem alterar senha quando não fornecida")
    void deveAtualizarClienteSemAlterarSenhaQuandoNaoFornecida() {
        // Given
        Long clienteId = 1L;
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "João Silva Atualizado", "joao.silva.novo", null
        );
        Pessoa pessoa = createPessoa(clienteId, "12345678901");
        ClienteResponse responseEsperada = createClienteResponse(clienteId, request.nome());

        when(pessoaRepository.findById(clienteId)).thenReturn(pessoa);
        when(pessoaRepository.findByUsername(request.username())).thenReturn(null);
        when(pessoaMapper.toClienteResponse(pessoa)).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.atualizar(clienteId, request);

        // Then
        assertNotNull(result);
        verify(passwordService, never()).encryptPassword(anyString());
        verify(pessoaMapper, times(1)).updateEntityFromRequest(pessoa, request);
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        // Given
        Long clienteId = 1L;
        Pessoa pessoa = createPessoa(clienteId, "12345678901");

        when(pessoaRepository.findById(clienteId)).thenReturn(pessoa);

        // When
        assertDoesNotThrow(() -> clienteService.deletar(clienteId));

        // Then
        verify(pessoaRepository, times(1)).findById(clienteId);
        verify(pessoaRepository, times(1)).delete(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        // Given
        Long clienteId = 999L;
        when(pessoaRepository.findById(clienteId)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.deletar(clienteId)
        );

        assertEquals("Cliente não encontrado com ID: " + clienteId, exception.getMessage());
        verify(pessoaRepository, times(1)).findById(clienteId);
        verify(pessoaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpfComSucesso() {
        // Given
        String cpf = "12345678901";
        Pessoa pessoa = createPessoa(1L, cpf);
        ClienteResponse responseEsperada = createClienteResponse(1L, "João Silva");

        when(pessoaRepository.findByCpf(cpf)).thenReturn(pessoa);
        when(pessoaMapper.toClienteResponse(pessoa)).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.buscarPorCpf(cpf);

        // Then
        assertNotNull(result);
        assertEquals(responseEsperada, result);
        verify(pessoaRepository, times(1)).findByCpf(cpf);
        verify(pessoaMapper, times(1)).toClienteResponse(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por CPF inexistente")
    void deveLancarExcecaoAoBuscarClientePorCpfInexistente() {
        // Given
        String cpf = "99999999999";
        when(pessoaRepository.findByCpf(cpf)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.buscarPorCpf(cpf)
        );

        assertEquals("Cliente não encontrado com CPF: " + cpf, exception.getMessage());
        verify(pessoaRepository, times(1)).findByCpf(cpf);
        verifyNoInteractions(pessoaMapper);
    }

    @Test
    @DisplayName("Deve buscar cliente por username com sucesso")
    void deveBuscarClientePorUsernameComSucesso() {
        // Given
        String username = "joao.silva";
        Pessoa pessoa = createPessoa(1L, "12345678901");
        ClienteResponse responseEsperada = createClienteResponse(1L, "João Silva");

        when(pessoaRepository.findByUsername(username)).thenReturn(pessoa);
        when(pessoaMapper.toClienteResponse(pessoa)).thenReturn(responseEsperada);

        // When
        ClienteResponse result = clienteService.buscarPorUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(responseEsperada, result);
        verify(pessoaRepository, times(1)).findByUsername(username);
        verify(pessoaMapper, times(1)).toClienteResponse(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por username inexistente")
    void deveLancarExcecaoAoBuscarClientePorUsernameInexistente() {
        // Given
        String username = "usuario.inexistente";
        when(pessoaRepository.findByUsername(username)).thenReturn(null);

        // When & Then
        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.buscarPorUsername(username)
        );

        assertEquals("Cliente não encontrado com username: " + username, exception.getMessage());
        verify(pessoaRepository, times(1)).findByUsername(username);
        verifyNoInteractions(pessoaMapper);
    }

    // Métodos auxiliares para criar objetos de teste
    private Pessoa createPessoa(Long id, String cpf) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setCpf(cpf);
        pessoa.setNome("Nome Teste");
        pessoa.setUsername("username.teste");
        return pessoa;
    }

    private ClienteResponse createClienteResponse(Long id, String nome) {
        return new ClienteResponse(id, nome, "12345678901", "username.teste", "USER");
    }
}
