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

    
    @POST
    public Response simularInvestimento(@Valid SimulacaoRequest request) {
        
        authHelper.validarAcessoAoCliente(jwt, request.clienteId());
        

        
        
        SimulacaoResponse simulacao = simulacaoInvestimentoService.simularInvestimento(request);
        
        return Response.status(Response.Status.CREATED)
                .entity(simulacao)
                .build();
    }

    
    @GET
    @Path("/historico/{clienteId}")
    public Response buscarHistoricoSimulacoes(@PathParam("clienteId") @Positive Long clienteId) {
        
        authHelper.validarAcessoAoCliente(jwt, clienteId);
        
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoService.buscarSimulacoesPorCliente(clienteId);
        List<SimulacaoInvestimentoResponse> response = simulacoes.stream()
                .map(simulacao -> simulacaoMapper.toResponse(simulacao))
                .collect(Collectors.toList());
        
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    
    @GET
    @Path("/{id}")
    public Response buscarSimulacaoPorId(@PathParam("id") @Positive Long id) {
        SimulacaoInvestimento simulacao = simulacaoInvestimentoService.buscarSimulacaoPorId(id);
        
        if (simulacao == null) {
            throw new RuntimeException("Simulação não encontrada com ID: " + id);
        }

        

        
        authHelper.validarAcessoAoCliente(jwt, simulacao.getClienteId());
        
        SimulacaoInvestimentoResponse response = simulacaoMapper.toResponse(simulacao);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    
    @GET
    @Path("/estatisticas/{clienteId}")
    public Response buscarEstatisticasCliente(@PathParam("clienteId") @Positive Long clienteId) {
        
        authHelper.validarAcessoAoCliente(jwt, clienteId);
        
        SimulacaoInvestimentoService.EstatisticasCliente estatisticas = 
                simulacaoInvestimentoService.getEstatisticasCliente(clienteId);
        
        return Response.status(Response.Status.OK)
                .entity(estatisticas)
                .build();
    }
}


