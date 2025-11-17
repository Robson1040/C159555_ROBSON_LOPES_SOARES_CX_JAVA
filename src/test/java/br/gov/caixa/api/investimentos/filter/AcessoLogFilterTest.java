package br.gov.caixa.api.investimentos.filter;

import br.gov.caixa.api.investimentos.service.telemetria.AcessoLogService;
import br.gov.caixa.api.investimentos.service.telemetria.MetricasManager;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcessoLogFilterTest {

    private AcessoLogFilter filter;
    private AcessoLogService acessoLogService;
    private MetricasManager metricasManager;
    private Instance<JsonWebToken> jwtInstance;
    private JsonWebToken jwt;

    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;
    private UriInfo uriInfo;

    @BeforeEach
    void setUp() {
        filter = new AcessoLogFilter();
        acessoLogService = mock(AcessoLogService.class);
        metricasManager = mock(MetricasManager.class);
        @SuppressWarnings("unchecked")
        Instance<JsonWebToken> tempJwt = mock(Instance.class);
        jwtInstance = tempJwt;
        jwt = mock(JsonWebToken.class);

        // injetar dependências (são package-private no filter)
        filter.acessoLogService = acessoLogService;
        filter.metricasManager = metricasManager;
        filter.jwtInstance = jwtInstance;

        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
        uriInfo = mock(UriInfo.class);
    }

    @Test
    void filterRequest_deveArmazenarTempoInicioECorpoNulo() throws Exception {
        when(requestContext.getUriInfo()).thenReturn(uriInfo);

        filter.filter(requestContext);

        verify(requestContext).setProperty(eq("telemetria.start.time"), anyLong());
        verify(requestContext).setProperty(eq("acesso.log.corpo.requisicao"), isNull());
    }

    @Test
    void filterRequest_jwtNaoResolvel_deveIgnorarUsuario() throws Exception {
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(jwtInstance.isResolvable()).thenReturn(false);

        filter.filter(requestContext);

        verify(requestContext).setProperty(eq("telemetria.start.time"), anyLong());
        verify(requestContext).setProperty(eq("acesso.log.corpo.requisicao"), isNull());
        // não deve lançar exceção mesmo com jwt não resolvível
    }

    @Test
    void filterRequest_jwtClaimNaoNumerico_deveTratarNumberFormat() throws Exception {
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(jwtInstance.isResolvable()).thenReturn(true);
        when(jwtInstance.get()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn("not-a-number");

        filter.filter(requestContext);

        verify(requestContext).setProperty(eq("telemetria.start.time"), anyLong());
        verify(requestContext).setProperty(eq("acesso.log.corpo.requisicao"), isNull());
        // Não lança exceção; claim inválido é tratado
    }

    @Test
    void filterRequest_excecaoDuranteProcessamento_naoPropaga() throws Exception {
        // Fazer com que setProperty para o corpo lance exceção e verificar que método trata
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        doThrow(new RuntimeException("boom")).when(requestContext).setProperty(eq("acesso.log.corpo.requisicao"), any());

        // Mesmo lançando internamente, o método deve capturar a exceção e não propagá-la
        filter.filter(requestContext);

        // Start time ainda deve ter sido tentado
        verify(requestContext).setProperty(eq("telemetria.start.time"), anyLong());
    }

    @Test
    void filterResponse_deveRegistrarAcesso_eRegistrarMetricas() throws Exception {
        // preparar request context
        when(requestContext.getProperty("telemetria.start.time")).thenReturn(System.currentTimeMillis() - 120);
        when(requestContext.getProperty("acesso.log.corpo.requisicao")).thenReturn("corpo-req");

        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://localhost/produtos/listar"));
        when(uriInfo.getPath()).thenReturn("/produtos/listar");

        when(requestContext.getMethod()).thenReturn("GET");
        when(requestContext.getHeaderString("User-Agent")).thenReturn("JUnit-Agent");
        when(requestContext.getHeaderString("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2");

        // preparar jwt resolvable
        when(jwtInstance.isResolvable()).thenReturn(true);
        when(jwtInstance.get()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn("42");

        when(responseContext.getStatus()).thenReturn(200);

        filter.filter(requestContext, responseContext);

        // capturar argumento passado para registrarAcesso
        ArgumentCaptor<Long> usuarioCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> metodoCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> corpoReqCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> corpoRespCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> tempoCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> uaCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> erroMsgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> erroStackCaptor = ArgumentCaptor.forClass(String.class);

        verify(acessoLogService, times(1)).registrarAcesso(
                usuarioCaptor.capture(), endpointCaptor.capture(), metodoCaptor.capture(), uriCaptor.capture(),
                ipCaptor.capture(), corpoReqCaptor.capture(), statusCaptor.capture(), corpoRespCaptor.capture(),
                tempoCaptor.capture(), uaCaptor.capture(), erroMsgCaptor.capture(), erroStackCaptor.capture()
        );

        assertEquals(42L, usuarioCaptor.getValue());
        assertEquals("produtos", endpointCaptor.getValue());
        assertEquals("GET", metodoCaptor.getValue());
        assertEquals("http://localhost/produtos/listar", uriCaptor.getValue());
        assertEquals("10.0.0.1", ipCaptor.getValue());
        assertEquals("corpo-req", corpoReqCaptor.getValue());
        assertEquals(200, statusCaptor.getValue().intValue());
        assertNull(corpoRespCaptor.getValue());
        assertNotNull(tempoCaptor.getValue());
        assertEquals("JUnit-Agent", uaCaptor.getValue());
        assertNull(erroMsgCaptor.getValue());
        assertNull(erroStackCaptor.getValue());

        verify(metricasManager, times(1)).incrementarContador("produtos");
        verify(metricasManager, times(1)).registrarTempoResposta(eq("produtos"), anyLong());
    }

    @Test
    void filterResponse_startTimeNull_naoFazNada() throws Exception {
        when(requestContext.getProperty("telemetria.start.time")).thenReturn(null);
        filter.filter(requestContext, responseContext);
        verify(acessoLogService, never()).registrarAcesso(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyLong(), anyString(), anyString(), anyString());
        verify(metricasManager, never()).incrementarContador(anyString());
    }

    @Test
    void filterResponse_jwtClaimNull_deveRegistrarComUsuarioNull() throws Exception {
        when(requestContext.getProperty("telemetria.start.time")).thenReturn(System.currentTimeMillis() - 50);
        when(requestContext.getProperty("acesso.log.corpo.requisicao")).thenReturn(null);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://localhost/outro"));
        when(uriInfo.getPath()).thenReturn("/outro/path");
        when(requestContext.getMethod()).thenReturn("GET");
        when(requestContext.getHeaderString("User-Agent")).thenReturn(null);
        when(requestContext.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(requestContext.getHeaderString("X-Real-IP")).thenReturn(null);
        when(requestContext.getHeaderString("Host")).thenReturn("localhost");
        when(jwtInstance.isResolvable()).thenReturn(true);
        when(jwtInstance.get()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(null);
        when(responseContext.getStatus()).thenReturn(200);

        filter.filter(requestContext, responseContext);

        // Verifica que registrou com usuarioId = null
        ArgumentCaptor<Long> usuarioCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> metodoCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(acessoLogService, times(1)).registrarAcesso(
                usuarioCaptor.capture(), endpointCaptor.capture(), metodoCaptor.capture(), uriCaptor.capture(),
                ipCaptor.capture(), any(), statusCaptor.capture(), any(), anyLong(), any(), any(), any()
        );

        assertNull(usuarioCaptor.getValue());
        assertEquals("outro", endpointCaptor.getValue());
        assertEquals("GET", metodoCaptor.getValue());
        assertEquals("http://localhost/outro", uriCaptor.getValue());
        assertEquals(200, statusCaptor.getValue().intValue());
    }

    @Test
    void filterResponse_status400_extraiMensagem_comVirgula() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("extrairMensagemErro", String.class);
        m.setAccessible(true);

        String corpo = "{\"message\":\"erro ocorrido\", \"code\":123}";
        String resultado = (String) m.invoke(filter, corpo);

        assertEquals("erro ocorrido", resultado);
    }

    @Test
    void filterResponse_status400_extraiMensagem_semVirgula() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("extrairMensagemErro", String.class);
        m.setAccessible(true);

        String corpo = "{\"message\":\"erro final\"}";
        String resultado = (String) m.invoke(filter, corpo);

        assertEquals("erro final", resultado);
    }

    @Test
    void obterIpOrigem_desconhecido_quandoSemCabecalhos() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("obterIpOrigem", ContainerRequestContext.class);
        m.setAccessible(true);

        ContainerRequestContext rc = mock(ContainerRequestContext.class);
        when(rc.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(rc.getHeaderString("X-Real-IP")).thenReturn(null);
        when(rc.getHeaderString("Host")).thenReturn(null);

        assertEquals("desconhecido", m.invoke(filter, rc));
    }

    @Test
    void readInputStream_retornaBytes() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("readInputStream", InputStream.class);
        m.setAccessible(true);

        byte[] data = "hello world".getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        byte[] result = (byte[]) m.invoke(filter, in);
        assertArrayEquals(data, result);
    }

    @Test
    void extrairEndpoint_variosCaminhos_retornaCorreto() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("extrairEndpoint", String.class);
        m.setAccessible(true);

        assertEquals("simular-investimento", m.invoke(filter, "/simular-investimento/123"));
        assertEquals("perfil-risco", m.invoke(filter, "/perfil-risco/detalhe"));
        assertEquals("produtos-recomendados", m.invoke(filter, "/produtos-recomendados/abc"));
        assertEquals("produtos", m.invoke(filter, "/produtos/listar"));
        assertEquals("investimentos", m.invoke(filter, "/investimentos/1"));
        assertEquals("clientes", m.invoke(filter, "/clientes/1"));
        assertEquals("telemetria", m.invoke(filter, "/telemetria/status"));
        assertEquals("simulacoes", m.invoke(filter, "/simulacoes/xx"));
        assertEquals("autenticacao", m.invoke(filter, "/entrar"));
        assertEquals("root", m.invoke(filter, ""));
        assertEquals("root", m.invoke(filter, (Object) null));
    }

    @Test
    void obterIpOrigem_prioridadeDeCabecalhos() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("obterIpOrigem", ContainerRequestContext.class);
        m.setAccessible(true);

        ContainerRequestContext rc = mock(ContainerRequestContext.class);
        when(rc.getHeaderString("X-Forwarded-For")).thenReturn("1.1.1.1,2.2.2.2");
        assertEquals("1.1.1.1", m.invoke(filter, rc));

        when(rc.getHeaderString("X-Forwarded-For")).thenReturn(null);
        when(rc.getHeaderString("X-Real-IP")).thenReturn("3.3.3.3");
        assertEquals("3.3.3.3", m.invoke(filter, rc));

        when(rc.getHeaderString("X-Real-IP")).thenReturn(null);
        when(rc.getHeaderString("Host")).thenReturn("example.com:8080");
        assertEquals("example.com", m.invoke(filter, rc));
    }

    @Test
    void temCorpoRequisicao_deveIdentificarMetodos() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("temCorpoRequisicao", ContainerRequestContext.class);
        m.setAccessible(true);

        ContainerRequestContext rc = mock(ContainerRequestContext.class);
        when(rc.getMethod()).thenReturn("POST");
        assertTrue((Boolean) m.invoke(filter, rc));

        when(rc.getMethod()).thenReturn("PUT");
        assertTrue((Boolean) m.invoke(filter, rc));

        when(rc.getMethod()).thenReturn("PATCH");
        assertTrue((Boolean) m.invoke(filter, rc));

        when(rc.getMethod()).thenReturn("GET");
        assertFalse((Boolean) m.invoke(filter, rc));
    }

    @Test
    void extrairMensagemErro_parseSimples() throws Exception {
        Method m = AcessoLogFilter.class.getDeclaredMethod("extrairMensagemErro", String.class);
        m.setAccessible(true);

        String json = "{\"message\":\"erro ocorrido\", \"code\":123}";
        assertEquals("erro ocorrido", m.invoke(filter, json));

        assertNull(m.invoke(filter, "{}"));
        assertNull(m.invoke(filter, (Object) null));
    }

    @Test
    void extrairCorpoRequisicaoEResposta_retornamNulos_porLGPD() throws Exception {
        Method mReq = AcessoLogFilter.class.getDeclaredMethod("extrairCorpoRequisicao", ContainerRequestContext.class);
        mReq.setAccessible(true);
        when(requestContext.getEntityStream()).thenReturn(null);
        assertNull(mReq.invoke(filter, requestContext));

        Method mResp = AcessoLogFilter.class.getDeclaredMethod("extrairCorpoResposta", ContainerResponseContext.class);
        mResp.setAccessible(true);
        assertNull(mResp.invoke(filter, responseContext));
    }
}

