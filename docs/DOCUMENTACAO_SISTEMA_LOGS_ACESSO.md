# Sistema de Logs de Acesso - API Investimentos Caixa

## Visão Geral

Sistema completo de auditoria que registra **todos os acessos** aos endpoints da API, capturando informações detalhadas sobre quem acessou o quê, quando, de onde e com qual resultado.

**Os endpoints GET deste recurso possuem cache de 10 minutos**.

---

**Servidor:** `http://localhost:9090`

**Base Path:** `/telemetria`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` (visualização/remoção dos logs)

---

## Sumário de Endpoints

- [1. GET /telemetria/acesso-logs](#1-get-telemetriaacesso-logs)
- [2. GET /telemetria/acesso-logs/usuario/{usuarioId}](#2-get-telemetriaacesso-logsusuariousuarioid)
- [3. GET /telemetria/acesso-logs/erros](#4-get-telemetriaacesso-logserros)
- [4. GET /telemetria/acesso-logs/status/{statusCode}](#5-get-telemetriaacesso-logsstatusstatuscode)
- [5. GET /telemetria/acesso-logs/estatisticas](#6-get-telemetriaacesso-logsestatisticas)
- [6. DELETE /telemetria/acesso-logs](#7-delete-telemetriaacesso-logs)
- [7. DELETE /telemetria/acesso-logs/antigos/{diasRetencao}](#8-delete-telemetriaacesso-logsantigosdiasretencao)

---

## Endpoints

### 1. GET /telemetria/acesso-logs
**Lista todos os logs de acesso**

`http://localhost:9090/telemetria/acesso-logs`

#### Descrição
Retorna uma lista paginada (quando aplicável) com todos os registros de acesso. Recomendado utilizar filtros para operações de produção.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN`

#### Request
**URL:** `GET /telemetria/acesso-logs`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Accept: application/json
```

#### Responses
##### ✅ 200 - Lista de logs
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "usuarioNome": "joao123",
    "endpoint": "clientes",
    "metodoHttp": "POST",
    "uriCompleta": "http://localhost:9090/clientes",
    "ipOrigem": "192.168.1.100",
    "statusCode": 201,
    "tempoExecucaoMs": 45,
    "dataAcesso": "2025-11-17T10:30:45",
    "userAgent": "Mozilla/...",
    "erroMessage": null
  }
]
```

---

### 2. GET /telemetria/acesso-logs/usuario/{usuarioId}
**Busca logs de um usuário específico**

`http://localhost:9090/telemetria/acesso-logs/usuario/{usuarioId}`

#### Descrição
Retorna todos os acessos realizados por um usuário (identificado por `usuarioId`), ordenados por data descendente.

#### Path Parameters
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `usuarioId` | Long | Sim | ID do usuário a ser consultado |

#### Exemplo de Request
```
GET /telemetria/acesso-logs/usuario/1
Authorization: Bearer {token}
```

---


### 3. GET /telemetria/acesso-logs/erros
**Busca logs com erro (status >= 400)**

`http://localhost:9090/telemetria/acesso-logs/erros`

#### Descrição
Retorna registros onde a resposta da API teve código de erro (>= 400). Útil para investigação de falhas e alertas.

---

### 4. GET /telemetria/acesso-logs/status/{statusCode}
**Busca logs por código de status HTTP**

`http://localhost:9090/telemetria/acesso-logs/status/{statusCode}`

#### Descrição
Filtro para buscar acessos que resultaram em um código HTTP específico.

---

### 5. GET /telemetria/acesso-logs/estatisticas
**Retorna estatísticas dos logs**

`http://localhost:9090/telemetria/acesso-logs/estatisticas`

#### Descrição
Retorna estatísticas agregadas dos logs, como total de acessos, taxa de sucesso e taxa de erro.

**Resposta exemplificada:**
```json
{
  "totalAcessos": 1250,
  "acessosComSucesso": 1200,
  "acessosComErro": 50,
  "taxaSucesso": 96.0,
  "taxaErro": 4.0
}
```

---

### 6. DELETE /telemetria/acesso-logs
**Limpa TODOS os logs de acesso**

`http://localhost:9090/telemetria/acesso-logs`

#### Descrição
Opera��o destrutiva que remove todo o histórico de logs. Deve ser restrita a administradores e usada com cautela.

#### Exemplo de Request
```
DELETE /telemetria/acesso-logs
Authorization: Bearer {token}
```

---

### 7. DELETE /telemetria/acesso-logs/antigos/{diasRetencao}
**Limpa logs com mais de N dias**

`http://localhost:9090/telemetria/acesso-logs/antigos/{diasRetencao}`

#### Descrição
Remove registros com idade superior a `diasRetencao` dias, permitindo políticas de retenção configuráveis.

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `diasRetencao` | integer | Sim | Número de dias de retenção (ex: 30) |

---

## Dados Registrados

| Campo | Descrição | Exemplo |
|-------|-----------|---------|
| `id` | ID único do log | 1 |
| `usuarioId` | ID do usuário autenticado | 1 |
| `usuarioNome` | Nome do usuário | "joao123" |
| `endpoint` | Nome simplificado do endpoint | "clientes" |
| `metodoHttp` | Método HTTP utilizado | "POST" |
| `uriCompleta` | URL completa da requisição | "http://localhost:9090/clientes/1" |
| `ipOrigem` | IP de origem do cliente | "192.168.1.100" |
| `statusCode` | Código de status HTTP da resposta | 201 |
| `tempoExecucaoMs` | Tempo total em milissegundos | 45 |
| `dataAcesso` | Data e hora do acesso | "2025-11-17T10:30:45" |
| `userAgent` | User-Agent do cliente | "Mozilla/5.0..." |
| `erroMessage` | Mensagem de erro (se houver) | "Produto não encontrado" |
| `erroStacktrace` | Stack trace completo do erro | Java stack trace |

---

## Status Codes e Mensagens de Erro

### Status Codes Comuns
| Status | Descrição |
|--------|-----------|
| **200** | OK |
| **201** | Created |
| **400** | Bad Request |
| **401** | Unauthorized |
| **403** | Forbidden |
| **404** | Not Found |
| **429** | Too Many Requests | 
| **500** | Internal Server Error |

### Exemplos de Erro (401/403)
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/telemetria/acesso-logs",
  "errors": null
}
```

### Registros com Erro (400/404/500)
```json
{
  "message": "Produto não encontrado"
}
```

---

## Exemplos de Uso

### cURL - Listar todos os logs
```bash
curl -X GET http://localhost:9090/telemetria/acesso-logs \
  -H "Authorization: Bearer {token}"
```

### cURL - Logs por usuário
```bash
curl -X GET http://localhost:9090/telemetria/acesso-logs/usuario/1 \
  -H "Authorization: Bearer {token}"
```

### cURL - Logs de erro
```bash
curl -X GET http://localhost:9090/telemetria/acesso-logs/erros \
  -H "Authorization: Bearer {token}"
```

### cURL - Limpar logs antigos (> 90 dias)
```bash
curl -X DELETE http://localhost:9090/telemetria/acesso-logs/antigos/90 \
  -H "Authorization: Bearer {token}"
```

---

## Boas Práticas

1. **Limpeza Periódica**: Execute limpeza de logs antigos periodicamente para não sobrecarregar o banco.

2. **Monitoramento**: Verifique regularmente os acessos com erro.

3. **Segurança**: Nunca compartilhe logs que contêm dados sensíveis; truncar campos sensíveis antes de exportar.

4. **Performance**: Use filtros específicos em vez de buscar todos os logs.

---