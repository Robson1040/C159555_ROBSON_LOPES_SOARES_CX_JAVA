package br.gov.caixa.api.investimentos.mapper;

import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AcessoLogMapperTest {
    @Test
    void testToDTOFromAcessoLog() {
        AcessoLog log = new AcessoLog();
        log.setId(1L);
        log.setUsuarioId(2L);
        log.setEndpoint("/api/teste");
        log.setMetodoHttp("GET");
        log.setUriCompleta("/api/teste?param=1");
        log.setIpOrigem("127.0.0.1");
        log.setCorpoRequisicao("{}");
        log.setStatusCode(200);
        log.setCorpoResposta("{\"ok\":true}");
        log.setTempoExecucaoMs(123L);
        log.setDataAcesso(LocalDateTime.now());
        log.setUserAgent("JUnit");
        log.setErroMessage("");

        AcessoLogDTO dto = AcessoLogMapper.toDTO(log);
        assertEquals(log.getId(), dto.id());
        assertEquals(log.getUsuarioId(), dto.usuarioId());
        assertEquals(log.getEndpoint(), dto.endpoint());
        assertEquals(log.getMetodoHttp(), dto.metodoHttp());
        assertEquals(log.getUriCompleta(), dto.uriCompleta());
        assertEquals(log.getIpOrigem(), dto.ipOrigem());
        assertEquals(log.getCorpoRequisicao(), dto.corpoRequisicao());
        assertEquals(log.getStatusCode(), dto.statusCode());
        assertEquals(log.getCorpoResposta(), dto.corpoResposta());
        assertEquals(log.getTempoExecucaoMs(), dto.tempoExecucaoMs());
        assertEquals(log.getDataAcesso(), dto.dataAcesso());
        assertEquals(log.getUserAgent(), dto.userAgent());
        assertEquals(log.getErroMessage(), dto.erroMessage());
    }

    @Test
    void testToDTOList() {
        AcessoLog log1 = new AcessoLog();
        log1.setId(1L);
        log1.setUsuarioId(2L);
        log1.setEndpoint("/api/teste");
        log1.setMetodoHttp("GET");
        log1.setUriCompleta("/api/teste?param=1");
        log1.setIpOrigem("127.0.0.1");
        log1.setCorpoRequisicao("{}");
        log1.setStatusCode(200);
        log1.setCorpoResposta("{\"ok\":true}");
        log1.setTempoExecucaoMs(123L);
        log1.setDataAcesso(LocalDateTime.now());
        log1.setUserAgent("JUnit");
        log1.setErroMessage("");

        AcessoLog log2 = new AcessoLog();
        log2.setId(2L);
        log2.setUsuarioId(3L);
        log2.setEndpoint("/api/other");
        log2.setMetodoHttp("POST");
        log2.setUriCompleta("/api/other?param=2");
        log2.setIpOrigem("192.168.0.1");
        log2.setCorpoRequisicao("{data}");
        log2.setStatusCode(201);
        log2.setCorpoResposta("{\"created\":true}");
        log2.setTempoExecucaoMs(456L);
        log2.setDataAcesso(LocalDateTime.now());
        log2.setUserAgent("JUnit");
        log2.setErroMessage("Error");

        List<AcessoLog> logs = Arrays.asList(log1, log2);
        List<AcessoLogDTO> dtos = AcessoLogMapper.toDTO(logs);
        assertEquals(2, dtos.size());
        assertEquals(log1.getId(), dtos.get(0).id());
        assertEquals(log2.getId(), dtos.get(1).id());
    }

    @Test
    void testToDTOWithNullValues() {
        AcessoLog log = new AcessoLog();
        AcessoLogDTO dto = AcessoLogMapper.toDTO(log);
        assertNull(dto.id());
        assertNull(dto.usuarioId());
        assertNull(dto.endpoint());
        assertNull(dto.metodoHttp());
        assertNull(dto.uriCompleta());
        assertNull(dto.ipOrigem());
        assertNull(dto.corpoRequisicao());
        assertNull(dto.statusCode());
        assertNull(dto.corpoResposta());
        assertNull(dto.tempoExecucaoMs());
        assertNull(dto.userAgent());
        assertNull(dto.erroMessage());
    }
}
