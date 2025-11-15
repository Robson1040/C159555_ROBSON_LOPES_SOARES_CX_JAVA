# Exemplo de teste do endpoint GET /produtos-recomendados/{perfil}

## Cenários de teste

### 1. Perfil Conservador
**Request:**
```http
GET /produtos-recomendados/Conservador
Content-Type: application/json
```

**Expected Response (200):**
```json
[
  {
    "id": 1,
    "nome": "Produto de Renda Fixa Conservador",
    "tipo": "RENDA_FIXA",
    "rentabilidadeEsperada": 5.5,
    "risco": "BAIXO"
  },
  {
    "id": 2,
    "nome": "Outro Produto de Baixo Risco",
    "tipo": "RENDA_FIXA",
    "rentabilidadeEsperada": 4.2,
    "risco": "BAIXO"
  }
]
```

### 2. Perfil Moderado
**Request:**
```http
GET /produtos-recomendados/Moderado
Content-Type: application/json
```

**Expected Response (200):**
```json
[
  {
    "id": 3,
    "nome": "Produto de Renda Fixa Moderado",
    "tipo": "RENDA_FIXA",
    "rentabilidadeEsperada": 7.8,
    "risco": "MEDIO"
  }
]
```

### 3. Perfil Agressivo
**Request:**
```http
GET /produtos-recomendados/Agressivo
Content-Type: application/json
```

**Expected Response (200):**
```json
[
  {
    "id": 4,
    "nome": "Produto de Renda Variável",
    "tipo": "RENDA_VARIAVEL",
    "rentabilidadeEsperada": 12.5,
    "risco": "ALTO"
  }
]
```

### 4. Perfil inválido
**Request:**
```http
GET /produtos-recomendados/PerfilInvalido
Content-Type: application/json
```

**Expected Response (400):**
```json
{
  "message": "Perfil não reconhecido: PerfilInvalido. Perfis válidos: Conservador, Moderado, Agressivo"
}
```

### 5. Nenhum produto encontrado
**Request:**
```http
GET /produtos-recomendados/Conservador
Content-Type: application/json
```

**Expected Response (204):**
```
(corpo vazio)
```

## Mapeamento de Perfis para Riscos

| Perfil do Cliente | Nível de Risco | Tipo de Produto | FGC |
|-------------------|----------------|-----------------|-----|
| Conservador       | BAIXO          | RENDA_FIXA      | Sim |
| Moderado          | MEDIO          | RENDA_FIXA      | Não |
| Agressivo         | ALTO           | RENDA_VARIAVEL  | Não |

## Comandos cURL para teste

```bash
# Perfil Conservador
curl -X GET http://localhost:8080/produtos-recomendados/Conservador \
     -H "Content-Type: application/json"

# Perfil Moderado
curl -X GET http://localhost:8080/produtos-recomendados/Moderado \
     -H "Content-Type: application/json"

# Perfil Agressivo
curl -X GET http://localhost:8080/produtos-recomendados/Agressivo \
     -H "Content-Type: application/json"

# Perfil inválido
curl -X GET http://localhost:8080/produtos-recomendados/InvalidProfile \
     -H "Content-Type: application/json"
```

## Estrutura da resposta

Cada produto na resposta contém:
- `id`: Identificador único do produto
- `nome`: Nome descritivo do produto
- `tipo`: Tipo do produto (RENDA_FIXA, RENDA_VARIAVEL)
- `rentabilidadeEsperada`: Taxa de rentabilidade esperada
- `risco`: Nível de risco (BAIXO, MEDIO, ALTO)

## Códigos de status HTTP

- `200 OK`: Produtos encontrados e retornados
- `204 No Content`: Nenhum produto encontrado para o perfil
- `400 Bad Request`: Perfil inválido fornecido
- `500 Internal Server Error`: Erro interno do servidor