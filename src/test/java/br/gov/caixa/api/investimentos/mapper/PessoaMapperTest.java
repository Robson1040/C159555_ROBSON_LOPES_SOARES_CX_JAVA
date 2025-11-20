package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.cliente.ClienteRequest;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteResponse;
import br.gov.caixa.api.investimentos.dto.cliente.ClienteUpdateRequest;
import br.gov.caixa.api.investimentos.model.cliente.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para PessoaMapper")
class PessoaMapperTest {

    private PessoaMapper pessoaMapper;

    @BeforeEach
    void setUp() {
        pessoaMapper = new PessoaMapper();
    }

    @Test
    @DisplayName("Deve converter Pessoa para ClienteResponse corretamente")
    void deveConverterPessoaParaClienteResponseCorretamente() {
        // Given
        Pessoa pessoa = createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER");

        // When
        ClienteResponse response = pessoaMapper.toClienteResponse(pessoa);

        // Then
        assertNotNull(response);
        assertEquals(pessoa.getId(), response.id());
        assertEquals(pessoa.getNome(), response.nome());
        assertEquals(pessoa.getCpf(), response.cpf());
        assertEquals(pessoa.getUsername(), response.username());
        assertEquals(pessoa.getRole(), response.role());
    }

    @Test
    @DisplayName("Deve retornar null quando Pessoa é null")
    void deveRetornarNullQuandoPessoaENull() {
        // When
        ClienteResponse response = pessoaMapper.toClienteResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    @DisplayName("Deve converter lista de Pessoa para lista de ClienteResponse")
    void deveConverterListaDePessoaParaListaDeClienteResponse() {
        // Given
        List<Pessoa> pessoas = Arrays.asList(
                createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER"),
                createPessoa(2L, "Maria Santos", "12345678902", "maria.santos", "ADMIN")
        );

        // When
        List<ClienteResponse> responses = pessoaMapper.toClienteResponseList(pessoas);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        ClienteResponse firstResponse = responses.get(0);
        assertEquals(1L, firstResponse.id());
        assertEquals("João Silva", firstResponse.nome());
        assertEquals("USER", firstResponse.role());

        ClienteResponse secondResponse = responses.get(1);
        assertEquals(2L, secondResponse.id());
        assertEquals("Maria Santos", secondResponse.nome());
        assertEquals("ADMIN", secondResponse.role());
    }

    @Test
    @DisplayName("Deve retornar null quando lista de Pessoa é null")
    void deveRetornarNullQuandoListaDePessoaENull() {
        // When
        List<ClienteResponse> responses = pessoaMapper.toClienteResponseList(null);

        // Then
        assertNull(responses);
    }

    @Test
    @DisplayName("Deve filtrar elementos null da lista de Pessoa")
    void deveFiltrarElementosNullDaListaDePessoa() {
        // Given
        List<Pessoa> pessoas = Arrays.asList(
                createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER"),
                null,
                createPessoa(2L, "Maria Santos", "12345678902", "maria.santos", "ADMIN")
        );

        // When
        List<ClienteResponse> responses = pessoaMapper.toClienteResponseList(pessoas);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size()); // Deve ignorar o elemento null
    }

    @Test
    @DisplayName("Deve converter ClienteRequest para entidade Pessoa")
    void deveConverterClienteRequestParaEntidadePessoa() {
        // Given
        ClienteRequest request = new ClienteRequest(
                "João Silva", "12345678901", "joao.silva", "senha123", "USER"
        );

        // When
        Pessoa pessoa = pessoaMapper.toEntity(request);

        // Then
        assertNotNull(pessoa);
        assertEquals(request.nome(), pessoa.getNome());
        assertEquals(request.cpf(), pessoa.getCpf());
        assertEquals(request.username(), pessoa.getUsername());
        assertEquals(request.password(), pessoa.getPassword());
        assertEquals(request.role(), pessoa.getRole());
        assertNull(pessoa.getId()); // ID deve ser null para nova entidade
    }

    @Test
    @DisplayName("Deve retornar null quando ClienteRequest é null")
    void deveRetornarNullQuandoClienteRequestENull() {
        // When
        Pessoa pessoa = pessoaMapper.toEntity(null);

        // Then
        assertNull(pessoa);
    }

    @Test
    @DisplayName("Deve atualizar entidade Pessoa com dados do ClienteUpdateRequest")
    void deveAtualizarEntidadePessoaComDadosDoClienteUpdateRequest() {
        // Given
        Pessoa pessoa = createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER");
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "João Silva Atualizado", "joao.silva.novo", "novaSenha"
        );

        // When
        pessoaMapper.updateEntityFromRequest(pessoa, request);

        // Then
        assertEquals(request.nome(), pessoa.getNome());
        assertEquals(request.username(), pessoa.getUsername());
        assertEquals(request.password(), pessoa.getPassword());
        // Campos não alterados devem permanecer iguais
        assertEquals(1L, pessoa.getId());
        assertEquals("12345678901", pessoa.getCpf());
        assertEquals("USER", pessoa.getRole());
    }

    @Test
    @DisplayName("Deve atualizar apenas campos não nulos do ClienteUpdateRequest")
    void deveAtualizarApenasCamposNaoNulosDoClienteUpdateRequest() {
        // Given
        Pessoa pessoa = createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER");
        String nomeOriginal = pessoa.getNome();
        String usernameOriginal = pessoa.getUsername();
        String passwordOriginal = pessoa.getPassword();

        ClienteUpdateRequest requestParcial = new ClienteUpdateRequest(
                "Nome Atualizado", null, null
        );

        // When
        pessoaMapper.updateEntityFromRequest(pessoa, requestParcial);

        // Then
        assertEquals("Nome Atualizado", pessoa.getNome()); // Atualizado
        assertEquals(usernameOriginal, pessoa.getUsername()); // Não alterado
        assertEquals(passwordOriginal, pessoa.getPassword()); // Não alterado
    }

    @Test
    @DisplayName("Deve ignorar quando Pessoa é null na atualização")
    void deveIgnorarQuandoPessoaENull() {
        // Given
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "Nome", "username", "senha"
        );

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> pessoaMapper.updateEntityFromRequest(null, request));
    }

    @Test
    @DisplayName("Deve ignorar quando ClienteUpdateRequest é null na atualização")
    void deveIgnorarQuandoClienteUpdateRequestENull() {
        // Given
        Pessoa pessoa = createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER");
        String nomeOriginal = pessoa.getNome();

        // When
        pessoaMapper.updateEntityFromRequest(pessoa, null);

        // Then - Nenhum campo deve ser alterado
        assertEquals(nomeOriginal, pessoa.getNome());
    }

    @Test
    @DisplayName("Deve ignorar quando ambos Pessoa e ClienteUpdateRequest são null")
    void deveIgnorarQuandoAmbosSaoNull() {
        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> pessoaMapper.updateEntityFromRequest(null, null));
    }

    @Test
    @DisplayName("Deve processar lista vazia corretamente")
    void deveProcessarListaVaziaCorretamente() {
        // Given
        List<Pessoa> pessoasVazia = Arrays.asList();

        // When
        List<ClienteResponse> responses = pessoaMapper.toClienteResponseList(pessoasVazia);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar campos vazios mas não nulos no ClienteUpdateRequest")
    void deveAtualizarCamposVaziosMasNaoNulosNoClienteUpdateRequest() {
        // Given
        Pessoa pessoa = createPessoa(1L, "João Silva", "12345678901", "joao.silva", "USER");
        ClienteUpdateRequest request = new ClienteUpdateRequest(
                "", "novo.username", "novaSenha"
        );

        // When
        pessoaMapper.updateEntityFromRequest(pessoa, request);

        // Then
        assertEquals("", pessoa.getNome()); // String vazia deve ser aplicada
        assertEquals("novo.username", pessoa.getUsername());
        assertEquals("novaSenha", pessoa.getPassword());
    }

    // Método auxiliar para criar objetos Pessoa para teste
    private Pessoa createPessoa(Long id, String nome, String cpf, String username, String role) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);
        pessoa.setUsername(username);
        pessoa.setPassword("senhaOriginal");
        pessoa.setRole(role);
        return pessoa;
    }
}