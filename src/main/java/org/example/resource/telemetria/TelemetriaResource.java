package org.example.resource.telemetria;

import org.example.dto.telemetria.TelemetriaResponse;
import org.example.service.telemetria.TelemetriaService;
import org.example.repository.telemetria.TelemetriaMetricaRepository;
import org.example.model.telemetria.TelemetriaMetrica;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/telemetria")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;
    
    @Inject
    TelemetriaMetricaRepository telemetriaRepository;

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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao obter endpoints mais acessados: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/limpar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response limparMetricas() {
        try {
            telemetriaRepository.limparTodasMetricas();
            return Response.ok("Métricas limpas com sucesso").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao limpar métricas: " + e.getMessage())
                    .build();
        }
    }
}