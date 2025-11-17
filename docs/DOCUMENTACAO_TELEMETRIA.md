# Documentação da API - TelemetriaResource

## Visão Geral

O `TelemetriaResource` é responsável por fornecer métricas e dados de monitoramento da API de Investimentos Caixa. Oferece endpoints para visualizar estatísticas de uso, performance dos serviços e gerenciar dados de telemetria. Todos os endpoints são restritos a administradores.

**Servidor:** `http://localhost:9090`

**Base Path:** `/telemetria`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** `ADMIN` apenas

---

## Sumário de Endpoints

- [1. GET /telemetria](#1-get-telemetria)
- [2. GET /telemetria/detalhado](#2-get-telemetriadetalhado)
- [3. GET /telemetria/mais-acessados/{limite}](#3-get-telemetriamais-acessadoslimite)
- [4. DELETE /telemetria](#4-delete-telemetrialimpar)

---

## Endpoints

### 1. GET /telemetria
**Obtém telemetria resumida do sistema**

`http://localhost:9090/telemetria`

#### Descrição
Retorna um resumo consolidado das métricas de uso da API, incluindo quantidade de chamadas e tempo médio de resposta por endpoint, além do período de coleta dos dados.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /telemetria`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Sem body**

#### Responses

##### ✅ 200 - Telemetria obtida com sucesso

**Response Body:**
```json
{
  "servicos": [
    {
      "nome": "GET /clientes",
      "contador_execucao": 150,
      "tempo_medio_resposta": 125.5
    },
    {
      "nome": "POST /clientes", 
      "contador_execucao": 45,
      "tempo_medio_resposta": 89.2
    }
  ],
  "periodo": {
    "inicio": "2025-11-01 08:00:00",
    "fim": "2025-11-15 17:30:00"
  }
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `servicos` | array | Lista de serviços com suas métricas |
| `servicos[].nome` | string | Nome do endpoint |
| `servicos[].contador_execucao` | long | Número total de chamadas |
| `servicos[].tempo_medio_resposta` | double | Tempo médio de resposta em milissegundos |
| `periodo` | object | Período de coleta dos dados (pode ser null) |
| `periodo.inicio` | string | Data/hora de início da coleta |
| `periodo.fim` | string | Data/hora do fim da coleta |

---

### 2. GET /telemetria/detalhado
**Obtém telemetria detalhada do sistema**

`http://localhost:9090/telemetria/detalhado`

#### Descrição
Retorna todas as métricas detalhadas do banco de dados, incluindo informações completas de cada endpoint monitorado com dados de criação e atualização.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /telemetria/detalhado`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Sem body**

#### Responses

##### ✅ 200 - Telemetria detalhada obtida com sucesso

**Response Body:**
```json
[
  {
    "id": 1,
    "endpoint": "GET /clientes",
    "contadorExecucoes": 150,
    "tempoMedioResposta": 125.5,
    "tempoTotalExecucao": 18825.0,
    "dataCriacao": "2025-11-01T08:00:00",
    "ultimaAtualizacao": "2025-11-15T17:30:00"
  },
  {
    "id": 2,
    "endpoint": "POST /clientes",
    "contadorExecucoes": 45,
    "tempoMedioResposta": 89.2,
    "tempoTotalExecucao": 4014.0,
    "dataCriacao": "2025-11-01T09:15:00", 
    "ultimaAtualizacao": "2025-11-15T16:45:00"
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único da métrica |
| `endpoint` | string | Nome do endpoint monitorado |
| `contadorExecucoes` | Long | Número total de execuções |
| `tempoMedioResposta` | Double | Tempo médio de resposta (ms) |
| `tempoTotalExecucao` | Double | Tempo total acumulado (ms) |
| `dataCriacao` | datetime | Data de criação da métrica |
| `ultimaAtualizacao` | datetime | Última atualização da métrica |

---

### 3. GET /telemetria/mais-acessados/{limite}
**Obtém endpoints mais acessados**

`http://localhost:9090/telemetria/mais-acessados/{limite}`

#### Descrição
Retorna uma lista dos endpoints mais acessados, ordenados por quantidade de chamadas em ordem decrescente, limitada pelo parâmetro especificado.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /telemetria/mais-acessados/{limite}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `limite` | int | Sim | Número inteiro positivo | Quantidade máxima de registros a retornar |

**Exemplo:** `GET /telemetria/mais-acessados/5`

#### Responses

##### ✅ 200 - Endpoints mais acessados obtidos com sucesso

**Response Body:**
```json
[
  {
    "id": 3,
    "endpoint": "GET /produtos",
    "contadorExecucoes": 250,
    "tempoMedioResposta": 95.8,
    "tempoTotalExecucao": 23950.0,
    "dataCriacao": "2025-11-01T08:30:00",
    "ultimaAtualizacao": "2025-11-15T17:00:00"
  },
  {
    "id": 1,
    "endpoint": "GET /clientes",
    "contadorExecucoes": 150,
    "tempoMedioResposta": 125.5,
    "tempoTotalExecucao": 18825.0,
    "dataCriacao": "2025-11-01T08:00:00",
    "ultimaAtualizacao": "2025-11-15T17:30:00"
  }
]
```

---

### 4. DELETE /telemetria
**Limpa todas as métricas de telemetria**

`http://localhost:9090/telemetria`

#### Descrição
Remove todas as métricas de telemetria do sistema, resetando os contadores e dados históricos. Operação irreversível.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `DELETE /telemetria`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Sem body**

#### Responses

##### ✅ 204 - Métricas limpas com sucesso

**Sem body na response**

---

## Status Codes e Erros por Endpoint

### GET /telemetria

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Telemetria obtida com sucesso |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não é ADMIN |
| **500** | Internal Server Error | Erro no serviço/banco |

#### Mensagens de Erro Possíveis
```json
// 401 - Token ausente/inválido
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/telemetria",
  "errors": null
}

// 403 - Não é ADMIN
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/telemetria",
  "errors": null
}

// 500 - Erro interno
{
  "message": "Erro ao obter telemetria: Connection timeout to database",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/telemetria",
  "errors": null
}
```

---

### GET /telemetria/detalhado

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Telemetria detalhada obtida |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não é ADMIN |
| **500** | Internal Server Error | Erro no banco/repository |

#### Mensagens de Erro Possíveis
```json
// 401 - Token ausente/inválido
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/telemetria/detalhado",
  "errors": null
}

// 403 - Não é ADMIN
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/telemetria/detalhado",
  "errors": null
}

// 500 - Erro no banco
{
  "message": "Erro ao obter telemetria detalhada: Database connection failed",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/telemetria/detalhado",
  "errors": null
}
```

---

### GET /telemetria/mais-acessados/{limite}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Lista de mais acessados obtida |
| **400** | Bad Request | Parâmetro limite inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não é ADMIN |
| **500** | Internal Server Error | Erro na consulta |

#### Mensagens de Erro Possíveis
```json
// 400 - Parâmetro inválido
{
  "message": "Dados da requisição inválidos: formato JSON incorreto",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/telemetria/mais-acessados/abc",
  "errors": null
}

// 401 - Token ausente/inválido
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/telemetria/mais-acessados/5",
  "errors": null
}

// 403 - Não é ADMIN
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/telemetria/mais-acessados/5",
  "errors": null
}

// 500 - Erro na query
{
  "message": "Erro ao obter endpoints mais acessados: Query execution failed",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/telemetria/mais-acessados/5",
  "errors": null
}
```

---

### DELETE /telemetria

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **204** | No Content | Métricas limpas com sucesso |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não é ADMIN |
| **500** | Internal Server Error | Erro na operação de limpeza |

#### Mensagens de Erro Possíveis
```json
// 401 - Token ausente/inválido
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/telemetria",
  "errors": null
}

// 403 - Não é ADMIN
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/telemetria",
  "errors": null
}

// 500 - Erro na operação
{
  "message": "Erro ao limpar métricas: Transaction rollback failed",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/telemetria",
  "errors": null
}
```

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Ocorrência | Endpoints Afetados |
|--------|------|------------|-------------------|
| **200** | OK | Sucesso | GET /telemetria, GET /telemetria/detalhado, GET /telemetria/mais-acessados/{limite} |
| **204** | No Content | Operação concluída sem conteúdo | DELETE /telemetria |
| **400** | Bad Request | Parâmetros inválidos | GET /telemetria/mais-acessados/{limite} |
| **401** | Unauthorized | Token ausente/inválido | Todos os endpoints |
| **403** | Forbidden | Usuário não é ADMIN | Todos os endpoints |
| **500** | Internal Server Error | Erros internos | Todos os endpoints |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado. Você não possui permissão para acessar este recurso."`

#### 🔴 Erros de Validação (400)
- `"Dados da requisição inválidos: formato JSON incorreto"` (parâmetro limite inválido)

#### 🔴 Erros Internos (500)
- `"Erro ao obter telemetria: {detalhes do erro}"`
- `"Erro ao obter telemetria detalhada: {detalhes do erro}"`
- `"Erro ao obter endpoints mais acessados: {detalhes do erro}"`
- `"Erro ao limpar métricas: {detalhes do erro}"`

### Estrutura Padrão de Erros

```json
{
  "message": "Mensagem descritiva do erro",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/telemetria/endpoint",
  "errors": null
}
```

---

## Informações Técnicas

### Coleta de Métricas

O sistema coleta automaticamente:
- **Contador de execuções**: Número total de chamadas por endpoint
- **Tempo de resposta**: Tempo médio e total de execução
- **Timestamps**: Data de criação e última atualização
- **Performance**: Métricas de performance em tempo real

### Autorização

Todos os endpoints requerem:
- Token JWT válido no header `Authorization: Bearer {token}`
- Role `ADMIN` no token
- Token não expirado

### Dados Persistidos

As métricas são armazenadas na tabela `telemetria_metrica`:
- `endpoint`: Nome do endpoint monitorado
- `contador_execucoes`: Total de chamadas
- `tempo_medio_resposta`: Tempo médio (ms)
- `tempo_total_execucao`: Tempo total acumulado (ms)
- `data_criacao`: Timestamp de criação
- `ultima_atualizacao`: Timestamp da última atualização

### Operação de Limpeza

O endpoint DELETE `/telemetria`:
- Remove **todos** os dados de telemetria
- Operação **irreversível**
- Reseta contadores para zero
- Remove histórico de performance

---

## Exemplos de Uso

### cURL

**Obter telemetria resumida:**
```bash
curl -X GET http://localhost:9090/telemetria \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```

**Obter telemetria detalhada:**
```bash
curl -X GET http://localhost:9090/telemetria/detalhado \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```

**Obter top 10 mais acessados:**
```bash
curl -X GET http://localhost:9090/telemetria/mais-acessados/10 \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```

**Limpar métricas:**
```bash
curl -X DELETE http://localhost:9090/telemetria \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```



---