package br.gov.caixa.api.investimentos.repository.telemetria;

import br.gov.caixa.api.investimentos.model.telemetria.AcessoLog;
import io.quarkus.panache.common.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcessoLogRepositoryTest {
    private TestAcessoLogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TestAcessoLogRepository();
    }

    @Test
    void listarTodos_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.listarTodos();
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarPorUsuario_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.buscarPorUsuario(1L);
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarPorEndpoint_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.buscarPorEndpoint("/api/teste");
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarPorPeriodo_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.buscarPorPeriodo(inicio, fim);
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarAcessosComErro_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.buscarAcessosComErro();
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarPorStatusCode_returnsList() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        List<AcessoLog> result = repository.buscarPorStatusCode(500);
        assertNotNull(result);
        assertSame(mockList, result);
    }

    @Test
    void buscarComFiltros_multipleCombinations() {
        List<AcessoLog> mockList = Collections.singletonList(new AcessoLog());
        repository.setReturnList(mockList);
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        assertSame(mockList, repository.buscarComFiltros(null, null, null, null));
        assertSame(mockList, repository.buscarComFiltros(1L, null, null, null));
        assertSame(mockList, repository.buscarComFiltros(null, "/api/teste", null, null));
        assertSame(mockList, repository.buscarComFiltros(null, null, inicio, fim));
        assertSame(mockList, repository.buscarComFiltros(1L, "/api/teste", null, null));
        assertSame(mockList, repository.buscarComFiltros(1L, null, inicio, fim));
        assertSame(mockList, repository.buscarComFiltros(null, "/api/teste", inicio, fim));
        assertSame(mockList, repository.buscarComFiltros(1L, "/api/teste", inicio, fim));
    }

    @Test
    void contarTodos_returnsCount() {
        repository.setReturnCount(10L);
        assertEquals(10L, repository.contarTodos());
    }

    @Test
    void contarPorUsuario_returnsCount() {
        repository.setReturnCount(5L);
        assertEquals(5L, repository.contarPorUsuario(1L));
    }

    @Test
    void contarPorEndpoint_returnsCount() {
        repository.setReturnCount(3L);
        assertEquals(3L, repository.contarPorEndpoint("/api/teste"));
    }

    @Test
    void contarAcessosComErro_returnsCount() {
        repository.setReturnCount(2L);
        assertEquals(2L, repository.contarAcessosComErro());
    }

    @Test
    void limparLogsAntigos_doesNotThrow() {
        assertDoesNotThrow(() -> repository.limparLogsAntigos(30));
    }

    @Test
    void limparTodosLogs_doesNotThrow() {
        assertDoesNotThrow(() -> repository.limparTodosLogs());
    }

    // Subclasse de teste que sobrescreve métodos Panache para evitar mocking de métodos finais
    private static class TestAcessoLogRepository extends AcessoLogRepository {
        private List<AcessoLog> returnList = Collections.emptyList();
        private long returnCount = 0L;

        void setReturnList(List<AcessoLog> list) { this.returnList = list; }
        void setReturnCount(long c) { this.returnCount = c; }

        @Override
        public List<AcessoLog> listAll(Sort sort) {
            return returnList;
        }

        @Override
        public List<AcessoLog> list(String query, Sort sort, Object... params) {
            return returnList;
        }

        @Override
        public long count() {
            return returnCount;
        }

        @Override
        public long count(String query, Object... params) {
            return returnCount;
        }

        @Override
        public long delete(String query, Object... params) {
            // noop
            return 0L;
        }

        @Override
        public long deleteAll() {
            // noop
            return 0L;
        }
    }
}
