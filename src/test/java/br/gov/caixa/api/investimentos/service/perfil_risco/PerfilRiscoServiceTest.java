package br.gov.caixa.api.investimentos.service.perfil_risco;

import br.gov.caixa.api.investimentos.dto.perfil_risco.PerfilRiscoResponse;
import br.gov.caixa.api.investimentos.enums.produto.NivelRisco;
import br.gov.caixa.api.investimentos.enums.produto.TipoProduto;
import br.gov.caixa.api.investimentos.model.produto.Produto;
import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import br.gov.caixa.api.investimentos.repository.produto.IProdutoRepository;
import br.gov.caixa.api.investimentos.repository.simulacao.ISimulacaoInvestimentoRepository;
import br.gov.caixa.api.investimentos.service.cliente.ClienteService;
import br.gov.caixa.api.investimentos.repository.investimento.IInvestimentoRepository;
import br.gov.caixa.api.investimentos.ml.GeradorRecomendacaoML;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilRiscoServiceTest {

    @InjectMocks
    PerfilRiscoService perfilRiscoService;

    @Mock
    ClienteService clienteService;

    @Mock
    IInvestimentoRepository investimentoRepository;

    @Mock
    ISimulacaoInvestimentoRepository simulacaoRepository;

    @Mock
    IProdutoRepository produtoRepository;

    @Mock
    GeradorRecomendacaoML geradorRecomendacaoML;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calcularPerfilRisco_clienteNaoExiste_throwsException() {
        Long clienteId = 1L;
        doThrow(new RuntimeException("Cliente não encontrado")).when(clienteService).buscarPorId(clienteId);

        Exception e = assertThrows(RuntimeException.class, () -> perfilRiscoService.calcularPerfilRisco(clienteId));
        assertEquals("Cliente não encontrado", e.getMessage());
    }

    @Test
    void calcularPerfilRisco_semInvestimentosMasComSimulacoes_retornaPerfil() {
        Long clienteId = 1L;

        when(clienteService.buscarPorId(clienteId)).thenReturn(null); // Apenas não lança exceção
        when(produtoRepository.listAll()).thenReturn(List.of(
                criarProduto("Produto A", NivelRisco.BAIXO, 10)
        ));
        when(investimentoRepository.findByClienteId(clienteId)).thenReturn(List.of());
        when(simulacaoRepository.findByClienteId(clienteId)).thenReturn(List.of(
                mock(SimulacaoInvestimento.class)
        ));
        when(geradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao(anyList(), anyList()))
                .thenReturn(List.of(criarProduto("Produto A", NivelRisco.BAIXO, 100)));

        PerfilRiscoResponse response = perfilRiscoService.calcularPerfilRisco(clienteId);
        assertNotNull(response);
        assertEquals("CONSERVADOR", response.perfil());
        assertEquals(100, response.pontuacao());
    }

    private Produto criarProduto(String nome, NivelRisco risco, int pontuacao) {
        Produto produto = new Produto();
        produto.setTipo(TipoProduto.POUPANCA);
        produto.setFgc(true);
        produto.setPontuacao(pontuacao);
        return produto;
    }
}