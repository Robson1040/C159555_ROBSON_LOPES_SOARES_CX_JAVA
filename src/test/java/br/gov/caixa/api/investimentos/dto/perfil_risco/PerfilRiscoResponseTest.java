package br.gov.caixa.api.investimentos.dto.perfil_risco;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PerfilRiscoResponse - Testes unitários para métodos estáticos")
class PerfilRiscoResponseTest {

    @Test
    @DisplayName("Deve criar resposta para perfil conservador corretamente")
    void devecriarResparaPerfilConservadorCorretamente() {
        // Given
        Long clienteId = 123L;
        Integer pontuacao = 25;

        // When
        PerfilRiscoResponse response = PerfilRiscoResponse.conservador(clienteId, pontuacao);

        // Then
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals("CONSERVADOR", response.perfil());
        assertEquals(pontuacao, response.pontuacao());
        assertEquals("Perfil focado em segurança e liquidez, com baixa tolerância ao risco.", response.descricao());
    }

    @Test
    @DisplayName("Deve criar resposta para perfil moderado corretamente")
    void deveCriarRespostaParaPerfilModeradoCorretamente() {
        // Given
        Long clienteId = 456L;
        Integer pontuacao = 55;

        // When
        PerfilRiscoResponse response = PerfilRiscoResponse.moderado(clienteId, pontuacao);

        // Then
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals("MODERADO", response.perfil());
        assertEquals(pontuacao, response.pontuacao());
        assertEquals("Perfil equilibrado entre segurança e rentabilidade.", response.descricao());
    }

    @Test
    @DisplayName("Deve criar resposta para perfil agressivo corretamente")
    void deveCriarRespostaParaPerfilAgressivoCorretamente() {
        // Given
        Long clienteId = 789L;
        Integer pontuacao = 85;

        // When
        PerfilRiscoResponse response = PerfilRiscoResponse.agressivo(clienteId, pontuacao);

        // Then
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals("AGRESSIVO", response.perfil());
        assertEquals(pontuacao, response.pontuacao());
        assertEquals("Perfil voltado para alta rentabilidade, com maior tolerância ao risco.", response.descricao());
    }

    @Test
    @DisplayName("Deve criar resposta usando construtor direto")
    void deveCriarRespostaUsandoConstrutorDireto() {
        // Given
        Long clienteId = 999L;
        String perfil = "CUSTOMIZADO";
        Integer pontuacao = 45;
        String descricao = "Perfil customizado para teste";

        // When
        PerfilRiscoResponse response = new PerfilRiscoResponse(clienteId, perfil, pontuacao, descricao);

        // Then
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals(perfil, response.perfil());
        assertEquals(pontuacao, response.pontuacao());
        assertEquals(descricao, response.descricao());
    }

    @Test
    @DisplayName("Deve funcionar com valores nulos")
    void deveFuncionarComValoresNulos() {
        // Given
        Long clienteId = null;
        Integer pontuacao = null;

        // When
        PerfilRiscoResponse response = PerfilRiscoResponse.conservador(clienteId, pontuacao);

        // Then
        assertNotNull(response);
        assertNull(response.clienteId());
        assertNull(response.pontuacao());
        assertEquals("CONSERVADOR", response.perfil());
        assertNotNull(response.descricao());
    }
}