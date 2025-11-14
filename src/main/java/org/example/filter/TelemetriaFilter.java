package org.example.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.example.service.telemetria.MetricasManager;

import java.io.IOException;

@Provider
public class TelemetriaFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    MetricasManager metricasManager;

    private static final String START_TIME_PROPERTY = "telemetria.start.time";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Armazena o tempo de início da requisição
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
        System.out.println("=== FILTER REQUEST === Path: " + requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String path = requestContext.getUriInfo().getPath();
            String endpoint = extractEndpointName(path);

            System.out.println("=== FILTER RESPONSE === Path: " + path + " -> Endpoint: " + endpoint + " Duration: " + duration + "ms");

            if (endpoint != null && !endpoint.equals("telemetria")) {
                // Incrementa contador usando nosso sistema customizado
                metricasManager.incrementarContador(endpoint);
                
                // Registra tempo de resposta usando nosso sistema customizado
                metricasManager.registrarTempoResposta(endpoint, duration);

                System.out.println("=== Métricas registradas para: " + endpoint);
            }
        }
    }

    private String extractEndpointName(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String cleaned = path.replaceAll("^/+", "").replaceAll("/+$", "");

        if (cleaned.startsWith("simular-investimento")) {
            return "simular-investimento";
        } else if (cleaned.startsWith("perfil-risco")) {
            return "perfil-risco";
        } else if (cleaned.startsWith("produtos")) {
            return "produtos";
        } else if (cleaned.startsWith("telemetria")) {
            return null; // Não registrar métricas do próprio endpoint
        }

        String[] parts = cleaned.split("/");
        if (parts.length > 0) {
            return parts[0];
        }

        return cleaned;
    }
}