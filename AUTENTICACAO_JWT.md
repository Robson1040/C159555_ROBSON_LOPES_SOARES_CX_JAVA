# Sistema de Autenticação JWT - API VEICULO SSO

## Endpoint de Autenticação

### POST /entrar

**Descrição:** Autentica um usuário e retorna um token JWT válido por 1 hora.

**Request:**
```json
{
    "username": "joao.silva",
    "password": "minhasenha123"
}
```

**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tipo": "Bearer",
    "expira_em": "2025-11-14T11:42:00",
    "usuario": "joao.silva",
    "role": "USER"
}
```

**Response (401 Unauthorized):**
```json
{
    "message": "Credenciais inválidas",
    "timestamp": "2025-11-14T10:42:00",
    "status": 401,
    "path": "/entrar",
    "errors": null
}
```

## Token JWT

O token JWT contém as seguintes informações:

- **sub**: Username do usuário
- **nome**: Nome completo do usuário  
- **cpf**: CPF do usuário
- **userId**: ID único do usuário
- **role**: Papel/função do usuário (USER, ADMIN, etc.)
- **iat**: Timestamp de criação
- **exp**: Timestamp de expiração (1 hora após criação)
- **iss**: Emissor do token (api-investimentos-caixa)

## Como usar

1. **Criar um usuário via endpoint de clientes:**
```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "12345678901",
    "username": "joao.silva",
    "password": "minhasenha123",
    "role": "USER"
  }'
```

2. **Fazer login:**
```bash
curl -X POST http://localhost:8080/entrar \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao.silva",
    "password": "minhasenha123"
  }'
```

3. **Usar o token (futuramente para RBAC):**
```bash
curl -X GET http://localhost:8080/algum-endpoint \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Notas Técnicas

- **Algoritmo**: HS256 (HMAC SHA-256)
- **Validade**: 1 hora
- **Chave secreta**: Configurável via variável de ambiente
- **Formato**: JWT padrão (header.payload.signature)

## Segurança

- Passwords são criptografadas com SHA-256 + salt
- Token contém informações não-sensíveis
- Chave de assinatura deve ser mantida segura em produção
- Por enquanto, não há validação automática de token nos endpoints (implementação futura para RBAC)

## Próximos Passos

- Implementação de middleware para validação automática de tokens
- Sistema de roles e permissões (RBAC)
- Refresh tokens
- Logout e invalidação de tokens