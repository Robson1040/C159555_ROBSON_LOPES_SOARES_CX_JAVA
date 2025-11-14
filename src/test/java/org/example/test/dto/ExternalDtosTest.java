package org.example.test.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AgrupamentoProdutoDataDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para os DTOs externos
 */
@DisplayName("Testes dos DTOs externos")
public class ExternalDtosTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("SimulacaoResponseDTO deve funcionar como record")
    public void testSimulacaoResponseDTO() {
        // Arrange
        Long id = 1L;
        Long clienteId = 12345L;
        String produto = "CDB Teste";
        BigDecimal valorInvestido = new BigDecimal("10000.00");
        BigDecimal valorFinal = new BigDecimal("11000.00");
        Integer prazoMeses = 12;
        Integer prazoDias = null;
        Integer prazoAnos = null;
        LocalDateTime dataSimulacao = LocalDateTime.now();

        // Act
        SimulacaoResponseDTO dto = new SimulacaoResponseDTO(
                id, clienteId, produto, valorInvestido, valorFinal,
                prazoMeses, prazoDias, prazoAnos, dataSimulacao
        );

        // Assert
        assertEquals(id, dto.id());
        assertEquals(clienteId, dto.clienteId());
        assertEquals(produto, dto.produto());
        assertEquals(valorInvestido, dto.valorInvestido());
        assertEquals(valorFinal, dto.valorFinal());
        assertEquals(prazoMeses, dto.prazoMeses());
        assertEquals(prazoDias, dto.prazoDias());
        assertEquals(prazoAnos, dto.prazoAnos());
        assertEquals(dataSimulacao, dto.dataSimulacao());

        // Verifica toString
        assertTrue(dto.toString().contains("CDB Teste"));
        assertTrue(dto.toString().contains("10000.00"));

        // Verifica equals e hashCode
        SimulacaoResponseDTO dto2 = new SimulacaoResponseDTO(
                id, clienteId, produto, valorInvestido, valorFinal,
                prazoMeses, prazoDias, prazoAnos, dataSimulacao
        );
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("AgrupamentoProdutoDataDTO deve funcionar como record")
    public void testAgrupamentoProdutoDataDTO() {
        // Arrange
        String produto = "CDB Teste";
        LocalDate data = LocalDate.now();
        Long quantidadeSimulacoes = 5L;
        BigDecimal mediaValorInvestido = new BigDecimal("15000.00");
        BigDecimal mediaValorFinal = new BigDecimal("16500.00");

        // Act
        AgrupamentoProdutoDataDTO dto = new AgrupamentoProdutoDataDTO(
                produto, data, quantidadeSimulacoes, mediaValorInvestido, mediaValorFinal
        );

        // Assert
        assertEquals(produto, dto.produto());
        assertEquals(data, dto.data());
        assertEquals(quantidadeSimulacoes, dto.quantidadeSimulacoes());
        assertEquals(mediaValorInvestido, dto.mediaValorInvestido());
        assertEquals(mediaValorFinal, dto.mediaValorFinal());

        // Verifica toString
        assertTrue(dto.toString().contains("CDB Teste"));
        assertTrue(dto.toString().contains("5"));

        // Verifica equals e hashCode
        AgrupamentoProdutoDataDTO dto2 = new AgrupamentoProdutoDataDTO(
                produto, data, quantidadeSimulacoes, mediaValorInvestido, mediaValorFinal
        );
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("SimulacaoResponseDTO deve ser serializável para JSON")
    public void testSimulacaoResponseDTOJsonSerialization() throws Exception {
        // Arrange
        SimulacaoResponseDTO dto = new SimulacaoResponseDTO(
                1L, 12345L, "CDB Teste", 
                new BigDecimal("10000.00"), new BigDecimal("11000.00"),
                12, null, null, LocalDateTime.of(2025, 11, 14, 10, 30)
        );

        // Act
        String json = mapper.writeValueAsString(dto);
        SimulacaoResponseDTO deserialized = mapper.readValue(json, SimulacaoResponseDTO.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"clienteId\":12345"));
        assertTrue(json.contains("\"produto\":\"CDB Teste\""));
        assertTrue(json.contains("\"valorInvestido\":10000.00"));
        assertTrue(json.contains("\"valorFinal\":11000.00"));
        assertTrue(json.contains("\"prazoMeses\":12"));

        assertEquals(dto.id(), deserialized.id());
        assertEquals(dto.clienteId(), deserialized.clienteId());
        assertEquals(dto.produto(), deserialized.produto());
        assertEquals(0, dto.valorInvestido().compareTo(deserialized.valorInvestido()));
        assertEquals(0, dto.valorFinal().compareTo(deserialized.valorFinal()));
    }

    @Test
    @DisplayName("AgrupamentoProdutoDataDTO deve ser serializável para JSON")
    public void testAgrupamentoProdutoDataDTOJsonSerialization() throws Exception {
        // Arrange
        AgrupamentoProdutoDataDTO dto = new AgrupamentoProdutoDataDTO(
                "CDB Teste", LocalDate.of(2025, 11, 14), 5L,
                new BigDecimal("15000.00"), new BigDecimal("16500.00")
        );

        // Act
        String json = mapper.writeValueAsString(dto);
        AgrupamentoProdutoDataDTO deserialized = mapper.readValue(json, AgrupamentoProdutoDataDTO.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"produto\":\"CDB Teste\""));
        assertTrue(json.contains("\"data\":[2025,11,14]")); // LocalDate é serializado como array
        assertTrue(json.contains("\"quantidadeSimulacoes\":5"));
        assertTrue(json.contains("\"mediaValorInvestido\":15000.00"));
        assertTrue(json.contains("\"mediaValorFinal\":16500.00"));

        assertEquals(dto.produto(), deserialized.produto());
        assertEquals(dto.data(), deserialized.data());
        assertEquals(dto.quantidadeSimulacoes(), deserialized.quantidadeSimulacoes());
        assertEquals(0, dto.mediaValorInvestido().compareTo(deserialized.mediaValorInvestido()));
        assertEquals(0, dto.mediaValorFinal().compareTo(deserialized.mediaValorFinal()));
    }
}