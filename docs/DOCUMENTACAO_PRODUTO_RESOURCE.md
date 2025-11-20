# Documentação da API - ProdutoResource

## Visão Geral

O `ProdutoResource` é responsável pelo gerenciamento completo dos produtos de investimento disponíveis na plataforma. Permite consultar, criar, atualizar e filtrar produtos financeiros com diferentes características de rentabilidade, risco e liquidez.

**Servidor:** `http://localhost:9090`

**Base Path:** `/produtos`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** Varia por endpoint (USER/ADMIN)

**Características:**
- **Cadastro completo**: Criar, ler, atualizar e contar produtos
- **Filtros avançados**: Busca por múltiplos critérios
- **Validações rigorosas**: Bean Validation + validação customizada
- **Controle de acesso**: Operações de escrita restritas a ADMIN
- **Enums estruturados**: Tipos e características padronizadas

---

## Sumário de Endpoints

- [1. GET /produtos](#1-get-produtos)
- [2. GET /produtos/{id}](#2-get-produtosid)
- [3. POST /produtos](#3-post-produtos)
- [4. PUT /produtos/{id}](#4-put-produtosid)
- [5. GET /produtos/count](#5-get-produtoscount)

---

## Endpoints

### 1. GET /produtos
**Lista produtos com filtros opcionais**

`http://localhost:9090/produtos`

#### Descrição
Retorna uma lista de produtos de investimento disponíveis na plataforma. Suporta filtros via query parameters para busca específica por características do produto. Se nenhum filtro for aplicado, retorna todos os produtos cadastrados.

#### Algoritmo de Filtragem
1. **Priorização**: Filtros são aplicados em ordem de especificidade
2. **Exclusividade**: Apenas um filtro é aplicado por requisição
3. **Fallback**: Sem filtros = listagem completa
4. **Ordem de precedência**:
   - `tipo` → `tipo_rentabilidade` → `fgc` → `liquidez_imediata` → `sem_liquidez` → `nome` → todos

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /produtos`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Query Parameters (todos opcionais):**

| Parâmetro | Tipo | Descrição | Valores Possíveis                                                              |
|-----------|------|-----------|--------------------------------------------------------------------------------|
| `tipo` | enum | Filtra por tipo de produto | CDB, LCI, LCA, TESOURO_DIRETO, POUPANCA, DEBENTURE, CRI, FUNDO, FII, ACAO, ETF |
| `tipo_rentabilidade` | enum | Filtra por tipo de rentabilidade | PRE, POS **(Em rentabilidade POS o sistema simulará os valores dos índices.)** |
| `fgc` | boolean | Produtos protegidos pelo FGC | true, false                                                                    |
| `liquidez_imediata` | boolean | Produtos com liquidez imediata | true, false                                                                    |
| `sem_liquidez` | boolean | Produtos sem liquidez | true, false                                                                    |
| `nome` | string | Busca por nome (contém texto) | Qualquer texto não vazio                                                       |

**Exemplos de URLs:**
```
GET /produtos
GET /produtos?tipo=CDB
GET /produtos?tipo_rentabilidade=POS
GET /produtos?fgc=true
GET /produtos?liquidez_imediata=true
GET /produtos?sem_liquidez=true
GET /produtos?nome=Premium
```

**Regras de Filtragem:**
- **Um filtro por vez**: Se múltiplos filtros forem fornecidos, apenas o primeiro na ordem de precedência será aplicado
- **Case sensitive**: Enum values devem ser exatos
- **Nome flexível**: Busca por substring (case insensitive)
- **Boolean explícito**: `fgc=true` filtra produtos COM FGC; `fgc=false` não filtra

#### Responses

##### ✅ 200 - Sucesso

**Response Body:**
```json
[
  {
    "id": 1,
    "nome": "CDB Banco XYZ 120% CDI",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 20.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 90,
    "minimo_dias_investimento": 30,
    "fgc": true,
    "risco": "BAIXO"
  },
  {
    "id": 2,
    "nome": "LCI Premium Pré-fixada",
    "tipo": "LCI",
    "tipo_rentabilidade": "PRE",
    "rentabilidade": 11.5,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "NENHUM",
    "liquidez": -1,
    "minimo_dias_investimento": 90,
    "fgc": true,
    "risco": "BAIXO"
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único do produto |
| `nome` | String | Nome completo do produto |
| `tipo` | enum | Tipo de produto financeiro |
| `tipo_rentabilidade` | enum | PRE (pré-fixado) ou POS (pós-fixado) **(Em rentabilidade POS o sistema simulará os valores dos índices.)**|
| `rentabilidade` | BigDecimal | Taxa/percentual de rentabilidade |
| `periodo_rentabilidade` | enum | Periodicidade da rentabilidade |
| `indice` | enum | Índice de referência (CDI, SELIC, etc.) |
| `liquidez` | Integer | Dias para liquidez (-1 = sem liquidez, >= 0 = dias) |
| `minimo_dias_investimento` | Integer | Prazo mínimo obrigatório |
| `fgc` | Boolean | Protegido pelo Fundo Garantidor de Créditos |
| `risco` | enum | Nível de risco (BAIXO, MEDIO, ALTO) |

---

### 2. GET /produtos/{id}
**Busca produto específico por ID**

`http://localhost:9090/produtos/{id}`

#### Descrição
Retorna os detalhes completos de um produto específico através do seu ID único. Utilizado para consulta detalhada de produtos individuais.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /produtos/{id}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `id` | Long | **Sim** | `@NotNull` | ID único do produto |

**Exemplo:** `GET /produtos/1`

#### Responses

##### ✅ 200 - Produto encontrado

**Response Body:**
```json
{
  "id": 1,
  "nome": "CDB Banco XYZ 120% CDI",
  "tipo": "CDB",
  "tipo_rentabilidade": "POS",
  "rentabilidade": 20.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 90,
  "minimo_dias_investimento": 30,
  "fgc": true,
  "risco": "BAIXO"
}
```

---

### 3. POST /produtos
**Cria um novo produto**

`http://localhost:9090/produtos`

#### Descrição
Cria um novo produto de investimento na plataforma. Realiza validações completas dos dados fornecidos, incluindo validação customizada da consistência entre tipo de rentabilidade e índice.

#### Validações Aplicadas
1. **Bean Validation**: Campos obrigatórios, tamanhos e formatos
2. **Validação customizada**: Consistência rentabilidade-índice
3. **Regras de negócio**: Lógica específica do domínio

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

#### Request

**URL:** `POST /produtos`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nome": "CDB Premium 125% CDI",
  "tipo": "CDB",
  "tipo_rentabilidade": "POS",
  "rentabilidade": 25.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 30,
  "minimo_dias_investimento": 30,
  "fgc": true
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `nome` | String | **Sim** | `@NotBlank`, `@Size(min=2, max=255)` | Nome do produto (2-255 caracteres) |
| `tipo` | enum | **Sim** | `@NotNull` | Tipo de produto financeiro |
| `tipo_rentabilidade` | enum | **Sim** | `@NotNull` | PRE ou POS **(Em rentabilidade POS o sistema simulará os valores dos índices.)**|
| `rentabilidade` | BigDecimal | **Sim** | `@NotNull`, `@DecimalMin("0.0")` | Taxa de rentabilidade (>= 0) |
| `periodo_rentabilidade` | enum | **Sim** | `@NotNull` | AO_DIA, AO_MES, AO_ANO, PERIODO_TOTAL |
| `indice` | enum | Condicional | `@ValidRentabilidadeIndice` | Obrigatório se POS, opcional se PRE |
| `liquidez` | Integer | **Sim** | `@NotNull`, `@Min(-1)` | -1 (sem liquidez) ou >= 0 (dias) |
| `minimo_dias_investimento` | Integer | **Sim** | `@NotNull`, `@Min(0)` | Prazo mínimo em dias |
| `fgc` | Boolean | **Sim** | `@NotNull` | Protegido pelo FGC |

**Validações Especiais:**

1. **@ValidRentabilidadeIndice**: Validação customizada que verifica:
   - Se `tipo_rentabilidade = "POS"` → `indice` deve ser diferente de `null` e `"NENHUM"`
   - Se `tipo_rentabilidade = "PRE"` → `indice` pode ser `null` ou `"NENHUM"`

    
2. **Em rentabilidade POS o sistema simulará os valores dos índices.**
**Exemplos de Requests Válidos:**

*Produto pré-fixado:*
```json
{
  "nome": "LCI Pré-fixada 11% a.a.",
  "tipo": "LCI",
  "tipo_rentabilidade": "PRE",
  "rentabilidade": 11.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "NENHUM",
  "liquidez": -1,
  "minimo_dias_investimento": 90,
  "fgc": true
}
```

*Produto pós-fixado:*
```json
{
  "nome": "CDB 110% CDI",
  "tipo": "CDB", 
  "tipo_rentabilidade": "POS",
  "rentabilidade": 10.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 0,
  "minimo_dias_investimento": 1,
  "fgc": true
}
```

#### Responses

##### ✅ 201 - Produto criado com sucesso

**Response Body:**
```json
{
  "id": 15,
  "nome": "CDB Premium 125% CDI",
  "tipo": "CDB",
  "tipo_rentabilidade": "POS",
  "rentabilidade": 25.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 30,
  "minimo_dias_investimento": 30,
  "fgc": true,
  "risco": "BAIXO"
}
```

---

### 4. PUT /produtos/{id}
**Atualiza um produto existente**

`http://localhost:9090/produtos/{id}`

#### Descrição
Atualiza completamente um produto existente na plataforma. Realiza as mesmas validações do POST, incluindo verificação de existência do produto.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

#### Request

**URL:** `PUT /produtos/{id}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `id` | Long | **Sim** | `@NotNull` | ID do produto a ser atualizado |

**Body:** Mesmo formato do POST (todos os campos obrigatórios)

**Exemplo:** `PUT /produtos/15`

#### Responses

##### ✅ 200 - Produto atualizado com sucesso

**Response Body:**
```json
{
  "id": 15,
  "nome": "CDB Premium 130% CDI Atualizado",
  "tipo": "CDB",
  "tipo_rentabilidade": "POS",
  "rentabilidade": 30.0,
  "periodo_rentabilidade": "AO_ANO",
  "indice": "CDI",
  "liquidez": 60,
  "minimo_dias_investimento": 30,
  "fgc": true,
  "risco": "BAIXO"
}
```

---

### 5. GET /produtos/count
**Conta total de produtos**

`http://localhost:9090/produtos/count`

#### Descrição
Retorna o número total de produtos cadastrados na plataforma. Útil para paginação, dashboards e estatísticas gerais.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /produtos/count`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Parâmetros:** Nenhum

#### Responses

##### ✅ 200 - Contagem obtida com sucesso

**Response Body:**
```json
{
  "total": 42
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `total` | Long | Número total de produtos cadastrados |

---

## Status Codes e Erros por Endpoint

### GET /produtos

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Consulta executada com sucesso (mesmo se retornar lista vazia) |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não autorizada |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 401 - Unauthorized
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/produtos",
  "errors": null
}
```

---

### GET /produtos/{id}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Produto encontrado |
| **400** | Bad Request | ID inválido (não numérico) |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não autorizada |
| **404** | Not Found | Produto não encontrado |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 404 - Not Found
```json
{
  "message": "Produto não encontrado com ID: 999",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/produtos/999",
  "errors": null
}
```

---

### POST /produtos

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **201** | Created | Produto criado com sucesso |
| **400** | Bad Request | Validações falharam, dados inválidos |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não é ADMIN |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request (Validações Bean)
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/produtos",
  "errors": [
    "Campo 'nome': Nome é obrigatório",
    "Campo 'tipo': Tipo do produto é obrigatório",
    "Campo 'rentabilidade': Rentabilidade deve ser maior ou igual a zero"
  ]
}
```

##### ❌ 400 - Bad Request (Validação Customizada)
```json
{
  "message": "Erro de validação: Produtos com rentabilidade pós-fixada devem ter um índice válido (diferente de NENHUM)",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/produtos",
  "errors": null
}
```

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: apenas usuários com role ADMIN podem acessar este recurso",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/produtos",
  "errors": null
}
```

---

### PUT /produtos/{id}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Produto atualizado com sucesso |
| **400** | Bad Request | Validações falharam, ID inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não é ADMIN |
| **404** | Not Found | Produto não encontrado para atualizar |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 404 - Not Found
```json
{
  "message": "Produto não encontrado com ID: 999",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/produtos/999",
  "errors": null
}
```

---

### GET /produtos/count

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Contagem obtida com sucesso |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não autorizada |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Descrição | Endpoints Afetados |
|--------|------|-----------|-------------------|
| **200** | OK | Sucesso | GET (todos), PUT |
| **201** | Created | Produto criado | POST |
| **400** | Bad Request | Validações/dados inválidos | POST, PUT |
| **401** | Unauthorized | Token inválido | Todos |
| **403** | Forbidden | Role não autorizada | Todos |
| **404** | Not Found | Produto não encontrado | GET /{id}, PUT |
| **429** | Too Many Requests | Rate Limit | Todos |
| **500** | Internal Server Error | Erro interno | Todos |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Validação (400)

**Campos obrigatórios:**
- `"Nome é obrigatório"`
- `"Tipo do produto é obrigatório"`
- `"Tipo de rentabilidade é obrigatório"`
- `"Rentabilidade é obrigatória"`
- `"Período de rentabilidade é obrigatório"`
- `"Liquidez é obrigatória"`
- `"Mínimo de dias de investimento é obrigatório"`
- `"FGC é obrigatório"`

**Validações de formato:**
- `"Nome deve ter entre 2 e 255 caracteres"`
- `"Rentabilidade deve ser maior ou igual a zero"`
- `"Liquidez deve ser -1 (sem liquidez) ou >= 0"`
- `"Mínimo de dias de investimento deve ser >= 0"`

**Validações customizadas:**
- `"Produtos com rentabilidade pós-fixada devem ter um índice válido (diferente de NENHUM)"`

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`
- `"Token JWT inválido ou expirado"`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado: apenas usuários com role ADMIN podem acessar este recurso"`
- `"Token JWT não encontrado"`
- `"Acesso negado: role não autorizada"`

#### 🔴 Erros de Recurso (404)
- `"Produto não encontrado com ID: {id}"`

#### 🔴 Erros Internos (500)
- `"Erro interno do servidor: {detalhes do erro}"`

---

## Enums e Tipos de Dados

### TipoProduto
| Valor | Descrição | Tipo de Renda |
|-------|-----------|---------------|
| `CDB` | Certificado de Depósito Bancário | RENDA_FIXA |
| `LCI` | Letra de Crédito Imobiliário | RENDA_FIXA |
| `LCA` | Letra de Crédito do Agronegócio | RENDA_FIXA |
| `TESOURO_DIRETO` | Tesouro Direto | RENDA_FIXA |
| `POUPANCA` | Poupança | RENDA_FIXA |
| `DEBENTURE` | Debênture | RENDA_FIXA |
| `CRI` | Certificado de Recebíveis Imobiliários | RENDA_FIXA |
| `FUNDO` | Fundo de Investimento | RENDA_VARIAVEL |
| `FII` | Fundo de Investimento Imobiliário | RENDA_VARIAVEL |
| `ACAO` | Ação | RENDA_VARIAVEL |
| `ETF` | ETF | RENDA_VARIAVEL |

### TipoRentabilidade
| Valor | Descrição |
|-------|-----------|
| `PRE` | Pré-fixado |
| `POS` | Pós-fixado |

### PeriodoRentabilidade
| Valor | Significado |
|-------|-------------|
| `AO_DIA` | Ao dia |
| `AO_MES` | Ao mês |
| `AO_ANO` | Ao ano |
| `PERIODO_TOTAL` | Período total |

### Indice
| Valor | Descrição |
|-------|-----------|
| `CDI` | Certificado de Depósito Interbancário |
| `SELIC` | Sistema Especial de Liquidação e Custódia |
| `IPCA` | Índice Nacional de Preços ao Consumidor Amplo |
| `TR` | Taxa Referencial |
| `NENHUM` | Sem índice (produtos pré-fixados) |

### NivelRisco
| Valor | Descrição |
|-------|-----------|
| `BAIXO` | Risco baixo (produtos com garantias) |
| `MEDIO` | Risco médio |
| `ALTO` | Risco alto (renda variável) |

---

## Validação Customizada: @ValidRentabilidadeIndice

### Funcionamento
A anotação `@ValidRentabilidadeIndice` implementa uma validação de consistência que garante:

1. **Produtos pós-fixados (POS)**: Devem ter um índice válido
2. **Produtos pré-fixados (PRE)**: Índice é opcional

### Regras Implementadas
```java
if (tipoRentabilidade == POS) {
    // Índice não pode ser null nem NENHUM
    if (indice == null || indice == NENHUM) {
        return false; // Inválido
    }
}
// PRE ou outros casos: sempre válido
return true;
```

### Exemplos de Validação

#### ✅ Válidos
```json
// Pré-fixado sem índice
{"tipo_rentabilidade": "PRE", "indice": "NENHUM"}
{"tipo_rentabilidade": "PRE", "indice": null}

// Pós-fixado com índice
{"tipo_rentabilidade": "POS", "indice": "CDI"}
{"tipo_rentabilidade": "POS", "indice": "SELIC"}
```

#### ❌ Inválidos
```json
// Pós-fixado sem índice válido
{"tipo_rentabilidade": "POS", "indice": "NENHUM"}
{"tipo_rentabilidade": "POS", "indice": null}
```

---

## Algoritmos de Filtragem

### Precedência de Filtros
```java
if (tipo != null) return buscarPorTipo(tipo);
else if (tipoRentabilidade != null) return buscarPorTipoRentabilidade(tipoRentabilidade);
else if (fgc == TRUE) return buscarProdutosComFgc();
else if (liquidezImediata == TRUE) return buscarProdutosComLiquidezImediata();
else if (semLiquidez == TRUE) return buscarProdutosSemLiquidez();
else if (nome != null && !nome.isEmpty()) return buscarPorNome(nome);
else return listarTodos();
```

### Tipos de Busca

1. **Por tipo**: Produtos de categoria específica (CDB, LCI, etc.)
2. **Por rentabilidade**: Pré ou pós-fixados
3. **Com FGC**: Apenas produtos garantidos
4. **Liquidez imediata**: liquidez = 0
5. **Sem liquidez**: liquidez = -1
6. **Por nome**: Busca substring no nome
7. **Todos**: Sem filtros aplicados

---

## Exemplos de Uso

### cURL

**Listar todos os produtos:**
```bash
curl -X GET http://localhost:9090/produtos \
  -H "Authorization: Bearer {token}"
```

**Filtrar CDBs:**
```bash
curl -X GET "http://localhost:9090/produtos?tipo=CDB" \
  -H "Authorization: Bearer {token}"
```

**Buscar produto específico:**
```bash
curl -X GET http://localhost:9090/produtos/1 \
  -H "Authorization: Bearer {token}"
```

**Criar novo produto (ADMIN):**
```bash
curl -X POST http://localhost:9090/produtos \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "CDB Novo 115% CDI",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 15.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 90,
    "minimo_dias_investimento": 30,
    "fgc": true
  }'
```

**Atualizar produto (ADMIN):**
```bash
curl -X PUT http://localhost:9090/produtos/15 \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "CDB Atualizado 120% CDI",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 20.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 60,
    "minimo_dias_investimento": 30,
    "fgc": true
  }'
```

**Contar produtos:**
```bash
curl -X GET http://localhost:9090/produtos/count \
  -H "Authorization: Bearer {token}"
```

### Respostas Típicas

**Lista vazia:**
```json
[]
```

**Erro de validação:**
```json
{
  "message": "Dados inválidos fornecidos",
  "errors": [
    "Campo 'nome': Nome é obrigatório"
  ]
}
```

**Erro de autorização:**
```json
{
  "message": "Acesso negado: apenas usuários com role ADMIN podem acessar este recurso"
}
```



---

## Casos de Uso Típicos

### 1. Catálogo de Produtos
**Cenário**: Cliente navega pelos produtos disponíveis
```
GET /produtos → Lista completa
GET /produtos?fgc=true → Apenas com garantia
GET /produtos/5 → Detalhes específicos
```

### 2. Busca Específica
**Cenário**: Cliente busca produto por características
```
GET /produtos?tipo=CDB → CDBs disponíveis
GET /produtos?liquidez_imediata=true → Com liquidez
GET /produtos?nome=Premium → Produtos premium
```

### 3. Administração (ADMIN)
**Cenário**: Gestor gerencia catálogo
```
POST /produtos → Criar novo produto
PUT /produtos/10 → Atualizar existente
GET /produtos/count → Estatísticas
```

### 4. Análise de Portfólio
**Cenário**: Assessor analisa opções por categoria
```
GET /produtos?tipo_rentabilidade=POS → Pós-fixados
GET /produtos?tipo_rentabilidade=PRE → Pré-fixados
```

---