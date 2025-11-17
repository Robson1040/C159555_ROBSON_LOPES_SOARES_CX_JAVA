package br.gov.caixa.api.investimentos.resource.simulacao;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoRequest;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoResponse;
import br.gov.caixa.api.investimentos.dto.simulacao.SimulacaoInvestimentoResponse;
import br.gov.caixa.api.investimentos.enums.produto.TipoRentabilidade;
import br.gov.caixa.api.investimentos.enums.simulacao.Indice;

import br.gov.caixa.api.investimentos.model.simulacao.SimulacaoInvestimento;
import java.util.stream.Collectors;
import br.gov.caixa.api.investimentos.service.simulacao.SimulacaoInvestimentoService;
import br.gov.caixa.api.investimentos.helper.auth.JwtAuthorizationHelper;
import br.gov.caixa.api.investimentos.mapper.SimulacaoInvestimentoMapper;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

/**
 * Resource para simulação de investimentos
 */
@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class SimulacaoInvestimentoResource {

    @Inject
    SimulacaoInvestimentoService simulacaoInvestimentoService;

    @Inject
    JsonWebToken jwt;

    @Inject
    JwtAuthorizationHelper authHelper;

    @Inject
    SimulacaoInvestimentoMapper simulacaoMapper;

    /**
     * POST /simular-investimento
     * Simula um investimento baseado nos critérios informados
     * 
     * Validações aplicadas:
     * - clienteId: obrigatório e positivo
     * - valor: obrigatório, entre R$ 1,00 e R$ 999.999.999,99
     * - prazo: pelo menos um dos três deve ser informado (meses, dias ou anos)
     * - liquidez: se informado, entre 0 e 3650 dias
     */
    @POST
    public Response simularInvestimento(@Valid SimulacaoRequest request) {
        // Verificar se o usuário pode criar simulação para o cliente especificado
        authHelper.validarAcessoAoCliente(jwt, request.clienteId());
        

        
        // O service pode lançar RuntimeException que será tratada pelo handler global
        SimulacaoResponse simulacao = simulacaoInvestimentoService.simularInvestimento(request);
        
        return Response.status(Response.Status.CREATED)
                .entity(simulacao)
                .build();
    }

    /**
     * GET /simular-investimento/historico/{clienteId}
     * Busca o histórico de simulações de um cliente
     */
    @GET
    @Path("/historico/{clienteId}")
    public Response buscarHistoricoSimulacoes(@PathParam("clienteId") @Positive Long clienteId) {
        // Verificar autorização baseada no JWT
        authHelper.validarAcessoAoCliente(jwt, clienteId);
        
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.buscarSimulacoesPorCliente(clienteId);
        List<SimulacaoInvestimentoResponse> response = simulacoes.stream()
                .map(simulacao -> simulacaoMapper.toResponse(simulacao))
                .collect(Collectors.toList());
        
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    /**
     * GET /simular-investimento/{id}
     * Busca uma simulação específica por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarSimulacaoPorId(@PathParam("id") @Positive Long id) {
        SimulacaoInvestimento simulacao = simulacaoInvestimentoService.buscarSimulacaoPorId(id);
        
        if (simulacao == null) {
            throw new RuntimeException("Simulação não encontrada com ID: " + id);
        }

        

        // Verificar se o usuário pode acessar esta simulação
        authHelper.validarAcessoAoCliente(jwt, simulacao.getClienteId());
        
        SimulacaoInvestimentoResponse response = simulacaoMapper.toResponse(simulacao);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    /**
     * GET /simular-investimento/estatisticas/{clienteId}
     * Busca estatísticas das simulações de um cliente
     */
    @GET
    @Path("/estatisticas/{clienteId}")
    public Response buscarEstatisticasCliente(@PathParam("clienteId") @Positive Long clienteId) {
        // Verificar autorização baseada no JWT
        authHelper.validarAcessoAoCliente(jwt, clienteId);
        
        SimulacaoInvestimentoService.EstatisticasCliente estatisticas = 
                simulacaoInvestimentoService.getEstatisticasCliente(clienteId);
        
        return Response.status(Response.Status.OK)
                .entity(estatisticas)
                .build();
    }
}


