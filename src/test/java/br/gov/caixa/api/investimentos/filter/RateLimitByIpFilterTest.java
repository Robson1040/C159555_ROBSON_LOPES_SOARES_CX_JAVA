package br.gov.caixa.api.investimentos.filter;

import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RateLimitByIpFilterTest {

    private RateLimitByIpFilter filter;
    private ContainerRequestContext ctx;
    private HttpServerRequest vertxRequest;
    private io.vertx.core.net.SocketAddress socketAddress;

    @BeforeEach
    void setUp() {
        filter = new RateLimitByIpFilter();
        ctx = mock(ContainerRequestContext.class);
        vertxRequest = mock(HttpServerRequest.class);
        socketAddress = mock(io.vertx.core.net.SocketAddress.class);
        when(vertxRequest.remoteAddress()).thenReturn(socketAddress);
        // Injeta dependência simulada
        filter.vertxRequest = vertxRequest;
        // Reset internal buckets map (reflection, since it's private and final)
        try {
            java.lang.reflect.Field bucketsField = RateLimitByIpFilter.class.getDeclaredField("buckets");
            bucketsField.setAccessible(true);
            ((java.util.Map<?,?>) bucketsField.get(filter)).clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset buckets map", e);
        }
        // Optionally set REQUESTS to a known value for tests
        try {
            java.lang.reflect.Field requestsField = RateLimitByIpFilter.class.getDeclaredField("REQUESTS");
            requestsField.setAccessible(true);
            requestsField.setInt(filter, 30);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set REQUESTS value", e);
        }
    }

    @Test
    void devePermitirPrimeiraRequisicao() {
        when(ctx.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(ctx.getHeaderString("X-Real-IP")).thenReturn(null);
        when(socketAddress.host()).thenReturn("192.168.0.10");

        filter.filter(ctx);

        verify(ctx, never()).abortWith(any(Response.class));
    }

    @Test
    void deveBloquearQuandoExcederLimite() {
        when(ctx.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(ctx.getHeaderString("X-Real-IP")).thenReturn(null);
        when(socketAddress.host()).thenReturn("10.0.0.1");

        // Simula 31 requisições (limite é 30)
        for (int i = 0; i < 31; i++) {
            filter.filter(ctx);
        }

        verify(ctx, atLeastOnce()).abortWith(argThat(response ->
                response.getStatus() == 429 &&
                        response.getEntity().toString().contains("Too Many Requests")
        ));
    }

    @Test
    void deveUsarHeaderXForwardedForQuandoDisponivel() {
        when(ctx.getHeaderString("X-Forwarded-For")).thenReturn("203.0.113.5");
        when(ctx.getHeaderString("X-Real-IP")).thenReturn(null);

        filter.filter(ctx);

        verify(ctx, never()).abortWith(any(Response.class));
    }

    @Test
    void deveUsarHeaderXRealIpQuandoDisponivel() {
        when(ctx.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(ctx.getHeaderString("X-Real-IP")).thenReturn("198.51.100.7");

        filter.filter(ctx);

        verify(ctx, never()).abortWith(any(Response.class));
    }
}