# Documentação da API - SimulacaoInvestimentoResource

## Visão Geral

O `SimulacaoInvestimentoResource` é responsável por realizar simulações de investimentos financeiros e gerenciar o histórico de simulações dos clientes. Permite calcular projeções de rentabilidade, consultar histórico de simulações e obter estatísticas comportamentais.

**Servidor:** `http://localhost:9090`

**Base Path:** `/simular-investimento`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** `USER`, `ADMIN`

---

## Sumário de Endpoints

- [1. POST /simular-investimento](#1-post-simular-investimento)
- [2. GET /simular-investimento/historico/{clienteId}](#2-get-simular-investimentohistoricoclienteid)
- [3. GET /simular-investimento/{id}](#3-get-simular-investimentoid)
- [4. GET /simular-investimento/estatisticas/{clienteId}](#4-get-simular-investimentoestatisticasclienteid)

---

## Endpoints

### 1. POST /simular-investimento
**Realiza simulação de investimento**

`http://localhost:9090/simular-investimento`

#### Descrição
Calcula a projeção de rentabilidade de um investimento baseado nos parâmetros fornecidos. O sistema busca produtos que atendam aos critérios especificados e retorna o resultado da simulação com o produto validado e valores calculados.

#### Algoritmo de Simulação
1. **Validação de entrada**: Campos obrigatórios e regras de negócio
2. **Busca de produtos**: Filtragem baseada nos critérios informados
3. **Cálculo financeiro**: Projeção de rentabilidade no prazo especificado
4. **Persistência**: Salvamento da simulação para histórico
5. **Resposta**: Produto validado e resultado calculado

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `POST /simular-investimento`

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
  "tipoProduto": "CDB",
  "produto": "CDB Premium",
  "tipo_rentabilidade": "POS",
  "indice": "CDI",
  "liquidez": 90,
  "fgc": true
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `clienteId` | Long | **Sim** | `@NotNull` | ID do cliente |
| `produtoId` | Long | Não | - | ID específico do produto (opcional) |
| `valor` | BigDecimal | **Sim** | `@NotNull`, `@DecimalMin("1.00")`, `@DecimalMax("999999999.99")` | Valor do investimento (R$ 1,00 a R$ 999.999.999,99) |
| `prazoMeses` | Integer | **Um obrigatório** | `@Min(1)`, `@Max(600)` | Prazo em meses (1 a 600) |
| `prazoDias` | Integer | **Um obrigatório** | `@Min(1)`, `@Max(18250)` | Prazo em dias (1 a 18.250) |
| `prazoAnos` | Integer | **Um obrigatório** | `@Min(1)`, `@Max(50)` | Prazo em anos (1 a 50) |
| `tipoProduto` | enum | Não | - | Tipo do produto (CDB, LCI, LCA, etc.) |
| `produto` | string | Não | - | Nome do produto para filtrar |
| `tipo_rentabilidade` | enum | Não | - | PRE, POS **(Em rentabilidade POS o sistema simulará os valores dos índices.)**|
| `indice` | enum | Não | - | CDI, SELIC, IPCA, TR, NENHUM |
| `liquidez` | Integer | Não | `@Min(-1)` | Liquidez desejada (-1 = sem liquidez, >= 0 = dias) |
| `fgc` | Boolean | Não | - | Protegido pelo FGC |

**Regras de Validação Especiais:**

1. **Prazo obrigatório**: Pelo menos um campo de prazo deve ser informado (`@ValidPrazo`)
2. **Liquidez**: Deve ser -1 (sem liquidez) ou >= 0 (dias de liquidez)
3. **Prazo máximo**: Não pode exceder 20 anos (240 meses) para simulação precisa
4. **Consistência rentabilidade/índice**:
   - PRE → índice deve ser NENHUM
   - POS → índice deve ser específico (CDI, SELIC, etc.)

**Exemplo de Request:**
```json
{
  "clienteId": 1,
  "valor": 5000.00,
  "prazoMeses": 24,
  "tipoProduto": "CDB", 
  "tipo_rentabilidade": "POS",
  "indice": "CDI",
  "liquidez": 30,
  "fgc": true
}
```

#### Responses

##### ✅ 201 - Simulação criada com sucesso

**Response Body:**
```json
{
  "produtoValidado": {
    "id": 1,
    "nome": "CDB Banco XYZ 120% CDI",
    "tipo": "CDB",
    "tipo_rentabilidade": "POS_FIXADO",
    "rentabilidade": 20.0,
    "periodo_rentabilidade": "AO_ANO",
    "indice": "CDI",
    "liquidez": 30,
    "minimo_dias_investimento": 30,
    "fgc": true,
    "risco": "BAIXO"
  },
  "resultadoSimulacao": {
    "valorFinal": 5624.50,
    "rentabilidadeEfetiva": 12.49,
    "prazoMeses": 24,
    "prazoDias": 720,
    "prazoAnos": 2,
    "valorInvestido": 5000.00,
    "rendimento": 624.50,
    "valorSimulado": true,
    "cenarioSimulacao": "Simulação baseada em CDI atual"
  },
  "dataSimulacao": "2025-11-15T14:30:00",
  "clienteId": 1,
  "simulacaoId": 123
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `produtoValidado` | object | Produto encontrado que atende aos critérios |
| `resultadoSimulacao` | object | Resultado dos cálculos financeiros |
| `resultadoSimulacao.valorFinal` | BigDecimal | Valor total ao final do prazo |
| `resultadoSimulacao.rentabilidadeEfetiva` | BigDecimal | Rentabilidade efetiva calculada (%) |
| `resultadoSimulacao.rendimento` | BigDecimal | Valor do rendimento (valorFinal - valorInvestido) |
| `resultadoSimulacao.valorSimulado` | Boolean | Indica se é uma simulação (true) |
| `resultadoSimulacao.cenarioSimulacao` | string | Descrição do cenário utilizado |
| `dataSimulacao` | datetime | Data/hora da simulação |
| `clienteId` | Long | ID do cliente |
| `simulacaoId` | Long | ID da simulação persistida |

---

### 2. GET /simular-investimento/historico/{clienteId}
**Busca histórico de simulações de um cliente**

`http://localhost:9090/simular-investimento/historico/{clienteId}`

#### Descrição
Retorna todas as simulações realizadas por um cliente específico, ordenadas por data. Permite acompanhar o comportamento e histórico de simulações para análise de perfil.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /simular-investimento/historico/{clienteId}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `clienteId` | Long | Sim | `@Positive` | ID único do cliente (deve ser positivo) |

**Exemplo:** `GET /simular-investimento/historico/1`

#### Responses

##### ✅ 200 - Histórico obtido com sucesso

**Response Body:**
```json
[
  {
    "id": 123,
    "produtoId": 1,
    "clienteId": 1,
    "produto": "CDB Banco XYZ 120% CDI",
    "valorInvestido": 5000.00,
    "valorFinal": 5624.50,
    "prazoMeses": 24,
    "prazoDias": 720,
    "prazoAnos": 2,
    "dataSimulacao": "2025-11-15T14:30:00",
    "rentabilidadeEfetiva": 12.49,
    "rendimento": 624.50,
    "valorSimulado": true,
    "cenarioSimulacao": "Simulação baseada em CDI atual"
  },
  {
    "id": 122,
    "produtoId": 3,
    "clienteId": 1,
    "produto": "LCI Banco ABC 95% CDI",
    "valorInvestido": 10000.00,
    "valorFinal": 10950.00,
    "prazoMeses": 12,
    "prazoDias": 360,
    "prazoAnos": 1,
    "dataSimulacao": "2025-11-10T10:15:00",
    "rentabilidadeEfetiva": 9.50,
    "rendimento": 950.00,
    "valorSimulado": true,
    "cenarioSimulacao": "Simulação baseada em CDI atual"
  }
]
```

---

### 3. GET /simular-investimento/{id}
**Busca simulação específica por ID**

`http://localhost:9090/simular-investimento/{id}`

#### Descrição
Retorna os detalhes de uma simulação específica pelo seu ID. Inclui validação de acesso para garantir que o usuário só acesse simulações próprias (ou todas se for ADMIN).

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /simular-investimento/{id}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `id` | Long | Sim | `@Positive` | ID único da simulação |

**Exemplo:** `GET /simular-investimento/123`

#### Responses

##### ✅ 200 - Simulação encontrada

**Response Body:**
```json
{
  "id": 123,
  "produtoId": 1,
  "clienteId": 1,
  "produto": "CDB Banco XYZ 120% CDI",
  "valorInvestido": 5000.00,
  "valorFinal": 5624.50,
  "prazoMeses": 24,
  "prazoDias": 720,
  "prazoAnos": 2,
  "dataSimulacao": "2025-11-15T14:30:00",
  "rentabilidadeEfetiva": 12.49,
  "rendimento": 624.50,
  "valorSimulado": true,
  "cenarioSimulacao": "Simulação baseada em CDI atual"
}
```

---

### 4. GET /simular-investimento/estatisticas/{clienteId}
**Busca estatísticas de simulações do cliente**

`http://localhost:9090/simular-investimento/estatisticas/{clienteId}`

#### Descrição
Retorna estatísticas consolidadas das simulações de um cliente, incluindo totais, médias e última simulação. Útil para análise comportamental e dashboards.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /simular-investimento/estatisticas/{clienteId}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Validação | Descrição |
|-----------|------|-------------|-----------|-----------|
| `clienteId` | Long | Sim | `@Positive` | ID único do cliente |

**Exemplo:** `GET /simular-investimento/estatisticas/1`

#### Responses

##### ✅ 200 - Estatísticas obtidas com sucesso

**Response Body:**
```json
{
  "totalSimulacoes": 15,
  "totalInvestido": 125000.00,
  "mediaValorInvestido": 8333.33,
  "ultimaSimulacao": {
    "id": 123,
    "produtoId": 1,
    "clienteId": 1,
    "produto": "CDB Banco XYZ 120% CDI",
    "valorInvestido": 5000.00,
    "valorFinal": 5624.50,
    "prazoMeses": 24,
    "prazoDias": 720,
    "prazoAnos": 2,
    "dataSimulacao": "2025-11-15T14:30:00",
    "rentabilidadeEfetiva": 12.49,
    "rendimento": 624.50,
    "valorSimulado": true,
    "cenarioSimulacao": "Simulação baseada em CDI atual"
  }
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `totalSimulacoes` | Long | Número total de simulações realizadas |
| `totalInvestido` | BigDecimal | Soma de todos os valores simulados |
| `mediaValorInvestido` | BigDecimal | Valor médio por simulação |
| `ultimaSimulacao` | object | Dados da simulação mais recente (pode ser null) |

---

## Status Codes e Erros por Endpoint

### POST /simular-investimento

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **201** | Created | Simulação criada com sucesso |
| **400** | Bad Request | Validações falharam, regras de negócio violadas |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Role não autorizada |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request (Validações)
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/simular-investimento",
  "errors": [
    "Campo 'clienteId': ID do cliente é obrigatório (valor fornecido: null)",
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
  "path": "/simular-investimento",
  "errors": null
}
```

```json
{
  "message": "Erro de validação: Produtos pré-fixados não devem ter índice específico. Use 'NENHUM' como índice.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/simular-investimento",
  "errors": null
}
```

```json
{
  "message": "Erro de validação: Prazo muito longo para simulação precisa. Máximo recomendado: 20 anos (240 meses)",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/simular-investimento",
  "errors": null
}
```

---

### GET /simular-investimento/historico/{clienteId}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Histórico obtido com sucesso |
| **400** | Bad Request | clienteId inválido (não positivo) |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | USER tentando acessar dados de outro cliente |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/simular-investimento/historico/2",
  "errors": null
}
```

---

### GET /simular-investimento/{id}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Simulação encontrada |
| **400** | Bad Request | ID inválido (não positivo) |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Tentativa de acessar simulação de outro cliente |
| **404** | Not Found | Simulação não encontrada |
| **429** | Too Many Requests | Rate Limit | 
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 404 - Not Found
```json
{
  "message": "Simulação não encontrada com ID: 999",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/simular-investimento/999",
  "errors": null
}
```

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/simular-investimento/123",
  "errors": null
}
```

---

### GET /simular-investimento/estatisticas/{clienteId}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Estatísticas obtidas |
| **400** | Bad Request | clienteId inválido |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Acesso negado aos dados do cliente |
| **429** | Too Many Requests | Rate Limit |
| **500** | Internal Server Error | Erro interno |

#### Mensagens de Erro Possíveis

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/simular-investimento/estatisticas/2",
  "errors": null
}
```

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Descrição | Endpoints Afetados |
|--------|------|-----------|-------------------|
| **200** | OK | Sucesso | GET (historico, por ID, estatísticas) |
| **201** | Created | Simulação criada | POST |
| **400** | Bad Request | Validações/regras violadas | Todos |
| **401** | Unauthorized | Token inválido | Todos |
| **403** | Forbidden | Sem permissão específica | Todos autenticados |
| **404** | Not Found | Simulação não encontrada | GET /{id} |
| **429** | Too Many Requests | Rate Limit | Todos |
| **500** | Internal Server Error | Erro interno | Todos |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Validação (400)

**Campos obrigatórios:**
- `"ID do cliente é obrigatório"`
- `"Valor do investimento é obrigatório"`

**Validações de range:**
- `"Valor mínimo de investimento é R$ 1,00"`
- `"Valor máximo de investimento é R$ 999.999.999,99"`
- `"Prazo em meses deve ser no mínimo 1"`
- `"Prazo em meses deve ser no máximo 600 (50 anos)"`
- `"Liquidez deve ser -1 (sem liquidez) ou >= 0"`

**Validações customizadas:**
- `"Pelo menos um prazo deve ser informado (prazoMeses, prazoDias ou prazoAnos)"`

**Regras de negócio:**
- `"Liquidez deve ser -1 (sem liquidos) ou o número de dias desejado."`
- `"Prazo muito longo para simulação precisa. Máximo recomendado: 20 anos (240 meses)"`
- `"Produtos pré-fixados não devem ter índice específico. Use 'NENHUM' como índice."`
- `"Produtos pós-fixados devem ter um índice específico (CDI, SELIC, IPCA, etc.)"`

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado: usuário só pode acessar seus próprios dados"`
- `"Token JWT não encontrado"`
- `"Acesso negado: role não autorizada"`

#### 🔴 Erros de Recurso (404)
- `"Simulação não encontrada com ID: {id}"`

#### 🔴 Erros Internos (500)
- `"Erro interno do servidor: {detalhes do erro}"`

---

## Tipos de Produtos e Enums

### TipoProduto
- `CDB`: Certificado de Depósito Bancário
- `LCI`: Letra de Crédito Imobiliário  
- `LCA`: Letra de Crédito do Agronegócio
- `TESOURO_DIRETO`: Títulos do Tesouro Nacional
- `POUPANCA`: Caderneta de Poupança
- `DEBENTURE`: Debêntures corporativas
- `CRI`: Certificado de Recebíveis Imobiliários
- `FUNDO`: Fundos de Investimento
- `FII`: Fundos de Investimento Imobiliário
- `ACAO`: Ações de empresas
- `ETF`: Exchange Traded Funds

### TipoRentabilidade
- `PRE`: Taxa conhecida no momento da aplicação
- `POS`: Taxa vinculada a um índice

### Indice
- `CDI`: Certificado de Depósito Interbancário
- `SELIC`: Sistema Especial de Liquidação e Custódia
- `IPCA`: Índice Nacional de Preços ao Consumidor Amplo
- `TR`: Taxa Referencial
- `NENHUM`: Produtos pré-fixados

### NivelRisco
- `BAIXO`: Para clientes com perfil conservador
- `MEDIO`: Para clietes com perfil moderado
- `ALTO`: Para clientes com perfil agressivo

---

## Algoritmo de Simulação

### Etapas do Processo

1. **Validação de Entrada**
   - Bean Validation nos campos
   - Validação customizada @ValidPrazo
   - Regras de negócio específicas

2. **Busca de Produtos**
   - Filtragem por critérios informados
   - Priorização por produtoId se especificado
   - Fallback para produtos compatíveis

3. **Cálculos Financeiros**
   - Aplicação de fórmulas de juros compostos
   - Consideração de índices econômicos
   - Projeção para o prazo especificado

4. **Persistência**
   - Salvamento da simulação no banco
   - Geração de ID único
   - Associação ao cliente

5. **Resposta**
   - Produto validado encontrado
   - Resultado dos cálculos
   - Metadados da simulação

### Validações Especiais

#### Consistência Rentabilidade vs. Índice
```java
// PRE_FIXADO deve ter índice NENHUM
if (tipoRentabilidade == PRE && indice != NENHUM) {
    throw erro("Pré-fixado não deve ter índice");
}

// POS_FIXADO deve ter índice específico  
if (tipoRentabilidade == POS && indice == NENHUM) {
    throw erro("Pós-fixado deve ter índice");
}
```

#### Prazo Máximo
```java
if (prazoMeses > 240) { // 20 anos
    throw erro("Prazo muito longo");
}
```

---

## Informações Técnicas

### Autenticação e Autorização

**Validação de Propriedade:**
- Utiliza `JwtAuthorizationHelper.validarAcessoAoCliente()`
- **ADMIN**: Acesso irrestrito
- **USER**: Apenas dados próprios (baseado em `userId` no JWT)

**Endpoints com Validação de Acesso:**
- `GET /historico/{clienteId}`: Valida acesso ao cliente
- `GET /{id}`: Valida acesso via clienteId da simulação
- `GET /estatisticas/{clienteId}`: Valida acesso ao cliente
- `POST /`: Sem validação específica (qualquer user pode simular)

### Dependências do Sistema

- **SimulacaoInvestimentoService**: Lógica de negócio
- **ProdutoRepository**: Busca de produtos disponíveis
- **SimulacaoRepository**: Persistência de simulações
- **ClienteService**: Validação de clientes (indireta)
- **JwtAuthorizationHelper**: Controle de acesso
- **SimulacaoInvestimentoMapper**: Conversão entidade → DTO

### Performance

- **Cálculos intensivos**: Simulações com múltiplos produtos
- **Consultas ao banco**: Filtragem de produtos por critérios
- **Persistência**: Cada simulação é salva
- **Sem cache**: Recalcula sempre (dados atuais de mercado)

---

## Exemplos de Uso

### cURL

**Simulação básica:**
```bash
curl -X POST http://localhost:9090/simular-investimento \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "valor": 10000.00,
    "prazoMeses": 12,
    "tipoProduto": "CDB",
    "liquidez": 90,
    "fgc": true
  }'
```

**Buscar histórico:**
```bash
curl -X GET http://localhost:9090/simular-investimento/historico/1 \
  -H "Authorization: Bearer {token}"
```

**Buscar simulação específica:**
```bash
curl -X GET http://localhost:9090/simular-investimento/123 \
  -H "Authorization: Bearer {token}"
```

**Obter estatísticas:**
```bash
curl -X GET http://localhost:9090/simular-investimento/estatisticas/1 \
  -H "Authorization: Bearer {token}"
```

### Respostas Típicas

**Simulação bem-sucedida:**
```json
{
  "produtoValidado": {
    "nome": "CDB Premium 115% CDI",
    "rentabilidade": 15.0,
    "fgc": true
  },
  "resultadoSimulacao": {
    "valorFinal": 11150.00,
    "rendimento": 1150.00,
    "rentabilidadeEfetiva": 11.5
  },
  "simulacaoId": 124
}
```

**Erro de validação:**
```json
{
  "message": "Dados inválidos fornecidos",
  "errors": [
    "Campo 'valor': Valor mínimo de investimento é R$ 1,00"
  ]
}
```



---

## Casos de Uso Típicos

### 1. Simulação Exploratória
**Cenário**: Cliente quer conhecer opções de investimento
```json
{
  "clienteId": 1,
  "valor": 5000.00,
  "prazoMeses": 12,
  "fgc": true
}
```
→ Sistema retorna melhor produto com FGC

### 2. Simulação Direcionada
**Cenário**: Cliente quer simular produto específico
```json
{
  "clienteId": 1,
  "produtoId": 5,
  "valor": 10000.00,
  "prazoAnos": 2
}
```
→ Simulação do produto ID 5

### 3. Comparação de Cenários
**Cenário**: Cliente testa diferentes prazos
```
POST com prazoMeses: 12 → Resultado A
POST com prazoMeses: 24 → Resultado B  
POST com prazoMeses: 36 → Resultado C
```

### 4. Análise Comportamental
**Cenário**: Estudo do perfil do cliente
```
GET /historico/1 → Histórico completo
GET /estatisticas/1 → Métricas consolidadas
```

---