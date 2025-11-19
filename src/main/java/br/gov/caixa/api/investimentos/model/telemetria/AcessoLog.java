package br.gov.caixa.api.investimentos.model.telemetria;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "acesso_log")
public class AcessoLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;

    @Column(name = "metodo_http", nullable = false, length = 10)
    private String metodoHttp;

    @Column(name = "uri_completa", nullable = false, length = 500)
    private String uriCompleta;

    @Column(name = "ip_origem", length = 50)
    private String ipOrigem;

    @Lob
    @Column(name = "corpo_requisicao", columnDefinition = "LONGTEXT")
    private String corpoRequisicao;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Lob
    @Column(name = "corpo_resposta", columnDefinition = "LONGTEXT")
    private String corpoResposta;

    @Column(name = "tempo_execucao_ms", nullable = false)
    private Long tempoExecucaoMs;

    @Column(name = "data_acesso", nullable = false)
    private LocalDateTime dataAcesso;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "erro_message", length = 500)
    private String erroMessage;

    @Column(name = "erro_stacktrace", columnDefinition = "TEXT")
    private String erroStacktrace;

    
    public AcessoLog() {
       this.dataAcesso = LocalDateTime.now();
    }

    public AcessoLog(Long usuarioId, String endpoint, String metodoHttp,
                     String uriCompleta, String ipOrigem, String corpoRequisicao) {
        this();
        this.usuarioId = usuarioId;
        this.endpoint = endpoint;
        this.metodoHttp = metodoHttp;
        this.uriCompleta = uriCompleta;
        this.ipOrigem = ipOrigem;
        this.corpoRequisicao = corpoRequisicao;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMetodoHttp() {
        return metodoHttp;
    }

    public void setMetodoHttp(String metodoHttp) {
        this.metodoHttp = metodoHttp;
    }

    public String getUriCompleta() {
        return uriCompleta;
    }

    public void setUriCompleta(String uriCompleta) {
        this.uriCompleta = uriCompleta;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

    public String getCorpoRequisicao() {
        return corpoRequisicao;
    }

    public void setCorpoRequisicao(String corpoRequisicao) {
        this.corpoRequisicao = corpoRequisicao;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getCorpoResposta() {
        return corpoResposta;
    }

    public void setCorpoResposta(String corpoResposta) {
        this.corpoResposta = corpoResposta;
    }

    public Long getTempoExecucaoMs() {
        return tempoExecucaoMs;
    }

    public void setTempoExecucaoMs(Long tempoExecucaoMs) {
        this.tempoExecucaoMs = tempoExecucaoMs;
    }

    public LocalDateTime getDataAcesso() {
        return dataAcesso;
    }

    public void setDataAcesso(LocalDateTime dataAcesso) {
        this.dataAcesso = dataAcesso;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getErroMessage() {
        return erroMessage;
    }

    public void setErroMessage(String erroMessage) {
        this.erroMessage = erroMessage;
    }

    public String getErroStacktrace() {
        return erroStacktrace;
    }

    public void setErroStacktrace(String erroStacktrace) {
        this.erroStacktrace = erroStacktrace;
    }

    @Override
    public String toString() {
        return String.format(
            "AcessoLog{id=%d, usuario=%s, metodo=%s, endpoint=%s, statusCode=%d, tempoMs=%d, data=%s}",
            id, metodoHttp, endpoint, statusCode, tempoExecucaoMs, dataAcesso
        );
    }
}

