# Documentação da API - SimulacaoResource

## Visão Geral

O `SimulacaoResource` é responsável por fornecer funcionalidades de consulta e análise agregada das simulações de investimentos realizadas na plataforma. Permite visualizar todas as simulações e obter insights através de agrupamentos por produto e período temporal.

**Servidor:** `http://localhost:9090`

**Base Path:** `/simulacoes`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO para todos os endpoints
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

**Características:**
- **Acesso restrito**: Apenas usuários com role ADMIN
- **Dados consolidados**: Informações agregadas de simulações
- **Análise temporal**: Agrupamentos por dia, mês e ano
- **Insights estatísticos**: Médias, quantidades e totais
- **Ordenação inteligente**: Resultados ordenados por produto e período

---

## Sumário de Endpoints

- [1. GET /simulacoes](#1-get-simulacoes)
- [2. GET /simulacoes/por-produto-dia](#2-get-simulacoespor-produto-dia)
- [3. GET /simulacoes/por-produto-mes](#3-get-simulacoespor-produto-mes)
- [4. GET /simulacoes/por-produto-ano](#4-get-simulacoespor-produto-ano)

---

## Endpoints

### 1. GET /simulacoes
**Lista todas as simulações realizadas**

`http://localhost:9090/simulacoes`

#### Descrição
Retorna uma lista completa de todas as simulações de investimentos realizadas na plataforma, incluindo dados detalhados de cada simulação. Útil para análise completa do histórico de simulações de todos os clientes.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**
- **Validação de Acesso:** Apenas administradores podem acessar dados consolidados

#### Request

**URL:** `GET /simulacoes`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Parâmetros:** Nenhum

#### Responses

##### ✅ 200 - Sucesso

**Response Body:**
```json
[
  {
    "id": 123,
    "clienteId": 1,
    "produto": "CDB Banco XYZ 120% CDI",
    "valorInvestido": 10000.00,
    "valorFinal": 11250.00,
    "prazoMeses": 12,
    "prazoDias": 365,
    "prazoAnos": 1,
    "dataSimulacao": "2025-11-15T14:30:00"
  },
  {
    "id": 124,
    "clienteId": 2,
    "produto": "LCI Banco ABC 95% CDI",
    "valorInvestido": 5000.00,
    "valorFinal": 5475.00,
    "prazoMeses": 12,
    "prazoDias": 365,
    "prazoAnos": 1,
    "dataSimulacao": "2025-11-15T15:45:00"
  },
  {
    "id": 125,
    "clienteId": 1,
    "produto": "CDB Premium 130% CDI",
    "valorInvestido": 25000.00,
    "valorFinal": 28750.00,
    "prazoMeses": 18,
    "prazoDias": 540,
    "prazoAnos": 1,
    "dataSimulacao": "2025-11-14T09:20:00"
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único da simulação |
| `clienteId` | Long | ID do cliente que realizou a simulação |
| `produto` | String | Nome do produto simulado |
| `valorInvestido` | BigDecimal | Valor inicial do investimento |
| `valorFinal` | BigDecimal | Valor final projetado |
| `prazoMeses` | Integer | Prazo da simulação em meses |
| `prazoDias` | Integer | Prazo da simulação em dias |
| `prazoAnos` | Integer | Prazo da simulação em anos |
| `dataSimulacao` | LocalDateTime | Data e hora da realização da simulação |

**Ordenação:** Natural do banco de dados (por ID crescente)

---

### 2. GET /simulacoes/por-produto-dia
**Agrupamento de simulações por produto e data**

`http://localhost:9090/simulacoes/por-produto-dia`

#### Descrição
Retorna um agrupamento das simulações organizadas por produto e data específica (sem considerar horário). Para cada combinação produto/data, calcula estatísticas agregadas como quantidade total, valor médio investido e valor médio final projetado.

#### Algoritmo de Agrupamento
1. **Filtragem**: Remove simulações com dados nulos (produto ou data)
2. **Agrupamento**: Organiza por nome do produto → data (LocalDate)
3. **Cálculos estatísticos**:
   - Soma dos valores investidos e finais
   - Média aritmética com precisão de 2 casas decimais
   - Arredondamento HALF_UP
4. **Ordenação**: Primeiro por produto (alfabética), depois por data (cronológica)

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

#### Request

**URL:** `GET /simulacoes/por-produto-dia`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Parâmetros:** Nenhum

#### Responses

##### ✅ 200 - Sucesso

**Response Body:**
```json
[
  {
    "produto": "CDB Banco XYZ 120% CDI",
    "data": "2025-11-15",
    "quantidadeSimulacoes": 3,
    "mediaValorInvestido": 15000.00,
    "mediaValorFinal": 16875.00
  },
  {
    "produto": "CDB Banco XYZ 120% CDI", 
    "data": "2025-11-14",
    "quantidadeSimulacoes": 1,
    "mediaValorInvestido": 25000.00,
    "mediaValorFinal": 28750.00
  },
  {
    "produto": "LCI Banco ABC 95% CDI",
    "data": "2025-11-15",
    "quantidadeSimulacoes": 2,
    "mediaValorInvestido": 7500.00,
    "mediaValorFinal": 8212.50
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `produto` | String | Nome do produto agrupado |
| `data` | LocalDate | Data das simulações (formato: YYYY-MM-DD) |
| `quantidadeSimulacoes` | Long | Número total de simulações no período |
| `mediaValorInvestido` | BigDecimal | Valor médio investido no período |
| `mediaValorFinal` | BigDecimal | Valor médio final projetado |

**Ordenação:** Por produto (alfabética) → por data (cronológica crescente)

---

### 3. GET /simulacoes/por-produto-mes
**Agrupamento de simulações por produto e mês**

`http://localhost:9090/simulacoes/por-produto-mes`

#### Descrição
Retorna um agrupamento das simulações organizadas por produto e mês/ano (YearMonth). Consolida dados mensais para análise de tendências e comportamentos sazonais de simulações por produto.

#### Algoritmo de Agrupamento
1. **Filtragem**: Remove simulações com dados nulos
2. **Agrupamento**: Organiza por produto → YearMonth (ano-mês)
3. **Cálculos estatísticos**: Mesma lógica do agrupamento diário
4. **Ordenação**: Produto alfabética → ano-mês cronológico

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

#### Request

**URL:** `GET /simulacoes/por-produto-mes`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Parâmetros:** Nenhum

#### Responses

##### ✅ 200 - Sucesso

**Response Body:**
```json
[
  {
    "produto": "CDB Banco XYZ 120% CDI",
    "mes": "2025-11",
    "quantidadeSimulacoes": 15,
    "mediaValorInvestido": 18500.00,
    "mediaValorFinal": 20812.50
  },
  {
    "produto": "CDB Banco XYZ 120% CDI",
    "mes": "2025-10", 
    "quantidadeSimulacoes": 8,
    "mediaValorInvestido": 12000.00,
    "mediaValorFinal": 13500.00
  },
  {
    "produto": "LCI Banco ABC 95% CDI",
    "mes": "2025-11",
    "quantidadeSimulacoes": 12,
    "mediaValorInvestido": 9750.00,
    "mediaValorFinal": 10687.50
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `produto` | String | Nome do produto agrupado |
| `mes` | YearMonth | Mês/ano das simulações (formato: YYYY-MM) |
| `quantidadeSimulacoes` | Long | Número total de simulações no mês |
| `mediaValorInvestido` | BigDecimal | Valor médio investido no mês |
| `mediaValorFinal` | BigDecimal | Valor médio final projetado no mês |

**Ordenação:** Por produto (alfabética) → por ano-mês (cronológica crescente)

---

### 4. GET /simulacoes/por-produto-ano
**Agrupamento de simulações por produto e ano**

`http://localhost:9090/simulacoes/por-produto-ano`

#### Descrição
Retorna um agrupamento das simulações organizadas por produto e ano. Fornece uma visão consolidada anual para análise de tendências de longo prazo e evolução dos produtos ao longo do tempo.

#### Algoritmo de Agrupamento
1. **Filtragem**: Remove simulações com dados nulos
2. **Agrupamento**: Organiza por produto → Year (ano)
3. **Cálculos estatísticos**: Mesma lógica dos demais agrupamentos
4. **Ordenação**: Produto alfabética → ano cronológico

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` **EXCLUSIVAMENTE**

#### Request

**URL:** `GET /simulacoes/por-produto-ano`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Parâmetros:** Nenhum

#### Responses

##### ✅ 200 - Sucesso

**Response Body:**
```json
[
  {
    "produto": "CDB Banco XYZ 120% CDI",
    "ano": 2025,
    "quantidadeSimulacoes": 145,
    "mediaValorInvestido": 16750.00,
    "mediaValorFinal": 18843.75
  },
  {
    "produto": "CDB Banco XYZ 120% CDI",
    "ano": 2024,
    "quantidadeSimulacoes": 89,
    "mediaValorInvestido": 14200.00,
    "mediaValorFinal": 15975.00
  },
  {
    "produto": "LCI Banco ABC 95% CDI",
    "ano": 2025,
    "quantidadeSimulacoes": 78,
    "mediaValorInvestido": 11850.00,
    "mediaValorFinal": 13002.75
  }
]
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `produto` | String | Nome do produto agrupado |
| `ano` | Year | Ano das simulações (formato: YYYY) |
| `quantidadeSimulacoes` | Long | Número total de simulações no ano |
| `mediaValorInvestido` | BigDecimal | Valor médio investido no ano |
| `mediaValorFinal` | BigDecimal | Valor médio final projetado no ano |

**Ordenação:** Por produto (alfabética) → por ano (cronológica crescente)

---

## Status Codes e Erros por Endpoint

### Todos os Endpoints (Padrão Geral)

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Consulta executada com sucesso |
| **401** | Unauthorized | Token JWT ausente, inválido ou expirado |
| **403** | Forbidden | Role diferente de ADMIN ou token sem permissões |
| **500** | Internal Server Error | Erro interno do sistema ou banco de dados |

#### Mensagens de Erro Possíveis

##### ❌ 401 - Unauthorized
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/simulacoes",
  "errors": null
}
```

```json
{
  "message": "Token JWT inválido ou expirado",
  "timestamp": "2025-11-15T14:30:00", 
  "status": 401,
  "path": "/simulacoes/por-produto-dia",
  "errors": null
}
```

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado: apenas usuários com role ADMIN podem acessar este recurso",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/simulacoes",
  "errors": null
}
```

```json
{
  "message": "Token JWT não encontrado",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/simulacoes/por-produto-mes",
  "errors": null
}
```

##### ❌ 500 - Internal Server Error
```json
{
  "message": "Erro interno do servidor: Falha na conexão com o banco de dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/simulacoes/por-produto-ano",
  "errors": null
}
```

```json
{
  "message": "Erro interno do servidor: Falha no processamento dos agrupamentos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/simulacoes",
  "errors": null
}
```

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Descrição | Todos os Endpoints |
|--------|------|-----------|-------------------|
| **200** | OK | Consulta bem-sucedida | ✅ |
| **401** | Unauthorized | Token ausente/inválido | ✅ |
| **403** | Forbidden | Role não autorizada (não-ADMIN) | ✅ |
| **500** | Internal Server Error | Erro interno/banco | ✅ |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`
- `"Token JWT inválido ou expirado"`
- `"Credenciais inválidas"`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado: apenas usuários com role ADMIN podem acessar este recurso"`
- `"Token JWT não encontrado"`
- `"Acesso negado: role não autorizada"`

#### 🔴 Erros Internos (500)
- `"Erro interno do servidor: Falha na conexão com o banco de dados"`
- `"Erro interno do servidor: Falha no processamento dos agrupamentos"`
- `"Erro interno do servidor: Timeout na consulta ao banco"`

---

## Características Técnicas

### Algoritmos de Agrupamento

#### Filtragem de Dados
```java
// Remove simulações com dados inconsistentes
simulacoes.stream()
    .filter(s -> s != null && 
                 s.getProduto() != null && 
                 s.getDataSimulacao() != null)
```

#### Cálculos Estatísticos
```java
// Média com precisão decimal
BigDecimal media = quantidadeSimulacoes > 0 ? 
    soma.divide(BigDecimal.valueOf(quantidadeSimulacoes), 2, RoundingMode.HALF_UP) :
    BigDecimal.ZERO;
```

#### Estratégia de Ordenação
1. **Primeira ordenação**: Nome do produto (alfabética crescente)
2. **Segunda ordenação**: Período temporal (cronológica crescente)
3. **Tie-breaking**: Por ID da simulação quando necessário

### Performance e Otimização

#### Consultas ao Banco
- **Estratégia**: Uma única consulta `listAll()` por endpoint
- **Processamento**: Agrupamento em memória via Streams API
- **Complexidade**: O(n log n) devido à ordenação

#### Uso de Memória
- **Armazenamento temporário**: Mapas hierárquicos para agrupamento
- **Streams**: Processamento lazy quando possível
- **Garbage Collection**: Estruturas temporárias descartadas automaticamente

#### Recomendações de Performance
1. **Paginação**: Considerar implementar para grandes volumes
2. **Cache**: Implementar cache Redis para consultas frequentes
3. **Índices**: Otimizar índices na tabela simulacao_investimento
4. **Filtragem SQL**: Mover filtros para queries SQL quando possível

### Dependências do Sistema

- **SimulacaoInvestimento**: Model/Entity principal
- **Panache ORM**: Framework de persistência
- **JAX-RS**: REST API framework
- **Java Streams API**: Processamento funcional
- **BigDecimal**: Precisão monetária
- **Java Time API**: Manipulação de datas (LocalDate, YearMonth, Year)

---

## Exemplos de Uso

### cURL

**Listar todas simulações:**
```bash
curl -X GET http://localhost:9090/simulacoes \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```

**Agrupamento por produto e dia:**
```bash
curl -X GET http://localhost:9090/simulacoes/por-produto-dia \
  -H "Authorization: Bearer {admin_token}"
```

**Agrupamento por produto e mês:**
```bash
curl -X GET http://localhost:9090/simulacoes/por-produto-mes \
  -H "Authorization: Bearer {admin_token}"
```

**Agrupamento por produto e ano:**
```bash
curl -X GET http://localhost:9090/simulacoes/por-produto-ano \
  -H "Authorization: Bearer {admin_token}"
```

### Respostas Típicas

**Lista de simulações (vazia):**
```json
[]
```

**Agrupamento sem dados:**
```json
[]
```

**Erro de autorização:**
```json
{
  "message": "Acesso negado: apenas usuários com role ADMIN podem acessar este recurso",
  "status": 403
}
```



---

## Casos de Uso Típicos

### 1. Dashboard Administrativo
**Cenário**: Visão geral das simulações da plataforma
```
GET /simulacoes → Lista completa
GET /simulacoes/por-produto-mes → Tendências mensais
```

### 2. Análise de Produtos
**Cenário**: Identificar produtos mais simulados
```
GET /simulacoes/por-produto-dia → Detalhamento diário
GET /simulacoes/por-produto-ano → Comparação anual
```

### 3. Relatórios Executivos
**Cenário**: KPIs e métricas de negócio
```
GET /simulacoes/por-produto-mes → Volume mensal por produto
GET /simulacoes/por-produto-ano → Crescimento anual
```

### 4. Auditoria e Compliance
**Cenário**: Rastreabilidade completa
```
GET /simulacoes → Histórico completo para auditoria
```

---

## Considerações de Segurança

### Controle de Acesso Rigoroso
1. **Role exclusiva**: Apenas ADMIN pode acessar
2. **Dados sensíveis**: Informações financeiras de todos os clientes
3. **Auditoria**: Logs de acesso necessários
4. **Monitoramento**: Alertas para acessos suspeitos

### Proteção de Dados
1. **LGPD/GDPR**: Dados pessoais de investimento
2. **Anonimização**: Considerar remover clienteId em alguns cenários
3. **Retenção**: Políticas de armazenamento de longo prazo
4. **Backup**: Estratégias de backup e recovery

### Recomendações de Segurança
1. **Rate Limiting**: Limitar consultas por administrador
2. **Logs de Auditoria**: Registrar todos os acessos
3. **Filtros por Período**: Evitar consultas de grandes volumes
4. **IP Whitelist**: Restringir acesso por localização
5. **Two-Factor Auth**: Autenticação dupla para ADMINs

---

## Análise de Dados e Insights

### Métricas Disponíveis

#### Por Simulação Individual
- Valor investido e projeção final
- Prazo de investimento (dias/meses/anos)
- Produto escolhido
- Cliente realizador
- Timestamp da simulação

#### Por Agrupamento (Dia/Mês/Ano)
- Quantidade total de simulações
- Valor médio investido
- Valor médio final projetado
- Distribuição por produto
- Tendências temporais