package br.gov.caixa.api.investimentos.filter;

import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class RateLimitByIpFilter implements ContainerRequestFilter {


    
    @ConfigProperty(name = "rate.limit.requests_per_minute", defaultValue = "30")
    private int REQUESTS; 

    private static final long WINDOW_MS = 60_000; 
    private static final long EXPIRE_MS = 3_600_000; 

    @Inject
    HttpServerRequest vertxRequest; 

    
    private static class IpBucket {
        int count;
        long windowStart;
        long lastAccess;
    }

    private final Map<String, IpBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext ctx) {
        String ip = getClientIp(ctx);

        IpBucket bucket = buckets.compute(ip, (k, b) -> {
            long now = Instant.now().toEpochMilli();
            if (b == null || (now - b.windowStart) > WINDOW_MS) {
                
                b = new IpBucket();
                b.count = 1;
                b.windowStart = now;
            } else {
                b.count++;
            }
            b.lastAccess = now;
            return b;
        });

        cleanupExpiredIps();

        if (bucket.count > REQUESTS) {
            ctx.abortWith(Response.status(429)
                    .entity("Too Many Requests from IP: " + ip)
                    .build());
        }
    }

    private String getClientIp(ContainerRequestContext ctx) {
        String ip = ctx.getHeaderString("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = ctx.getHeaderString("X-Real-IP");
        if ((ip == null || ip.isBlank()) && vertxRequest != null) {
            ip = vertxRequest.remoteAddress().host();
        }
        if (ip == null) ip = "unknown";
        return ip.split(",")[0].trim();
    }

    private void cleanupExpiredIps() {
        long now = Instant.now().toEpochMilli();
        buckets.entrySet().removeIf(entry -> (now - entry.getValue().lastAccess) > EXPIRE_MS);
    }
}