package br.gov.caixa.api.investimentos.resource.simulacao;

import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoDataDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoMesDTO;
import br.gov.caixa.api.investimentos.dto.simulacao.AgrupamentoProdutoAnoDTO;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoResourceTest {

    private SimulacaoResource resource;
    private MockedStatic<SimulacaoInvestimento> simulacaoStaticMock;

    @BeforeEach
    void setUp() {
        resource = new SimulacaoResource();

        simulacaoStaticMock = mockStatic(SimulacaoInvestimento.class);

        // Mockando listAll() com construtor completo
        simulacaoStaticMock.when(SimulacaoInvestimento::listAll).thenReturn(List.of(
                new SimulacaoInvestimento(
                        10L, 100L, "ProdutoA", new BigDecimal("1000"), new BigDecimal("1100"),
                        12, 0, 1,
                        new BigDecimal("0.10"), new BigDecimal("100"), true, "Cenário 1"
                ),
                new SimulacaoInvestimento(
                        11L, 100L, "ProdutoA", new BigDecimal("2000"), new BigDecimal("2200"),
                        12, 0, 1,
                        new BigDecimal("0.10"), new BigDecimal("200"), true, "Cenário 2"
                ),
                new SimulacaoInvestimento(
                        12L, 101L, "ProdutoB", new BigDecimal("1500"), new BigDecimal("1650"),
                        24, 0, 2,
                        new BigDecimal("0.10"), new BigDecimal("150"), true, "Cenário 3"
                )
        ));
    }

    @AfterEach
    void tearDown() {
        simulacaoStaticMock.close();
    }

    @Test
    void testListarTodasSimulacoes() {
        var response = resource.listarTodasSimulacoes();
        assertEquals(200, response.getStatus());
        var body = response.getEntity();
        assertNotNull(body);
        var list = (List<?>) body;
        assertEquals(3, list.size());
    }

    @Test
    void testAgruparPorProdutoEDia() {
        var response = resource.agruparPorProdutoEDia();
        assertEquals(200, response.getStatus());
        var body = (List<AgrupamentoProdutoDataDTO>) response.getEntity();
        assertEquals(3, body.size());
        assertTrue(body.stream().anyMatch(dto -> dto.produto().equals("ProdutoA")));
    }

    @Test
    void testAgruparPorProdutoEAnoMes() {
        var response = resource.agruparPorProdutoEAnoMes();
        assertEquals(200, response.getStatus());
        var body = (List<AgrupamentoProdutoMesDTO>) response.getEntity();
        assertEquals(2, body.size()); // ProdutoA (Jan 2025), ProdutoB (mes diferente simulado)
        var produtoA = body.stream().filter(dto -> dto.produto().equals("ProdutoA")).findFirst().orElseThrow();
        assertEquals(YearMonth.from(produtoA.mes()), produtoA.mes());
        assertEquals(new BigDecimal("1500.00"), produtoA.mediaValorInvestido());
    }

    @Test
    void testAgruparPorProdutoEAno() {
        var response = resource.agruparPorProdutoEAno();
        assertEquals(200, response.getStatus());
        var body = (List<AgrupamentoProdutoAnoDTO>) response.getEntity();
        assertEquals(2, body.size());
        var produtoA = body.stream().filter(dto -> dto.produto().equals("ProdutoA")).findFirst().orElseThrow();
        assertEquals(Year.of(produtoA.ano().getValue()), produtoA.ano());
    }
}