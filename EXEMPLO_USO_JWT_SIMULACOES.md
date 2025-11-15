# Exemplo de Uso - Endpoints de Simulação com JWT

## Simulações de Investimento com Controle de Acesso

Os seguintes endpoints agora implementam controle de acesso baseado em JWT:

- `GET /simular-investimento/historico/{clienteId}` - Buscar histórico de simulações
- `GET /simular-investimento/{id}` - Buscar simulação por ID
- `GET /simular-investimento/estatisticas/{clienteId}` - Buscar estatísticas do cliente

## Regras de Autorização

### ADMIN
- Pode acessar dados de **qualquer cliente**
- Possui acesso total a todas as simulações

### USER
- Pode acessar **apenas seus próprios dados**
- O `clienteId` do endpoint deve corresponder ao `userId` do token JWT
- Simulações só são retornadas se pertencerem ao usuário logado

## Exemplos de Uso

### 1. Login e Obtenção do Token

```bash
# Login como USER
curl -X POST http://localhost:9090/entrar \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao.silva",
    "password": "minhasenha123"
  }'

# Resposta:
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiraEm": "2025-11-15T12:30:00",
  "usuario": "joao.silva",
  "role": "USER"
}
```

### 2. Acessar Histórico de Simulações (USER)

```bash
# USER pode acessar apenas suas próprias simulações
# Se o userId no JWT for 5, ele só pode acessar clienteId=5
curl -X GET http://localhost:9090/simular-investimento/historico/5 \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"

# ✅ Sucesso: retorna as simulações do usuário
# ❌ Erro se tentar acessar outro clienteId:
curl -X GET http://localhost:9090/simular-investimento/historico/10 \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
# Retorna: "Acesso negado: usuário só pode acessar seus próprios dados"
```

### 3. Acessar Estatísticas (USER)

```bash
# USER pode acessar apenas suas próprias estatísticas
curl -X GET http://localhost:9090/simular-investimento/estatisticas/5 \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

### 4. Buscar Simulação por ID (USER)

```bash
# USER pode acessar apenas simulações que pertencem a ele
curl -X GET http://localhost:9090/simular-investimento/123 \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"

# Se a simulação 123 não pertencer ao usuário logado:
# Retorna: "Acesso negado: usuário só pode acessar seus próprios dados"
```

### 5. Acesso como ADMIN

```bash
# Login como ADMIN
curl -X POST http://localhost:9090/entrar \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# ADMIN pode acessar dados de qualquer cliente
curl -X GET http://localhost:9090/simular-investimento/historico/1 \
  -H "Authorization: Bearer TOKEN_ADMIN_AQUI"

curl -X GET http://localhost:9090/simular-investimento/historico/5 \
  -H "Authorization: Bearer TOKEN_ADMIN_AQUI"

curl -X GET http://localhost:9090/simular-investimento/estatisticas/10 \
  -H "Authorization: Bearer TOKEN_ADMIN_AQUI"
```

## Formato do Token JWT

O token JWT contém as seguintes informações relevantes para autorização:

```json
{
  "sub": "joao.silva",
  "nome": "João Silva",
  "cpf": "12345678901",
  "userId": 5,
  "groups": ["USER"],
  "iat": 1701234567,
  "exp": 1701238167,
  "iss": "api-investimentos-caixa"
}
```

## Erros Comuns

### Token Ausente
```bash
curl -X GET http://localhost:9090/simular-investimento/historico/5
# Retorna 401: Token JWT requerido
```

### Token Inválido
```bash
curl -X GET http://localhost:9090/simular-investimento/historico/5 \
  -H "Authorization: Bearer token-invalido"
# Retorna 401: Token JWT inválido
```

### Acesso Negado (USER tentando acessar dados de outro)
```bash
# Se userId no JWT = 5, mas tenta acessar clienteId = 10
curl -X GET http://localhost:9090/simular-investimento/historico/10 \
  -H "Authorization: Bearer TOKEN_USER_ID_5"
# Retorna 403: "Acesso negado: usuário só pode acessar seus próprios dados"
```

### Role Não Autorizada
```bash
# Se o token não contém role USER nem ADMIN
# Retorna 403: "Acesso negado: role não autorizada"
```

## Implementação Técnica

A validação é realizada através do método `validarAcessoAoCliente(Long clienteId)` que:

1. **Verifica se é ADMIN**: Se `jwt.getGroups().contains("ADMIN")`, permite acesso total
2. **Verifica se é USER**: Se `jwt.getGroups().contains("USER")`, compara o `userId` do JWT com o `clienteId` solicitado
3. **Nega acesso**: Se não tem role válida ou USER tenta acessar dados de outro cliente

## Próximos Passos

- Implementar a mesma validação em outros endpoints que manipulam dados sensíveis
- Adicionar logs de auditoria para tentativas de acesso não autorizadas
- Implementar rate limiting por usuário