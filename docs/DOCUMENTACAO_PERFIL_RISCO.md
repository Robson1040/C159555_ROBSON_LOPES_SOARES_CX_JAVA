# Documentação da API - PerfilRiscoResource

## Visão Geral

O `PerfilRiscoResource` é responsável por calcular e fornecer o perfil de risco de clientes baseado em seu histórico de investimentos e simulações. Utiliza algoritmos de Machine Learning para analisar o comportamento do cliente e determinar seu perfil (Conservador, Moderado ou Agressivo).

**Servidor:** `http://localhost:9090`

**Base Path:** `/perfil-risco`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

**Autenticação global:**
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

---

## Endpoints

### 1. GET /perfil-risco/{clienteId}
**Calcula o perfil de risco de um cliente**

#### Descrição
Analisa o histórico de investimentos e simulações de um cliente para determinar seu perfil de risco. O cálculo utiliza Machine Learning baseado nos produtos mais utilizados pelo cliente, considerando primeiro investimentos reais e, na ausência destes, simulações realizadas.

#### Algoritmo de Cálculo
1. **Prioridade 1**: Histórico de investimentos reais
2. **Prioridade 2**: Histórico de simulações (se não houver investimentos)
3. **Erro**: Se não houver nenhum histórico

**Perfis possíveis:**
- **CONSERVADOR**: Focado em produtos de baixo risco
- **MODERADO**: Equilibrio entre risco e retorno 
- **AGRESSIVO**: Tolerância a alto risco para maior rentabilidade

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`

#### Request

**URL:** `GET /perfil-risco/{clienteId}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `clienteId` | Long | Sim | ID único do cliente para análise |

**Exemplo:** `GET /perfil-risco/1`

#### Responses

##### ✅ 200 - Perfil calculado com sucesso

**Response Body:**
```json
{
  "clienteId": 1,
  "perfil": "MODERADO",
  "pontuacao": 65,
  "descricao": "Perfil equilibrado entre segurança e rentabilidade."
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `clienteId` | Long | ID do cliente analisado |
| `perfil` | string | Perfil de risco: "CONSERVADOR", "MODERADO", "AGRESSIVO" |
| `pontuacao` | integer | Pontuação calculada (0-100) baseada no algoritmo |
| `descricao` | string | Descrição detalhada do perfil |

**Exemplos por Perfil:**

**Perfil Conservador:**
```json
{
  "clienteId": 1,
  "perfil": "CONSERVADOR", 
  "pontuacao": 25,
  "descricao": "Perfil focado em segurança e liquidez, com baixa tolerância ao risco."
}
```

**Perfil Moderado:**
```json
{
  "clienteId": 2,
  "perfil": "MODERADO",
  "pontuacao": 55,
  "descricao": "Perfil equilibrado entre segurança e rentabilidade."
}
```

**Perfil Agressivo:**
```json
{
  "clienteId": 3,
  "perfil": "AGRESSIVO",
  "pontuacao": 85,
  "descricao": "Perfil voltado para alta rentabilidade, com maior tolerância ao risco."
}
```

---

## Status Codes e Erros por Endpoint

### GET /perfil-risco/{clienteId}

#### Status Codes Possíveis
| Status | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Perfil calculado com sucesso |
| **400** | Bad Request | Cliente sem histórico suficiente |
| **401** | Unauthorized | Token ausente/inválido |
| **403** | Forbidden | Token válido mas sem permissão |
| **404** | Not Found | Cliente não encontrado |
| **500** | Internal Server Error | Erro interno do sistema |

#### Mensagens de Erro Possíveis

##### ❌ 400 - Bad Request
```json
{
  "message": "Cliente não possui histórico de investimentos nem simulações para calcular perfil de risco"
}
```

**Quando ocorre:**
- Cliente existe mas não tem investimentos
- Cliente existe mas não tem simulações
- Histórico insuficiente para cálculo

##### ❌ 401 - Unauthorized
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/perfil-risco/1",
  "errors": null
}
```

**Quando ocorre:**
- Header Authorization ausente
- Token JWT inválido ou expirado
- Token malformado

##### ❌ 403 - Forbidden
```json
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/perfil-risco/1",
  "errors": null
}
```

**Quando ocorre:**
- Token válido mas role não autorizada
- User tentando acessar dados de outro cliente (sem validação atual)

##### ❌ 404 - Not Found
```json
{
  "message": "Cliente não encontrado"
}
```

**Quando ocorre:**
- ID de cliente não existe no sistema
- Cliente foi removido do banco

##### ❌ 500 - Internal Server Error
```json
{
  "message": "Erro interno no servidor"
}
```

**Quando ocorre:**
- Falha na conexão com banco de dados
- Erro no algoritmo de ML
- Falha no processamento interno
- Exception não tratada

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Consolidados

| Status | Nome | Descrição | Cenários |
|--------|------|-----------|----------|
| **200** | OK | Perfil calculado | Cliente com histórico suficiente |
| **400** | Bad Request | Dados insuficientes | Sem histórico de investimentos/simulações |
| **401** | Unauthorized | Token inválido | Não autenticado |
| **403** | Forbidden | Sem permissão | Role não autorizada |
| **404** | Not Found | Cliente não existe | ID inválido |
| **500** | Internal Server Error | Erro interno | Falhas do sistema |

### Categorias de Mensagens de Erro

#### 🔴 Erros de Validação (400)
- `"Cliente não possui histórico de investimentos nem simulações para calcular perfil de risco"`

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado. Você não possui permissão para acessar este recurso."`

#### 🔴 Erros de Recurso (404)
- `"Cliente não encontrado"`
- `"Cliente não encontrado com ID: {clienteId}"`

#### 🔴 Erros Internos (500)
- `"Erro interno no servidor"`

### Estrutura de Erros

**Erros simples (400, 404, 500):**
```json
{
  "message": "Mensagem do erro"
}
```

**Erros de framework (401, 403):**
```json
{
  "message": "Mensagem detalhada",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/perfil-risco/1",
  "errors": null
}
```

---

## Algoritmo de Cálculo de Perfil

### Etapas do Processo

1. **Validação do Cliente**
   - Verifica se o cliente existe no sistema
   - Retorna 404 se não encontrado

2. **Coleta de Dados**
   - **Prioridade 1**: Investimentos reais do cliente
   - **Prioridade 2**: Simulações realizadas (se não há investimentos)

3. **Análise via Machine Learning**
   - Utiliza `GeradorRecomendacaoML` para análise
   - Ordena produtos por aparições no histórico
   - Considera níveis de risco dos produtos

4. **Cálculo da Pontuação**
   - Baseado na frequência de produtos por nível de risco
   - Pontuação de 0 a 100 points
   - Fórmula: `(aparições_nivel_dominante * 100) / total_pontuações`

5. **Determinação do Perfil**
   - **CONSERVADOR**: Maioria produtos de risco BAIXO
   - **MODERADO**: Maioria produtos de risco MÉDIO  
   - **AGRESSIVO**: Maioria produtos de risco ALTO

### Níveis de Risco dos Produtos

| Nível | Enum | Características |
|-------|------|----------------|
| **BAIXO** | `NivelRisco.BAIXO` | Produtos garantidos pelo FGC |
| **MÉDIO** | `NivelRisco.MEDIO` | Renda fixa não garantida pelo FGC |
| **ALTO** | `NivelRisco.ALTO` | Renda variável, sem garantias |

### Casos Especiais

- **Sem histórico**: Retorna erro 400
- **Histórico misto**: Perfil determinado pela maioria
- **Empate**: Sistema prioriza o primeiro produto na ordenação

---

## Informações Técnicas

### Autenticação e Autorização

**Roles aceitas:**
- `USER`: Pode calcular perfil (sem validação de propriedade atual)
- `ADMIN`: Acesso irrestrito a qualquer cliente

**Token JWT deve conter:**
- Role válida (`USER` ou `ADMIN`)
- Token não expirado
- Assinatura válida

### Dependências do Cálculo

O endpoint depende de:
- **ClienteService**: Validação da existência do cliente
- **InvestimentoRepository**: Histórico de investimentos reais
- **SimulacaoRepository**: Histórico de simulações
- **ProdutoRepository**: Informações dos produtos e riscos
- **GeradorRecomendacaoML**: Algoritmo de machine learning

### Performance

- Consultas ao banco para histórico
- Processamento ML pode ser intensivo
- Cache não implementado (recalcula sempre)
- Complexidade cresce com volume de histórico

---

## Exemplos de Uso

### cURL

**Calcular perfil de cliente específico:**
```bash
curl -X GET http://localhost:9090/perfil-risco/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

**Exemplo com cliente sem histórico:**
```bash
curl -X GET http://localhost:9090/perfil-risco/999 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### Respostas Esperadas

**Cliente com perfil conservador:**
```bash
HTTP/1.1 200 OK
Content-Type: application/json

{
  "clienteId": 1,
  "perfil": "CONSERVADOR",
  "pontuacao": 30,
  "descricao": "Perfil focado em segurança e liquidez, com baixa tolerância ao risco."
}
```

**Cliente não encontrado:**
```bash
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Cliente não encontrado"
}
```

**Cliente sem histórico:**
```bash
HTTP/1.1 400 Bad Request 
Content-Type: application/json

{
  "message": "Cliente não possui histórico de investimentos nem simulações para calcular perfil de risco"
}
```


