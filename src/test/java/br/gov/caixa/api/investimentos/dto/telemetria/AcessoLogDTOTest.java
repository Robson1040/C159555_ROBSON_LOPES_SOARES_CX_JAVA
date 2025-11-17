package br.gov.caixa.api.investimentos.dto.telemetria;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AcessoLogDTOTest {
    @Test
    void testRecordFields() {
        Long id = 1L;
        Long usuarioId = 2L;
        String endpoint = "/api/teste";
        String metodoHttp = "GET";
        String uriCompleta = "/api/teste?param=1";
        String ipOrigem = "127.0.0.1";
        String corpoRequisicao = "{}";
        Integer statusCode = 200;
        String corpoResposta = "{\"ok\":true}";
        Long tempoExecucaoMs = 123L;
        LocalDateTime dataAcesso = LocalDateTime.now();
        String userAgent = "JUnit";
        String erroMessage = "";

        AcessoLogDTO dto = new AcessoLogDTO(id, usuarioId,  endpoint, metodoHttp, uriCompleta, ipOrigem,
                corpoRequisicao, statusCode, corpoResposta, tempoExecucaoMs, dataAcesso, userAgent, erroMessage);

        assertEquals(id, dto.id());
        assertEquals(usuarioId, dto.usuarioId());
        assertEquals(endpoint, dto.endpoint());
        assertEquals(metodoHttp, dto.metodoHttp());
        assertEquals(uriCompleta, dto.uriCompleta());
        assertEquals(ipOrigem, dto.ipOrigem());
        assertEquals(corpoRequisicao, dto.corpoRequisicao());
        assertEquals(statusCode, dto.statusCode());
        assertEquals(corpoResposta, dto.corpoResposta());
        assertEquals(tempoExecucaoMs, dto.tempoExecucaoMs());
        assertEquals(dataAcesso, dto.dataAcesso());
        assertEquals(userAgent, dto.userAgent());
        assertEquals(erroMessage, dto.erroMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        AcessoLogDTO dto1 = new AcessoLogDTO(1L, 2L, "/api/teste", "GET", "/api/teste?param=1", "127.0.0.1",
                "{}", 200, "{\"ok\":true}", 123L, now, "JUnit", "");
        AcessoLogDTO dto2 = new AcessoLogDTO(1L, 2L,  "/api/teste", "GET", "/api/teste?param=1", "127.0.0.1",
                "{}", 200, "{\"ok\":true}", 123L, now, "JUnit", "");
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        AcessoLogDTO dto = new AcessoLogDTO(1L, 2L,  "/api/teste", "GET", "/api/teste?param=1", "127.0.0.1",
                "{}", 200, "{\"ok\":true}", 123L, now, "JUnit", "");
        String str = dto.toString();
        assertTrue(str.contains("AcessoLogDTO"));
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("usuarioId=2"));
    }
}
