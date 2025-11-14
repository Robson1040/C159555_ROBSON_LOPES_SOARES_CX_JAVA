package org.example.resource.telemetria;

import org.example.dto.telemetria.TelemetriaResponse;
import org.example.service.telemetria.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/telemetria")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

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
}