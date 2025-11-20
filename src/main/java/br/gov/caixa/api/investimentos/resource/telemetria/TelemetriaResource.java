package br.gov.caixa.api.investimentos.resource.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.dto.telemetria.EstatisticasAcessoDTO;
import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
import br.gov.caixa.api.investimentos.service.telemetria.AcessoLogService;
import br.gov.caixa.api.investimentos.service.telemetria.TelemetriaService;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/telemetria")
@RolesAllowed({"ADMIN"})
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @Inject
    TelemetriaMetricaRepository telemetriaRepository;

    @Inject
    AcessoLogService acessoLogService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "telemetria")
    public Response obterTelemetria() {
        try {
            TelemetriaResponse telemetria = telemetriaService.obterTelemetria();
            return Response.ok(telemetria).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao obter telemetria: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/detalhado")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "telemetria-detalhado")
    public Response obterTelemetriaDetalhada() {
        try {
            List<TelemetriaMetrica> metricas = telemetriaRepository.listAll();
            return Response.ok(metricas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao obter telemetria detalhada: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/mais-acessados/{limite}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "telemetria-mais-acessados")
    public Response obterMaisAcessados(@PathParam("limite") int limite) {
        try {
            List<TelemetriaMetrica> metricas = telemetriaRepository.listarMaisAcessadas(limite);
            return Response.ok(metricas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao obter endpoints mais acessados: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @CacheInvalidateAll(cacheName = "telemetria")
    @CacheInvalidateAll(cacheName = "telemetria-detalhado")
    @CacheInvalidateAll(cacheName = "telemetria-mais-acessados")
    public Response limparMetricas() {
        try {
            telemetriaRepository.limparTodasMetricas();

            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao limpar métricas: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/acesso-logs")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "acesso-logs")
    public Response listarLogsAcesso() {
        try {
            List<AcessoLogDTO> logs = acessoLogService.listarTodosLogs();
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar logs de acesso: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/acesso-logs/usuario/{usuarioId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "acesso-logs-usuario")
    public Response listarLogsAcessoPorUsuario(@PathParam("usuarioId") Long usuarioId) {
        try {
            List<AcessoLogDTO> logs = acessoLogService.buscarLogsPorUsuario(usuarioId);
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar logs do usuário: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/acesso-logs/erros")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "acesso-logs-erros")
    public Response listarLogsAcessoComErro() {
        try {
            List<AcessoLogDTO> logs = acessoLogService.buscarLogsComErro();
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar logs com erro: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/acesso-logs/status/{statusCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "acesso-logs-status")
    public Response listarLogsAcessoPorStatusCode(@PathParam("statusCode") Integer statusCode) {
        try {
            List<AcessoLogDTO> logs = acessoLogService.buscarLogsPorStatusCode(statusCode);
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar logs por status code: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/acesso-logs/estatisticas")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "acesso-logs-estatisticas")
    public Response obterEstatisticasAcessoLogs() {
        try {
            EstatisticasAcessoDTO stats = acessoLogService.obterEstatisticas();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao obter estatísticas de acesso: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/acesso-logs")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheInvalidateAll(cacheName = "acesso-logs")
    @CacheInvalidateAll(cacheName = "acesso-logs-usuario")
    @CacheInvalidateAll(cacheName = "acesso-logs-erros")
    @CacheInvalidateAll(cacheName = "acesso-logs-status")
    @CacheInvalidateAll(cacheName = "acesso-logs-estatisticas")
    public Response limparLogsAcesso() {
        try {
            acessoLogService.limparTodosLogs();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao limpar logs de acesso: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/acesso-logs/antigos/{diasRetencao}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheInvalidateAll(cacheName = "acesso-logs")
    @CacheInvalidateAll(cacheName = "acesso-logs-usuario")
    @CacheInvalidateAll(cacheName = "acesso-logs-erros")
    @CacheInvalidateAll(cacheName = "acesso-logs-status")
    @CacheInvalidateAll(cacheName = "acesso-logs-estatisticas")
    public Response limparLogsAcessoAntigos(@PathParam("diasRetencao") int diasRetencao) {
        try {
            acessoLogService.limparLogsAntigos(diasRetencao);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao limpar logs antigos: " + e.getMessage())
                    .build();
        }
    }
}