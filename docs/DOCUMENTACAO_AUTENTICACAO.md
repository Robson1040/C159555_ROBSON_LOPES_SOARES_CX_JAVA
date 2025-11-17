# Documentação da API - AutenticacaoResource

## Visão Geral

O `AutenticacaoResource` é responsável por gerenciar a autenticação de usuários na API de Investimentos Caixa. Atualmente possui apenas um endpoint para login de usuários.

**Servidor:** `http://localhost:9090`

**Base Path:** `/`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

---

## Endpoints

### 1. POST /entrar
**Realiza login de usuário no sistema**

#### Descrição
Autentica um usuário através de username e password, retornando um token JWT válido por 1 hora com as informações do usuário autenticado.

#### Autenticação
- **Token JWT:** NÃO OBRIGATÓRIO
- **Roles permitidas:** `@PermitAll` (acesso público)

#### Request

**URL:** `POST /entrar`

**Headers obrigatórios:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "username": "string",
  "password": "string"
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `username` | string | Sim | `@NotBlank` | Nome de usuário para autenticação |
| `password` | string | Sim | `@NotBlank` | Senha do usuário |

**Exemplo de Request:**
```json
{
  "username": "joao123",
  "password": "minhasenha123"
}
```

#### Responses

##### ✅ 200 - Login realizado com sucesso

**Descrição:** Usuário autenticado com sucesso. Retorna token JWT válido.

**Response Body:**
```json
{
  "token": "string",
  "tipo": "Bearer",
  "expira_em": "2025-11-15T15:30:00",
  "usuario": "string", 
  "role": "string"
}
```

**Campos da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `token` | string | Token JWT para autenticação em endpoints protegidos |
| `tipo` | string | Tipo do token (sempre "Bearer") |
| `expira_em` | datetime | Data e hora de expiração do token (1 hora após criação) |
| `usuario` | string | Username do usuário autenticado |
| `role` | string | Role do usuário ("USER" ou "ADMIN") |

**Exemplo de Response 200:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcGktaW52ZXN0aW1lbnRvcy1jYWl4YSIsInN1YiI6ImpvYW8xMjMiLCJncm91cHMiOlsiVVNFUiJdLCJub21lIjoiSm9hbyBTaWx2YSIsImNwZiI6IjEyMzQ1Njc4OTAwIiwidXNlcklkIjoxLCJleHAiOjE3MDE3ODk2MDB9.signature",
  "tipo": "Bearer",
  "expira_em": "2025-11-15T15:30:00",
  "usuario": "joao123",
  "role": "USER"
}
```

---

##### ❌ 400 - Bad Request

**Descrição:** Dados de entrada inválidos ou malformados.

**Possíveis causas:**
- Campos obrigatórios não informados
- JSON malformado
- Validação `@NotBlank` falhou

**Response Body:**
```json
{
  "message": "string",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/entrar",
  "errors": ["string"]
}
```

**Exemplo de Response 400:**
```json
{
  "message": "Dados de entrada inválidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/entrar",
  "errors": [
    "Username é obrigatório",
    "Password é obrigatório"
  ]
}
```

---

##### ❌ 401 - Unauthorized

**Descrição:** Credenciais inválidas (username ou password incorretos).

**Response Body:**
```json
{
  "message": "Credenciais inválidas",
  "timestamp": "2025-11-15T14:30:00", 
  "status": 401,
  "path": "/entrar",
  "errors": null
}
```

**Exemplo de Response 401:**
```json
{
  "message": "Credenciais inválidas",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/entrar",
  "errors": null
}
```

---

##### ❌ 500 - Internal Server Error

**Descrição:** Erro interno do servidor durante o processamento.

**Possíveis causas:**
- Falha na conexão com banco de dados
- Erro no serviço de geração de token
- Erro não tratado na aplicação

**Response Body:**
```json
{
  "message": "Erro interno do servidor: {detalhes do erro}",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/entrar", 
  "errors": null
}
```

**Exemplo de Response 500:**
```json
{
  "message": "Erro interno do servidor: Falha na conexão com o banco de dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/entrar",
  "errors": null
}
```

---

## Informações Técnicas

### Token JWT

O token JWT gerado contém as seguintes informações:

**Claims padrão:**
- `iss` (issuer): "api-investimentos-caixa"
- `sub` (subject): username do usuário
- `groups`: array com a role do usuário
- `exp` (expiration): timestamp de expiração (1 hora)

**Claims customizados:**
- `nome`: nome completo do usuário
- `cpf`: CPF do usuário
- `userId`: ID único do usuário no sistema

**Exemplo de payload decodificado:**
```json
{
  "iss": "api-investimentos-caixa",
  "sub": "joao123", 
  "groups": ["USER"],
  "nome": "João Silva",
  "cpf": "12345678900",
  "userId": 1,
  "exp": 1701789600
}
```

### Uso do Token

Para usar o token em endpoints protegidos, inclua no header:
```
Authorization: Bearer {token}
```

### Roles Disponíveis

- **USER**: Usuário comum (pode acessar seus próprios dados)
- **ADMIN**: Administrador (acesso completo ao sistema)

---

## Exemplos de Uso

### cURL

**Login bem-sucedido:**
```bash
curl -X POST http://localhost:9090/entrar \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao123",
    "password": "minhasenha123"
  }'
```

**Login com credenciais inválidas:**
```bash
curl -X POST http://localhost:9090/entrar \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario_inexistente", 
    "password": "senha_errada"
  }'
```

---

## Considerações de Segurança

1. **Criptografia de Senhas**: As senhas são criptografadas no banco usando BCrypt
2. **Validação de Token**: Tokens são assinados com chave RSA privada
3. **Expiração**: Tokens expiram em 1 hora para limitar janela de exposição
4. **Não exposição**: Senhas nunca são retornadas nas responses
5. **Rate Limiting**: Considere implementar rate limiting para prevenir ataques de força bruta

---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Possíveis

| Status | Nome | Descrição | Quando Ocorre |
|--------|------|-----------|---------------|
| **200** | OK | Sucesso | Login realizado com sucesso |
| **400** | Bad Request | Dados inválidos | JSON malformado, validações falharam |
| **401** | Unauthorized | Não autorizado | Credenciais inválidas |
| **500** | Internal Server Error | Erro interno | Falha no servidor/banco |

### Mensagens de Erro por Categoria

#### 🔴 Erros de Validação (400)
**Campos obrigatórios:**
- `"Username é obrigatório"`
- `"Password é obrigatório"`

**Formato JSON:**
- `"Dados da requisição inválidos: formato JSON incorreto"`
- `"Formato JSON inválido"`

**Mensagem genérica de validação:**
- `"Dados inválidos fornecidos"`

#### 🔴 Erros de Autenticação (401)
**Credenciais inválidas:**
- `"Credenciais inválidas"` (username não existe ou senha incorreta)

#### 🔴 Erros Internos (500)
**Falhas do sistema:**
- `"Erro interno do servidor: {detalhes específicos do erro}"`
- `"Erro interno do servidor: Falha na conexão com o banco de dados"`
- `"Erro interno do servidor: Falha na geração do token JWT"`

### Estrutura Completa das Respostas de Erro

Todas as respostas de erro seguem o padrão:

```json
{
  "message": "Mensagem principal do erro",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/entrar",
  "errors": ["Lista de erros específicos (opcional)"]
}
```

### Exemplos Detalhados por Cenário

#### ❌ 400 - Campos Obrigatórios Ausentes
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/entrar",
  "errors": [
    "Campo 'username': Username é obrigatório (valor fornecido: null)",
    "Campo 'password': Password é obrigatório (valor fornecido: null)"
  ]
}
```

#### ❌ 400 - JSON Malformado
```json
{
  "message": "Formato JSON inválido",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/entrar",
  "errors": null
}
```

#### ❌ 401 - Username Não Existe
```json
{
  "message": "Credenciais inválidas",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/entrar",
  "errors": null
}
```

#### ❌ 401 - Senha Incorreta
```json
{
  "message": "Credenciais inválidas",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/entrar",
  "errors": null
}
```

#### ❌ 500 - Falha no Banco de Dados
```json
{
  "message": "Erro interno do servidor: Connection refused to database",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/entrar",
  "errors": null
}
```

#### ❌ 500 - Falha na Geração do Token
```json
{
  "message": "Erro interno do servidor: Private key not found for JWT signing",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/entrar", 
  "errors": null
}
```

---