package br.gov.caixa.api.investimentos.filter;

import br.gov.caixa.api.investimentos.service.telemetria.MetricasManager;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TelemetriaFilterTest {

    private TelemetriaFilter filter;
    private MetricasManager metricasManager;

    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;

    @BeforeEach
    void setUp() {
        filter = new TelemetriaFilter();
        metricasManager = mock(MetricasManager.class);
        filter.metricasManager = metricasManager;

        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
    }

    @Test
    void filterRequest_deveArmazenarTempoInicio() throws Exception {
        when(requestContext.getUriInfo()).thenReturn(mock(jakarta.ws.rs.core.UriInfo.class));
        filter.filter(requestContext);
        verify(requestContext).setProperty(eq("telemetria.start.time"), anyLong());
    }

    @Test
    void filterResponse_deveRegistrarMetricasParaEndpoint() throws Exception {
        // Mock do request context
        when(requestContext.getProperty("telemetria.start.time")).thenReturn(System.currentTimeMillis() - 100);
        when(requestContext.getUriInfo()).thenReturn(mock(jakarta.ws.rs.core.UriInfo.class));
        when(requestContext.getUriInfo().getPath()).thenReturn("/produtos/listar");

        filter.filter(requestContext, responseContext);

        // Verifica que os métodos do metricasManager foram chamados
        verify(metricasManager, times(1)).incrementarContador("/produtos/listar");
        verify(metricasManager, times(1)).registrarTempoResposta(eq("/produtos/listar"), anyLong());
    }

    @Test
    void filterResponse_naoDeveRegistrarMetricasParaEndpointTelemetria() throws Exception {
        when(requestContext.getProperty("telemetria.start.time")).thenReturn(System.currentTimeMillis() - 50);
        when(requestContext.getUriInfo()).thenReturn(mock(jakarta.ws.rs.core.UriInfo.class));
        when(requestContext.getUriInfo().getPath()).thenReturn("/telemetria/status");

        filter.filter(requestContext, responseContext);

        verify(metricasManager, never()).incrementarContador(anyString());
        verify(metricasManager, never()).registrarTempoResposta(anyString(), anyLong());
    }

    @Test
    void extractEndpointName_variosCaminhos_retornaCorreto() throws Exception {
        TelemetriaFilter filter = new TelemetriaFilter();

        Method method = TelemetriaFilter.class.getDeclaredMethod("extractEndpointName", String.class);
        method.setAccessible(true);

        assertEquals("/simular-investimento", method.invoke(filter, "/simular-investimento"));
        assertEquals("/perfil-risco/detalhe", method.invoke(filter, "/perfil-risco/detalhe/1"));
        assertEquals("/produtos", method.invoke(filter, "/produtos"));
        assertEquals("/outro/caminho", method.invoke(filter, "/outro/caminho"));
        assertEquals("/endpoint", method.invoke(filter, "/endpoint/65555"));
        assertNull(method.invoke(filter, ""));
        assertNull(method.invoke(filter, (Object) null));
    }
}