package br.gov.caixa.api.investimentos.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import br.gov.caixa.api.investimentos.service.telemetria.AcessoLogService;
import br.gov.caixa.api.investimentos.service.telemetria.MetricasManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Filtro que registra todos os acessos aos endpoints com detalhes completos
 * Captura: usuário, endpoint, método, IP, corpo da requisição, resposta, status e tempo de execução
 */
@Provider
public class AcessoLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    AcessoLogService acessoLogService;

    @Inject
    MetricasManager metricasManager;

    // Injeção opcional usando Instance
    @Inject
    jakarta.enterprise.inject.Instance<JsonWebToken> jwtInstance;

    private static final String START_TIME_PROPERTY = "telemetria.start.time";
    private static final String CORPO_REQUISICAO_PROPERTY = "acesso.log.corpo.requisicao";
    private static final String STATUS_CODE_PROPERTY = "acesso.log.status.code";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());

        try {
            String corpoRequisicao = extrairCorpoRequisicao(requestContext);
            requestContext.setProperty(CORPO_REQUISICAO_PROPERTY, corpoRequisicao);

            Long usuarioId = null;

            try {
                if (jwtInstance.isResolvable()) {
                    JsonWebToken jwt = jwtInstance.get();

                    Object claim = jwt.getClaim("userId");
                    if (claim != null) {
                        try {
                            usuarioId = Long.parseLong(claim.toString());
                        } catch (NumberFormatException e) {
                            usuarioId = null;
                        }
                    }
                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
            System.err.println("Erro ao processar AcessoLogFilter na requisição: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        try {
            Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
            String corpoRequisicao = (String) requestContext.getProperty(CORPO_REQUISICAO_PROPERTY);

            if (startTime != null) {
                long tempoExecucao = System.currentTimeMillis() - startTime;

                Long usuarioId = null;
                try  {
                if (jwtInstance.isResolvable()) {
                    JsonWebToken jwt = jwtInstance.get();

                    Object claim = jwt.getClaim("userId");
                    if (claim != null) {
                        try {
                            usuarioId = Long.parseLong(claim.toString());
                        } catch (NumberFormatException e) {
                            usuarioId = null;
                        }
                    }
                }
                } catch(Exception e){

                }

                String metodo = requestContext.getMethod();
                String uri = requestContext.getUriInfo().getRequestUri().toString();
                String endpoint = extrairEndpoint(requestContext.getUriInfo().getPath());
                String ipOrigem = obterIpOrigem(requestContext);
                String userAgent = requestContext.getHeaderString("User-Agent");
                int statusCode = responseContext.getStatus();
                String corpoResposta = extrairCorpoResposta(responseContext);

                String erroMessage = null;
                String erroStacktrace = null;

                if (statusCode >= 400) {
                    erroMessage = extrairMensagemErro(corpoResposta);
                }

                if (!endpoint.equals("telemetria") && !endpoint.equals("acesso-logs")) {
                    acessoLogService.registrarAcesso(
                            usuarioId,
                            endpoint,
                            metodo,
                            uri,
                            ipOrigem,
                            corpoRequisicao,
                            statusCode,
                            corpoResposta,
                            tempoExecucao,
                            userAgent,
                            erroMessage,
                            erroStacktrace
                    );

                    
                }

            }

        } catch (Exception e) {
            System.err.println("Erro ao processar AcessoLogFilter na resposta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extrai o corpo da requisição
     */
    private String extrairCorpoRequisicao(ContainerRequestContext requestContext) throws IOException {
        return null; //Checar LGPD
        /*)
        if (!temCorpoRequisicao(requestContext)) {
            return null;
        }

        try {
            byte[] body = readInputStream(requestContext.getEntityStream());
            requestContext.setEntityStream(new ByteArrayInputStream(body));
            return new String(body, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
         */
    }

    /**
     * Extrai o corpo da resposta
     */
    private String extrairCorpoResposta(ContainerResponseContext responseContext) {
        return null; //Checar LGPD
        /*
        try {
            if (responseContext.getEntity() != null) {
                // Se a entidade já é String
                if (responseContext.getEntity() instanceof String) {
                    return (String) responseContext.getEntity();
                }
                // Caso contrário, converte para JSON
                return responseContext.getEntity().toString();
            }
        } catch (Exception e) {
            // Falha silenciosa
        }
        return null;
         */
    }

    /**
     * Extrai mensagem de erro da resposta
     */
    private String extrairMensagemErro(String corpoResposta) {
        if (corpoResposta == null) {
            return null;
        }

        try {
            // Tenta extrair campo "message" de JSON simples
            if (corpoResposta.contains("\"message\"")) {
                int inicio = corpoResposta.indexOf("\"message\":");
                int fim = corpoResposta.indexOf(",", inicio);
                if (fim == -1) {
                    fim = corpoResposta.indexOf("}", inicio);
                }
                if (inicio != -1 && fim != -1) {
                    String mensagem = corpoResposta.substring(inicio + 10, fim);
                    return mensagem.replaceAll("\"", "").trim();
                }
            }
        } catch (Exception e) {
            // Falha silenciosa
        }
        return null;
    }

    /**
     * Extrai endpoint da URI
     */
    private String extrairEndpoint(String path) {
        if (path == null || path.isEmpty()) {
            return "root";
        }

        String cleaned = path.replaceAll("^/+", "").replaceAll("/+$", "");

        if (cleaned.startsWith("simular-investimento")) {
            return "simular-investimento";
        } else if (cleaned.startsWith("perfil-risco")) {
            return "perfil-risco";
        } else if (cleaned.startsWith("produtos-recomendados")) {
            return "produtos-recomendados";
        } else if (cleaned.startsWith("produtos")) {
            return "produtos";
        } else if (cleaned.startsWith("investimentos")) {
            return "investimentos";
        } else if (cleaned.startsWith("clientes")) {
            return "clientes";
        } else if (cleaned.startsWith("telemetria")) {
            return "telemetria";
        } else if (cleaned.startsWith("simulacoes")) {
            return "simulacoes";
        } else if (cleaned.startsWith("entrar")) {
            return "autenticacao";
        }

        String[] parts = cleaned.split("/");
        return parts.length > 0 ? parts[0] : cleaned;
    }

    /**
     * Obtém IP de origem da requisição
     */
    private String obterIpOrigem(ContainerRequestContext requestContext) {
        String xForwardedFor = requestContext.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = requestContext.getHeaderString("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fallback para remote address (em Docker pode não funcionar bem)
        return requestContext.getHeaderString("Host") != null ?
               requestContext.getHeaderString("Host").split(":")[0] :
               "desconhecido";
    }

    /**
     * Verifica se a requisição tem corpo
     */
    private boolean temCorpoRequisicao(ContainerRequestContext requestContext) {
        String metodo = requestContext.getMethod();
        return metodo.equals("POST") || metodo.equals("PUT") || metodo.equals("PATCH");
    }

    /**
     * Lê um InputStream
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}

