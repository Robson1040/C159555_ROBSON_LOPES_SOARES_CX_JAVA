# Documentação da API - ProdutoRecomendadoResource

## Visão Geral

O `ProdutoRecomendadoResource` é responsável por fornecer recomendações personalizadas de produtos de investimento baseadas no perfil de risco e histórico comportamental dos clientes. Utiliza algoritmos de Machine Learning para analisar padrões de investimento e gerar sugestões adequadas.

**Servidor:** `http://localhost:9090`

**Base Path:** `/produtos-recomendados`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** `USER`, `ADMIN`

---

## Endpoints

### 1. GET /produtos-recomendados/cliente/{clienteId}
**Obtém produtos recomendados baseados no histórico do cliente**

#### Descrição
Analisa o histórico de investimentos e simulações de um cliente específico para gerar recomendações personalizadas usando algoritmos de Machine Learning. O sistema prioriza investimentos reais e, na ausência destes, utiliza simulações como base para as recomendações.

#### Algoritmo de Recomendação
1. **Prioridade 1**: Histórico de investimentos reais
2. **Prioridade 2**: Histórico de simulações (fallback)
3. **Machine Learning**: Análise de padrões via GeradorRecomendacaoML
4. **Ordenação**: Produtos ordenados por frequência de aparição

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`
- **Validação de Acesso:** USERs só podem acessar seus próprios dados, ADMINs têm acesso irrestrito

#### Request

**URL:** `GET /produtos-recomendados/cliente/{clienteId}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `clienteId` | Long | Sim | `@Positive` | ID único do cliente (deve ser positivo) |

**Exemplo:** `GET /produtos-recomendados/cliente/1`

#### Responses

##### ✅ 200 - Recomendações obtidas com sucesso

**Response Body:**
```json
[
  {
    "id": 1,
    "nome": "CDB Banco XYZ",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 102.5,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 30,
    "minimo_dias_investimento": 90,
    "fgc": true,
    "risco": "BAIXO"
  },
  {
    "id": 5,
    "nome": "Tesouro Selic 2027",
    "tipo": "TESOURO_DIRETO",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 100.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "SELIC",
    "liquidez": 1,
    "minimo_dias_investimento": 1,
    "fgc": false,
    "risco": "BAIXO"
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único do produto |
| `nome` | string | Nome comercial do produto |
| `tipo` | enum | Tipo do produto (CDB, LCI, LCA, etc.) |
| `tipo_rentabilidade` | enum | Tipo de rentabilidade (PRE, POS) |
| `rentabilidade` | BigDecimal | Percentual de rentabilidade |
| `periodo_rentabilidade` | enum | Período (AO_DIA, AO_MES, AO_ANO, PERIODO_TOTAL) |
| `indice` | enum | Índice de referência (CDI, SELIC, IPCA, etc.) |
| `liquidez` | integer | Prazo para resgate em dias (-1 = sem liquidez) |
| `minimo_dias_investimento` | integer | Prazo mínimo de investimento em dias |
| `fgc` | boolean | Protegido pelo Fundo Garantidor de Créditos |
| `risco` | enum | Nível de risco (BAIXO, MEDIO, ALTO) |

---

### 2. GET /produtos-recomendados/{perfil}
**Obtém produtos recomendados por perfil de risco**

#### Descrição
Retorna todos os produtos adequados para um perfil de risco específico. Não requer histórico do cliente, sendo baseado apenas na classificação de risco dos produtos disponíveis.

#### Perfis Aceitos
- **conservador**: Produtos de risco BAIXO (garantidos pelo FGC)
- **moderado**: Produtos de risco MÉDIO (renda fixa sem FGC)
- **agressivo**: Produtos de risco ALTO (renda variável sem FGC)

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`
- **Validação de Acesso:** Não há validação específica de cliente

#### Request

**URL:** `GET /produtos-recomendados/{perfil}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `perfil` | string | Sim | Deve ser: conservador, moderado ou agressivo | Perfil de risco (case-insensitive) |

**Exemplos:** 
- `GET /produtos-recomendados/conservador`
- `GET /produtos-recomendados/moderado`
- `GET /produtos-recomendados/agressivo`

#### Responses

##### ✅ 200 - Produtos obtidos com sucesso

**Response Body para perfil conservador:**
```json
[
  {
    "id": 1,
    "nome": "CDB Banco ABC",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 102.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 90,
    "minimo_dias_investimento": 30,
    "fgc": true,
    "risco": "BAIXO"
  },
  {
    "id": 3,
    "nome": "Poupança CEF",
    "tipo": "POUPANCA",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 70.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "TR",
    "liquidez": 1,
    "minimo_dias_investimento": 1,
    "fgc": true,
    "risco": "BAIXO"
  }
]
```

**Response Body para perfil agressivo:**
```json
[
  {
    "id": 8,
    "nome": "Fundo Multimercado XYZ",
    "tipo": "FUNDO",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 150.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 30,
    "minimo_dias_investimento": 1,
    "fgc": false,
    "risco": "ALTO"
  },
  {
    "id": 10,
    "nome": "VALE3",
    "tipo": "ACAO",
    "tipo_rentabilidade": "POS",
    "rentabilidade": 0.0,
    "periodo_rentabilidade": "AO_DIA",
    "indice": null,
    "liquidez": 1,
    "minimo_dias_investimento": 1,
    "fgc": false,
    "risco": "ALTO"
  }
]
```

---

## Status Codes e Erros por Endpoint

### GET /produtos-recomendados/cliente/{clienteId}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Recomendações geradas com sucesso |
| **400** | Bad Request | Cliente sem histórico, ID inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | USER tentando acessar dados de outro cliente |
| **404** | Not Found | Cliente não encontrado |
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request
```json
{
  "message": "Cliente não possui histórico de investimentos nem simulações para gerar recomendações"
}
```

```json
{
  "message": "Cliente ID não pode ser nulo"
}
```

**Quando ocorre:**
- Cliente existe mas não tem investimentos nem simulações
- Parâmetro clienteId inválido (não positivo)

##### ❌ 401 - Unauthorized
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/produtos-recomendados/cliente/1",
  "errors": null
}
```

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/produtos-recomendados/cliente/2",
  "errors": null
}
```

##### ❌ 404 - Not Found (via ClienteNotFoundException)
```json
{
  "message": "Cliente não encontrado",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/produtos-recomendados/cliente/999",
  "errors": null
}
```

##### ❌ 500 - Internal Server Error
```json
{
  "message": "Erro interno no servidor"
}
```

---

### GET /produtos-recomendados/{perfil}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Produtos filtrados com sucesso |
| **400** | Bad Request | Perfil inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não autorizada |
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request
```json
{
  "message": "Perfil inválido: INVALIDO. Valores aceitos: Conservador, Moderado, Agressivo"
}
```

```json
{
  "message": "Perfil não pode ser nulo ou vazio"
}
```

**Quando ocorre:**
- Perfil não é um dos valores aceitos (conservador, moderado, agressivo)
- Perfil vazio ou nulo

##### ❌ 401 - Unauthorized
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/produtos-recomendados/conservador",
  "errors": null
}
```

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/produtos-recomendados/conservador",
  "errors": null
}
```

##### ❌ 500 - Internal Server Error
```json
{
  "message": "Erro interno no servidor"
}
```

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Descrição | Endpoints Afetados |
|--------|------|-----------|-------------------|
| **200** | OK | Sucesso | Ambos |
| **400** | Bad Request | Dados inválidos/insuficientes | Ambos |
| **401** | Unauthorized | Token ausente/inválido | Ambos |
| **403** | Forbidden | Sem permissão | Ambos |
| **404** | Not Found | Cliente não encontrado | GET /cliente/{clienteId} apenas |
| **500** | Internal Server Error | Erro interno | Ambos |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Validação (400)

**Endpoint /cliente/{clienteId}:**
- `"Cliente não possui histórico de investimentos nem simulações para gerar recomendações"`
- `"Cliente ID não pode ser nulo"`

**Endpoint /{perfil}:**
- `"Perfil inválido: {perfil}. Valores aceitos: Conservador, Moderado, Agressivo"`
- `"Perfil não pode ser nulo ou vazio"`

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado. Você não possui permissão para acessar este recurso."` (role inválida)
- `"Acesso negado: usuário só pode acessar seus próprios dados"` (USER acessando outro cliente)
- `"Token JWT não encontrado"` (token ausente)
- `"Acesso negado: role não autorizada"` (role não permitida)

#### 🔴 Erros de Recurso (404)
- `"Cliente não encontrado"` (apenas endpoint /cliente/{clienteId})

#### 🔴 Erros Internos (500)
- `"Erro interno no servidor"`

### Estrutura de Erros

**Erros simples (400, 500):**
```json
{
  "message": "Mensagem do erro"
}
```

**Erros de framework (401, 403, 404):**
```json
{
  "message": "Mensagem detalhada",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/produtos-recomendados/cliente/1",
  "errors": null
}
```

---

## Perfis de Risco e Produtos

### Mapeamento de Perfis

| Perfil | Nível de Risco | Características | Produtos Típicos |
|--------|----------------|----------------|------------------|
| **Conservador** | BAIXO | Garantia FGC, baixa volatilidade | CDB, LCI, LCA, Poupança, Tesouro Direto |
| **Moderado** | MÉDIO | Renda fixa sem FGC | Debêntures, CRI, Fundos DI |
| **Agressivo** | ALTO | Renda variável, alta volatilidade | Ações, FII, Fundos Multimercado, ETFs |

### Tipos de Produtos Disponíveis

#### Renda Fixa (Baixo/Médio Risco)
- **CDB**: Certificado de Depósito Bancário
- **LCI**: Letra de Crédito Imobiliário
- **LCA**: Letra de Crédito do Agronegócio
- **TESOURO_DIRETO**: Títulos do Tesouro Nacional
- **POUPANCA**: Caderneta de Poupança
- **DEBENTURE**: Debêntures corporativas
- **CRI**: Certificado de Recebíveis Imobiliários

#### Renda Variável (Alto Risco)
- **FUNDO**: Fundos de Investimento
- **FII**: Fundos de Investimento Imobiliário
- **ACAO**: Ações de empresas
- **ETF**: Exchange Traded Funds

### Algoritmo de Machine Learning

#### Para endpoint /cliente/{clienteId}:
1. **Coleta de Dados**: Investimentos reais → Simulações (fallback)
2. **Análise ML**: `GeradorRecomendacaoML.encontrarProdutosOrdenadosPorAparicao`
3. **Ordenação**: Por frequência de aparição no histórico
4. **Resultado**: Lista ordenada de produtos mais adequados

#### Para endpoint /{perfil}:
1. **Mapeamento**: Perfil → NivelRisco enum
2. **Filtro**: Produtos com risco correspondente
3. **Resultado**: Todos os produtos do nível de risco

---

## Informações Técnicas

### Autenticação e Autorização

**Validação de Acesso (endpoint /cliente/{clienteId}):**
- Utiliza `JwtAuthorizationHelper.validarAcessoAoCliente()`
- **ADMIN**: Acesso irrestrito
- **USER**: Apenas seus próprios dados (baseado em `userId` no JWT)

**Token JWT deve conter:**
- Role válida (`USER` ou `ADMIN`)
- `userId` (para validação de propriedade)
- Token não expirado

### Dependências do Sistema

- **ClienteService**: Validação da existência do cliente
- **ProdutoRepository**: Busca de produtos disponíveis
- **InvestimentoRepository**: Histórico de investimentos reais
- **SimulacaoRepository**: Histórico de simulações
- **GeradorRecomendacaoML**: Algoritmo de machine learning
- **ProdutoMapper**: Conversão entidade → DTO

### Performance

- **Cache**: Não implementado (consulta sempre atualizada)
- **ML Processing**: Pode ser intensivo com histórico extenso
- **Database Queries**: Múltiplas consultas por request
- **Complexidade**: O(n) onde n = tamanho do histórico

---

## Exemplos de Uso

### cURL

**Recomendações por cliente:**
```bash
curl -X GET http://localhost:9090/produtos-recomendados/cliente/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

**Recomendações por perfil conservador:**
```bash
curl -X GET http://localhost:9090/produtos-recomendados/conservador \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

**Recomendações por perfil agressivo:**
```bash
curl -X GET http://localhost:9090/produtos-recomendados/agressivo \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### Respostas Típicas

**Cliente com histórico rico:**
```json
[
  {
    "id": 1,
    "nome": "CDB Premium",
    "tipo": "CDB",
    "rentabilidade": 105.0,
    "risco": "BAIXO",
    "fgc": true
  }
]
```

**Cliente sem histórico:**
```bash
HTTP/1.1 400 Bad Request
{
  "message": "Cliente não possui histórico de investimentos nem simulações para gerar recomendações"
}
```

**Perfil inválido:**
```bash
HTTP/1.1 400 Bad Request
{
  "message": "Perfil inválido: INEXISTENTE. Valores aceitos: Conservador, Moderado, Agressivo"
}
```



---

## Casos de Uso Típicos

### 1. Recomendação Personalizada
**Cenário**: Cliente logado quer ver produtos adequados
```
GET /produtos-recomendados/cliente/{meuId}
→ Lista baseada no meu histórico real
```

### 2. Exploração por Perfil
**Cenário**: Cliente quer ver produtos de um perfil específico
```
GET /produtos-recomendados/conservador
→ Todos os produtos de baixo risco
```

### 3. Onboarding de Cliente
**Cenário**: Cliente novo sem histórico
```
GET /produtos-recomendados/cliente/novo
→ 400: Sem histórico
GET /produtos-recomendados/moderado  
→ Produtos padrão para começar
```

### 4. Assessment de Produtos
**Cenário**: Ver diferenças entre perfis
```
GET /produtos-recomendados/conservador  → Produtos seguros
GET /produtos-recomendados/agressivo    → Produtos arriscados
```

