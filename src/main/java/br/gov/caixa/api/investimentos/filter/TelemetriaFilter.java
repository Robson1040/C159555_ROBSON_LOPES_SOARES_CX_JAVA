package br.gov.caixa.api.investimentos.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import br.gov.caixa.api.investimentos.service.telemetria.MetricasManager;

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
        
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String path = requestContext.getUriInfo().getPath();
            String endpoint = extractEndpointName(path);
            

            if (endpoint != null && !endpoint.contains("telemetria")) {
                
                metricasManager.incrementarContador(endpoint);
                
                
                metricasManager.registrarTempoResposta(endpoint, duration);

               
            }
        }
    }

    private String extractEndpointName(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        // Remove barras extras no início e fim
        String cleaned = path.replaceAll("^/+", "").replaceAll("/+$", "");



        String[] parts = cleaned.split("/");
        if (parts.length > 0) {
            // Monta até duas partes
            StringBuilder sb = new StringBuilder("/");
            sb.append(parts[0]);
            if (parts.length > 1 && !parts[1].matches("\\d+")) { // ignora se for número
                sb.append("/").append(parts[1]);
            }
            return sb.toString();
        }

        return "/" + cleaned;
    }
}