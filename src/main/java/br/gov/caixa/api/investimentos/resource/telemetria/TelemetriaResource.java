package br.gov.caixa.api.investimentos.resource.telemetria;

import br.gov.caixa.api.investimentos.dto.telemetria.EstatisticasAcessoDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import br.gov.caixa.api.investimentos.dto.telemetria.TelemetriaResponse;
import br.gov.caixa.api.investimentos.dto.telemetria.AcessoLogDTO;
import br.gov.caixa.api.investimentos.service.telemetria.TelemetriaService;
import br.gov.caixa.api.investimentos.service.telemetria.AcessoLogService;
import br.gov.caixa.api.investimentos.repository.telemetria.TelemetriaMetricaRepository;
import br.gov.caixa.api.investimentos.model.telemetria.TelemetriaMetrica;
import java.time.LocalDateTime;
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

    // ==================== ENDPOINTS DE LOGS DE ACESSO ====================

    /**
     * GET /telemetria/acesso-logs
     * Lista todos os logs de acesso aos endpoints
     */
    @GET
    @Path("/acesso-logs")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * GET /telemetria/acesso-logs/usuario/{usuarioId}
     * Lista logs de acesso de um usuário específico
     */
    @GET
    @Path("/acesso-logs/usuario/{usuarioId}")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * GET /telemetria/acesso-logs/endpoint/{endpoint}
     * Lista logs de acesso a um endpoint específico
     */
    @GET
    @Path("/acesso-logs/endpoint/{endpoint}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarLogsAcessoPorEndpoint(@PathParam("endpoint") String endpoint) {
        try {
            List<AcessoLogDTO> logs = acessoLogService.buscarLogsPorEndpoint(endpoint);
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar logs do endpoint: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /telemetria/acesso-logs/erros
     * Lista logs de acesso com erro (status >= 400)
     */
    @GET
    @Path("/acesso-logs/erros")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * GET /telemetria/acesso-logs/status/{statusCode}
     * Lista logs por código de status HTTP
     */
    @GET
    @Path("/acesso-logs/status/{statusCode}")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * GET /telemetria/acesso-logs/estatisticas
     * Retorna estatísticas dos logs de acesso
     */
    @GET
    @Path("/acesso-logs/estatisticas")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * DELETE /telemetria/acesso-logs
     * Limpa todos os logs de acesso
     */
    @DELETE
    @Path("/acesso-logs")
    @Produces(MediaType.APPLICATION_JSON)
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

    /**
     * DELETE /telemetria/acesso-logs/antigos/{diasRetencao}
     * Limpa logs com mais de N dias
     */
    @DELETE
    @Path("/acesso-logs/antigos/{diasRetencao}")
    @Produces(MediaType.APPLICATION_JSON)
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