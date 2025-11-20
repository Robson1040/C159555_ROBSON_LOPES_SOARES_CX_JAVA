# Documentação da API - InvestimentoResource

## Visão Geral

O `InvestimentoResource` é responsável por registrar investimentos reais realizados pelos clientes e por consultar os investimentos associados a um cliente.

**Servidor:** `http://localhost:9090`

**Base Path:** `/investimentos`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** `USER`, `ADMIN`
- **Validação de Acesso por Cliente:** O recurso valida que o usuário autenticado tem permissão para acessar/criar investimentos para o `clienteId` informado (veja `JwtAuthorizationHelper.validarAcessoAoCliente`).

---

## Sumário de Endpoints

- [1. POST /investimentos](#1-post-investimentos)
- [2. GET /investimentos/{clienteId}](#2-get-investimentosclienteid)

---

## Endpoints

### 1. POST /investimentos
**Registra um novo investimento real**

`http://localhost:9090/investimentos`

#### Descrição
Registra um investimento realizado por um cliente. O endpoint persiste o investimento e retorna os dados do investimento criado.

#### Fluxo resumido
1. **Validação de entrada**: Campos obrigatórios e regras de negócio (inclui `@ValidPrazo`)
2. **Validação de autorização**: Validação de acesso ao cliente via JWT
3. **Persistência**: Criação do investimento por `InvestimentoService` e retorno da entidade criada
4. **Resposta**: `201 Created` com o `InvestimentoResponse`

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `POST /investimentos`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "clienteId": 1,
  "produtoId": 5,
  "valor": 10000.00,
  "prazoMeses": 12,
  "prazoDias": null,
  "prazoAnos": null
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `clienteId` | Long | **Sim** | `@NotNull` | ID do cliente que realiza o investimento |
| `produtoId` | Long | **Sim** | `@NotNull` | ID do produto financeiro adquirido |
| `valor` | BigDecimal | **Sim** | `@NotNull`, `@DecimalMin("1.00")`, `@DecimalMax("999999999.99")` | Valor do investimento (R$ 1,00 a R$ 999.999.999,99) |
| `prazoMeses` | Integer | Não* | `@Min(1)`, `@Max(600)` | Prazo em meses (1 a 600) |
| `prazoDias` | Integer | Não* | `@Min(1)`, `@Max(18250)` | Prazo em dias (1 a 18.250) |
| `prazoAnos` | Integer | Não* | `@Min(1)`, `@Max(50)` | Prazo em anos (1 a 50) |

*Regras especiais: o DTO aplica a validação `@ValidPrazo` — pelo menos um dos campos de prazo (`prazoMeses`, `prazoDias`, `prazoAnos`) deve ser informado e válido.

**Exemplo de Request:**
```json
{
  "clienteId": 1,
  "produtoId": 5,
  "valor": 5000.00,
  "prazoMeses": 24
}
```

#### Responses

##### ✅ 201 - Investimento criado com sucesso

**Response Body:**
```json
{
  "id": 987,
  "clienteId": 1,
  "produtoId": 5,
  "valor": 5000.00,
  "prazoMeses": 24,
  "prazoDias": 720,
  "prazoAnos": 2,
  "data": "2025-11-15",
  "tipo": "CDB",
  "tipo_rentabilidade": "POS",
  "rentabilidade": 20.00,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 30,
  "minimo_dias_investimento": 30,
  "fgc": true
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID do investimento criado |
| `clienteId` | Long | ID do cliente |
| `produtoId` | Long | ID do produto |
| `valor` | BigDecimal | Valor investido |
| `prazoMeses` | Integer | Prazo em meses (quando calculado/formato preferencial) |
| `prazoDias` | Integer | Prazo em dias (cálculo auxiliar) |
| `prazoAnos` | Integer | Prazo em anos (quando aplicável) |
| `data` | date | Data da aplicação |
| `tipo` | enum | Tipo do produto (ex: CDB, LCI) |
| `tipo_rentabilidade` | enum | Tipo de rentabilidade (ex: PRE, POS) |
| `rentabilidade` | BigDecimal | Valor percentual de rentabilidade |
| `periodo_rentabilidade` | enum | Período da rentabilidade (ex: AO_ANO) |
| `indice` | enum | Índice relacionado (ex: CDI, SELIC, IPCA, NENHUM) |
| `liquidez` | Integer | Liquidez em dias (-1 = sem liquidez) |
| `minimo_dias_investimento` | Integer | Mínimo de dias para resgate |
| `fgc` | Boolean | Indica proteção pelo FGC |

---

### 2. GET /investimentos/{clienteId}
**Busca todos os investimentos de um cliente**

`http://localhost:9090/investimentos/{clienteId}`

#### Descrição
Retorna a lista de investimentos persistidos para o cliente informado. O endpoint ordena ou filtra conforme regras internas do serviço.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /investimentos/{clienteId}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `clienteId` | Long | Sim | `@NotNull` | ID único do cliente |

**Exemplo:** `GET /investimentos/1`

#### Responses

##### ✅ 200 - Investimentos obtidos com sucesso

**Response Body:**
```json
[
  {
    "id": 987,
    "clienteId": 1,
    "produtoId": 5,
    "valor": 5000.00,
    "prazoMeses": 24,
    "prazoDias": 720,
    "prazoAnos": 2,
    "data": "2025-11-15",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 20.00,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 30,
    "minimo_dias_investimento": 30,
    "fgc": true
  }
]
```

---

## Status Codes e Erros por Endpoint

### POST /investimentos

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **201** | Created | Investimento criado com sucesso |
| **400** | Bad Request | Validações falharam, regras de negócio violadas (ex: ausência de prazo válido, valor fora dos limites) |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não autorizado a criar investimento para o cliente informado |
| **429** | Too Many Requests | Rate Limit | Todos |
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request (Validações)
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/investimentos",
  "errors": [
    "Campo 'clienteId': ID do cliente é obrigatório (valor fornecido: null)",
    "Campo 'produtoId': ID do produto é obrigatório (valor fornecido: null)",
    "Campo 'valor': Valor mínimo de investimento é R$ 1,00 (valor fornecido: 0.50)",
    "Campo 'prazoMeses': Prazo em meses deve ser no mínimo 1 (valor fornecido: 0)"
  ]
}
```

##### ❌ 400 - Bad Request (Regras de Negócio)
```json
{
  "message": "Erro de validação: Pelo menos um prazo deve ser informado (prazoMeses, prazoDias ou prazoAnos)",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/investimentos",
  "errors": null
}
```

---

### GET /investimentos/{clienteId}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Lista de investimentos retornada com sucesso |
| **204** | No Content | Cliente existe mas não possui investimentos (implementação pode retornar 200 com lista vazia)
| **400** | Bad Request | `clienteId` inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Usuário não autorizado a visualizar os investimentos deste cliente |
| **404** | Not Found | Cliente não encontrado (dependendo da implementação do serviço) |
| **429** | Too Many Requests | Rate Limit | Todos |
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 403 - Forbidden (Acesso negado)
```json
{
  "message": "Acesso negado ao cliente informado",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/investimentos/2",
  "errors": null
}
```

---


---


