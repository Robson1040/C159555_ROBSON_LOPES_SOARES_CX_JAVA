# Exemplo de Resposta do Endpoint GET /simulacoes

## Endpoint: GET /simulacoes

**Descrição:** Retorna todas as simulações de investimento realizadas no sistema.

**HTTP Method:** GET
**URL:** `/simulacoes`
**Content-Type:** application/json

---

### Exemplo de Resposta (200 OK):

```json
[
  {
    "id": 1,
    "clienteId": 123,
    "produto": "CDB Banco XYZ",
    "valorInvestido": 10000.00,
    "valorFinal": 11200.00,
    "prazoMeses": 12,
    "prazoDias": null,
    "prazoAnos": null,
    "dataSimulacao": "2025-11-14T14:30:00"
  },
  {
    "id": 2,
    "clienteId": 456,
    "produto": "Tesouro IPCA+ 2030",
    "valorInvestido": 5000.00,
    "valorFinal": 5320.50,
    "prazoMeses": null,
    "prazoDias": 180,
    "prazoAnos": null,
    "dataSimulacao": "2025-11-14T15:45:00"
  },
  {
    "id": 3,
    "clienteId": 789,
    "produto": "LCI Pré-fixada",
    "valorInvestido": 25000.00,
    "valorFinal": 27850.00,
    "prazoMeses": null,
    "prazoDias": null,
    "prazoAnos": 2,
    "dataSimulacao": "2025-11-14T16:20:00"
  }
]
```

### Caso sem simulações (200 OK):

```json
[]
```

---

### Campos da Resposta:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único da simulação |
| `clienteId` | Long | ID do cliente que realizou a simulação |
| `produto` | String | Nome do produto de investimento utilizado |
| `valorInvestido` | BigDecimal | Valor inicial investido |
| `valorFinal` | BigDecimal | Valor final projetado da simulação |
| `prazoMeses` | Integer | Prazo em meses (se foi informado) |
| `prazoDias` | Integer | Prazo em dias (se foi informado) |
| `prazoAnos` | Integer | Prazo em anos (se foi informado) |
| `dataSimulacao` | LocalDateTime | Data/hora em que a simulação foi realizada |

**Nota:** Apenas um dos campos de prazo (meses, dias ou anos) terá valor, os outros serão `null`.

---

## Endpoint: GET /simulacoes/por-produto-dia

**Descrição:** Retorna agrupamento das simulações por produto e data com estatísticas agregadas.

**HTTP Method:** GET
**URL:** `/simulacoes/por-produto-dia`
**Content-Type:** application/json

---

### Exemplo de Resposta (200 OK):

```json
[
  {
    "produto": "CDB Caixa 2026",
    "data": "2025-10-30",
    "quantidadeSimulacoes": 15,
    "mediaValorInvestido": 12500.00,
    "mediaValorFinal": 13750.00
  },
  {
    "produto": "Fundo XPTO",
    "data": "2025-10-30",
    "quantidadeSimulacoes": 8,
    "mediaValorInvestido": 8000.00,
    "mediaValorFinal": 8640.00
  },
  {
    "produto": "CDB Caixa 2026",
    "data": "2025-10-31",
    "quantidadeSimulacoes": 12,
    "mediaValorInvestido": 15000.00,
    "mediaValorFinal": 16500.00
  }
]
```

### Caso sem simulações (200 OK):

```json
[]
```

---

### Campos da Resposta do Agrupamento:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `produto` | String | Nome do produto de investimento |
| `data` | LocalDate | Data das simulações (formato: YYYY-MM-DD) |
| `quantidadeSimulacoes` | Integer | Número total de simulações do produto na data |
| `mediaValorInvestido` | BigDecimal | Média dos valores investidos |
| `mediaValorFinal` | BigDecimal | Média dos valores finais projetados |

---

### Características do Agrupamento:

- **Agrupamento:** Por produto E por data (sem considerar horário)
- **Ordenação:** Primeiro por produto (alfabética), depois por data (crescente)
- **Cálculos:** Médias calculadas com precisão de 2 casas decimais
- **Formato da data:** Apenas a data (YYYY-MM-DD), ignorando o horário