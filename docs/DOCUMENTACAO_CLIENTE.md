# Documentação da API - ClienteResource

## Visão Geral

O `ClienteResource` é responsável por gerenciar operações no cadastro de clientes na API de Investimentos Caixa. Oferece endpoints para criação, listagem, busca e atualização de clientes com controle de acesso baseado em roles.

**Servidor:** `http://localhost:9090`

**Base Path:** `/clientes`

**Formatos suportados:**
- Content-Type: `application/json`
- Accept: `application/json`

---

## Sumário de Endpoints

- [1. GET /clientes](#1-get-clientes)
- [2. GET /clientes/{id}](#2-get-clientesid)
- [3. POST /clientes](#3-post-clientes)
- [4. PUT /clientes/{id}](#4-put-clientesid)
- [5. GET /clientes/cpf/{cpf}](#5-get-clientescpfcpf)
- [6. GET /clientes/username/{username}](#6-get-clientesusernameusernam)

---

## Endpoints

### 1. GET /clientes
**Lista todos os clientes do sistema**

`http://localhost:9090/clientes`

#### Descrição
Retorna uma lista completa de todos os clientes cadastrados no sistema. Endpoint restrito apenas para administradores.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /clientes`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Sem body**

#### Responses

##### ✅ 200 - Lista recuperada com sucesso

**Response Body:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "cpf": "12345678900", 
    "username": "joao123",
    "role": "USER"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "cpf": "98765432100",
    "username": "maria456", 
    "role": "ADMIN"
  }
]
```

---

### 2. GET /clientes/{id}
**Busca cliente por ID**

`http://localhost:9090/clientes/{id}`

#### Descrição
Retorna os dados de um cliente específico pelo ID. ADMINs podem buscar qualquer cliente, USERs só podem buscar seus próprios dados.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`
- **Autorização:** USERs só podem acessar seus próprios dados (validado via JWT)

#### Request

**URL:** `GET /clientes/{id}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `id` | Long | Sim | ID único do cliente |

**Exemplo:** `GET /clientes/1`

#### Responses

##### ✅ 200 - Cliente encontrado

**Response Body:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "12345678900",
  "username": "joao123", 
  "role": "USER"
}
```

---

### 3. POST /clientes
**Cria um novo cliente**

`http://localhost:9090/clientes`

#### Descrição
Cadastra um novo cliente no sistema. Endpoint público para permitir auto-registro de usuários.

#### Autenticação
- **Token JWT:** NÃO OBRIGATÓRIO
- **Roles permitidas:** `@PermitAll` (acesso público)

#### Request

**URL:** `POST /clientes`

**Headers obrigatórios:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nome": "string",
  "cpf": "string", 
  "username": "string",
  "password": "string",
  "role": "string"
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `nome` | string | Sim | `@NotBlank`, `@Size(min=2, max=100)` | Nome completo do cliente |
| `cpf` | string | Sim | `@NotBlank`, `@ValidCPF` | CPF válido (11 dígitos) |
| `username` | string | Sim | `@NotBlank`, `@Size(min=3, max=50)` | Nome de usuário único |
| `password` | string | Sim | `@NotBlank`, `@Size(min=6)` | Senha (mínimo 6 caracteres) |
| `role` | string | Sim | `@NotBlank`, `@Pattern(USER\|ADMIN)` | Role do usuário |

**Exemplo de Request:**
```json
{
  "nome": "João Silva Santos",
  "cpf": "12345678901",
  "username": "joao_silva",
  "password": "minhasenha123",
  "role": "USER"
}
```

#### Responses

##### ✅ 201 - Cliente criado com sucesso

**Response Body:**
```json
{
  "id": 1,
  "nome": "João Silva Santos",
  "cpf": "12345678901",
  "username": "joao_silva",
  "role": "USER"
}
```

---

### 4. PUT /clientes/{id}
**Atualiza um cliente existente**

`http://localhost:9090/clientes/{id}`

#### Descrição
Atualiza os dados de um cliente específico. USERs só podem atualizar seus próprios dados, ADMINs podem atualizar qualquer cliente.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `USER`, `ADMIN`
- **Autorização:** USERs só podem atualizar seus próprios dados (validado via JWT)

#### Request

**URL:** `PUT /clientes/{id}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `id` | Long | Sim | ID único do cliente a ser atualizado |

**Body (JSON):**
```json
{
  "nome": "string",
  "username": "string",
  "password": "string"
}
```

**Campos do Request:**

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `nome` | string | Não | `@Size(min=2, max=100)` | Nome completo do cliente |
| `username` | string | Não | `@Size(min=3, max=50)` | Nome de usuário único |
| `password` | string | Não | `@Size(min=6)` | Nova senha (mínimo 6 caracteres) |

**Exemplo de Request:**
```json
{
  "nome": "João Silva Santos Jr",
  "username": "joao_silva_jr",
  "password": "novasenha456"
}
```

#### Responses

##### ✅ 200 - Cliente atualizado com sucesso

**Response Body:**
```json
{
  "id": 1,
  "nome": "João Silva Santos Jr",
  "cpf": "12345678901",
  "username": "joao_silva_jr",
  "role": "USER"
}
```

---

### 5. GET /clientes/cpf/{cpf}
**Busca cliente por CPF**

`http://localhost:9090/clientes/cpf/{cpf}`

#### Descrição
Retorna os dados de um cliente específico pelo CPF. Endpoint restrito para administradores apenas.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /clientes/cpf/{cpf}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `cpf` | string | Sim | CPF do cliente (11 dígitos) |

**Exemplo:** `GET /clientes/cpf/12345678901`

#### Responses

##### ✅ 200 - Cliente encontrado

**Response Body:**
```json
{
  "id": 1,
  "nome": "João Silva Santos",
  "cpf": "12345678901",
  "username": "joao_silva",
  "role": "USER"
}
```

---

### 6. GET /clientes/username/{username}
**Busca cliente por username**

`http://localhost:9090/clientes/username/{username}`

#### Descrição
Retorna os dados de um cliente específico pelo username. Endpoint restrito para administradores apenas.

#### Autenticação
- **Token JWT:** OBRIGATÓRIO
- **Roles permitidas:** `ADMIN` apenas

#### Request

**URL:** `GET /clientes/username/{username}`

**Headers obrigatórios:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Path Parameters:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `username` | string | Sim | Username único do cliente |

**Exemplo:** `GET /clientes/username/joao_silva`

#### Responses

##### ✅ 200 - Cliente encontrado

**Response Body:**
```json
{
  "id": 1,
  "nome": "João Silva Santos",
  "cpf": "12345678901", 
  "username": "joao_silva",
  "role": "USER"
}
```

---

## Status Codes e Erros Comuns

### ❌ 400 - Bad Request

**Descrição:** Dados de entrada inválidos ou malformados.

**Possíveis causas:**
- Campos obrigatórios não informados
- Validações falharam (tamanho, formato, etc.)
- JSON malformado
- CPF inválido
- Role inválida (deve ser USER ou ADMIN)

**Response Body:**
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/clientes",
  "errors": [
    "Nome é obrigatório",
    "CPF inválido",
    "Username deve ter entre 3 e 50 caracteres",
    "Password deve ter no mínimo 6 caracteres",
    "Role deve ser 'USER' ou 'ADMIN'"
  ]
}
```

---

### ❌ 401 - Unauthorized

**Descrição:** Token JWT não fornecido ou inválido.

**Possíveis causas:**
- Header Authorization ausente
- Token JWT expirado
- Token JWT inválido ou malformado

**Response Body:**
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/clientes/1",
  "errors": null
}
```

---

### ❌ 403 - Forbidden

**Descrição:** Token válido mas sem permissão para a operação.

**Possíveis causas:**
- USER tentando acessar dados de outro cliente
- USER tentando acessar endpoints de ADMIN
- Falha na validação de autorização

**Response Body:**
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/clientes/2",
  "errors": null
}
```

---

### ❌ 404 - Not Found

**Descrição:** Cliente não encontrado.

**Possíveis causas:**
- ID de cliente inexistente
- CPF não cadastrado no sistema
- Username não encontrado

**Response Body:**
```json
{
  "message": "Cliente não encontrado",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/clientes/999",
  "errors": null
}
```

---

### ❌ 409 - Conflict

**Descrição:** Dados já existem no sistema.

**Possíveis causas:**
- CPF já cadastrado
- Username já existe
- Tentativa de duplicação de dados únicos

**Response Body:**
```json
{
  "message": "CPF ou username já cadastrado no sistema",
  "timestamp": "2025-11-15T14:30:00",
  "status": 409,
  "path": "/clientes",
  "errors": null
}
```

---

### ❌ 500 - Internal Server Error

**Descrição:** Erro interno do servidor.

**Possíveis causas:**
- Falha na conexão com banco de dados
- Erro não tratado na aplicação
- Falha na criptografia de senhas

**Response Body:**
```json
{
  "message": "Erro interno do servidor: {detalhes do erro}",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/clientes",
  "errors": null
}
```

---

## Matriz de Autorização

| Endpoint | Método | ADMIN | USER | Público | Observações |
|----------|--------|-------|------|---------|-------------|
| `/clientes` | GET | ✅ | ❌ | ❌ | Lista todos os clientes |
| `/clientes/{id}` | GET | ✅ | ✅* | ❌ | *USER só seus próprios dados |
| `/clientes` | POST | ✅ | ✅ | ✅ | Criação de conta é pública |
| `/clientes/{id}` | PUT | ✅ | ✅* | ❌ | *USER só seus próprios dados |
| `/clientes/cpf/{cpf}` | GET | ✅ | ❌ | ❌ | Busca por CPF - Admin apenas |
| `/clientes/username/{username}` | GET | ✅ | ❌ | ❌ | Busca por username - Admin apenas |

---

## Informações Técnicas

### Estrutura do Token JWT

Para endpoints que exigem autenticação, o token deve conter:

**Claims obrigatórios:**
- `groups`: array com roles do usuário
- `userId`: ID do usuário no sistema

**Exemplo de uso do token:**
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Validação de CPF

A aplicação utiliza validação customizada de CPF que verifica:
- Formato (11 dígitos)
- Algoritmo de validação dos dígitos verificadores
- Rejeita CPFs com todos os dígitos iguais (111.111.111-11, etc.)

### Criptografia de Senhas

- Senhas são criptografadas usando BCrypt antes do armazenamento
- Senhas nunca são retornadas nas responses
- Minimum de 6 caracteres obrigatório

### Roles Disponíveis

- **USER**: Usuário comum
  - Pode criar conta própria
  - Pode visualizar/atualizar apenas seus dados
  - Acesso a funcionalidades básicas

- **ADMIN**: Administrador
  - Acesso total ao sistema
  - Pode visualizar/gerenciar todos os clientes
  - Pode buscar por CPF e username

---

## Exemplos de Uso

### cURL

**Criar novo cliente (público):**
```bash
curl -X POST http://localhost:9090/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "12345678901",
    "username": "joao123",
    "password": "senha123",
    "role": "USER"
  }'
```

**Listar todos os clientes (ADMIN):**
```bash
curl -X GET http://localhost:9090/clientes \
  -H "Authorization: Bearer {admin_token}" \
  -H "Content-Type: application/json"
```

**Buscar cliente por ID (autenticado):**
```bash
curl -X GET http://localhost:9090/clientes/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

**Atualizar dados do cliente:**
```bash
curl -X PUT http://localhost:9090/clientes/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Santos",
    "username": "joao_atualizado"
  }'
```

---


---

## Referência Completa de Status Codes e Mensagens de Erro

### Status Codes Possíveis

| Status | Nome | Descrição | Endpoints Afetados |
|--------|------|-----------|-------------------|
| **200** | OK | Sucesso | GET, PUT |
| **201** | Created | Recurso criado | POST |
| **400** | Bad Request | Dados inválidos | Todos |
| **401** | Unauthorized | Token inválido/ausente | Todos exceto POST /clientes |
| **403** | Forbidden | Sem permissão | Todos autenticados |
| **404** | Not Found | Recurso não encontrado | GET por ID, CPF, username |
| **409** | Conflict | Dados duplicados | POST, PUT |
| **429** | Too Many Requests | Rate Limit | Todos |
| **500** | Internal Server Error | Erro interno | Todos |

### Mensagens de Erro por Categoria

#### 🔴 Erros de Validação (400)

**Campos obrigatórios (POST /clientes):**
- `"Nome é obrigatório"`
- `"CPF é obrigatório"`
- `"Username é obrigatório"`
- `"Password é obrigatório"`
- `"Role é obrigatório"`

**Validações de tamanho:**
- `"Nome deve ter entre 2 e 100 caracteres"`
- `"Username deve ter entre 3 e 50 caracteres"`
- `"Password deve ter no mínimo 6 caracteres"`

**Validações de formato:**
- `"CPF inválido"` (validação customizada)
- `"Role deve ser 'USER' ou 'ADMIN'"`
- `"CPF deve conter exatamente 11 dígitos"`

**Formato JSON:**
- `"Dados da requisição inválidos: formato JSON incorreto"`
- `"Formato JSON inválido"`

**Mensagem genérica:**
- `"Dados inválidos fornecidos"`

#### 🔴 Erros de Autenticação (401)
- `"Acesso não autorizado. É necessário fazer login para acessar este recurso."`
- `"Token JWT não encontrado"`

#### 🔴 Erros de Autorização (403)
- `"Acesso negado. Você não possui permissão para acessar este recurso."`
- `"Acesso negado: usuário só pode acessar seus próprios dados"`
- `"Acesso negado: role não autorizada"`

#### 🔴 Erros de Recurso Não Encontrado (404)
- `"Cliente não encontrado"`
- `"Cliente não encontrado com ID: {id}"`
- `"Cliente não encontrado com CPF: {cpf}"`
- `"Cliente não encontrado com username: {username}"`

#### 🔴 Erros de Conflito (409)
- `"CPF já cadastrado no sistema"`
- `"Username já existe"`
- `"CPF ou username já cadastrado no sistema"`

#### 🔴 Erros Internos (500)
- `"Erro interno do servidor: {detalhes específicos}"`
- `"Erro interno do servidor: Falha na conexão com o banco de dados"`
- `"Erro interno do servidor: Erro na criptografia da senha"`

### Estrutura Completa das Respostas de Erro

Todas as respostas de erro seguem o padrão:

```json
{
  "message": "Mensagem principal do erro",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/clientes",
  "errors": ["Lista de erros específicos (opcional)"]
}
```

### Exemplos Detalhados por Cenário

#### ❌ 400 - Múltiplas Validações Falharam (POST)
```json
{
  "message": "Dados inválidos fornecidos",
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/clientes",
  "errors": [
    "Campo 'nome': Nome é obrigatório (valor fornecido: null)",
    "Campo 'cpf': CPF inválido (valor fornecido: 12345)",
    "Campo 'username': Username deve ter entre 3 e 50 caracteres (valor fornecido: ab)",
    "Campo 'password': Password deve ter no mínimo 6 caracteres (valor fornecido: 123)",
    "Campo 'role': Role deve ser 'USER' ou 'ADMIN' (valor fornecido: CLIENTE)"
  ]
}
```

#### ❌ 400 - CPF Inválido
```json
{
  "message": "Dados inválidos fornecidos", 
  "timestamp": "2025-11-15T14:30:00",
  "status": 400,
  "path": "/clientes",
  "errors": [
    "Campo 'cpf': CPF inválido (valor fornecido: 11111111111)"
  ]
}
```

#### ❌ 401 - Token Ausente
```json
{
  "message": "Acesso não autorizado. É necessário fazer login para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 401,
  "path": "/clientes",
  "errors": null
}
```

#### ❌ 403 - USER Tentando Acessar Dados de Outro Cliente
```json
{
  "message": "Acesso negado: usuário só pode acessar seus próprios dados",
  "timestamp": "2025-11-15T14:30:00", 
  "status": 403,
  "path": "/clientes/2",
  "errors": null
}
```

#### ❌ 403 - USER Tentando Listar Todos os Clientes
```json
{
  "message": "Acesso negado. Você não possui permissão para acessar este recurso.",
  "timestamp": "2025-11-15T14:30:00",
  "status": 403,
  "path": "/clientes",
  "errors": null
}
```

#### ❌ 404 - Cliente Não Encontrado por ID
```json
{
  "message": "Cliente não encontrado",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/clientes/999",
  "errors": null
}
```

#### ❌ 404 - Cliente Não Encontrado por CPF
```json
{
  "message": "Cliente não encontrado com CPF: 99999999999",
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "path": "/clientes/cpf/99999999999",
  "errors": null
}
```

#### ❌ 409 - CPF Já Cadastrado
```json
{
  "message": "CPF já cadastrado no sistema",
  "timestamp": "2025-11-15T14:30:00",
  "status": 409,
  "path": "/clientes",
  "errors": null
}
```

#### ❌ 409 - Username Já Existe
```json
{
  "message": "Username já existe",
  "timestamp": "2025-11-15T14:30:00",
  "status": 409,
  "path": "/clientes",
  "errors": null
}
```

#### ❌ 500 - Falha no Banco de Dados
```json
{
  "message": "Erro interno do servidor: Connection timeout to database",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/clientes",
  "errors": null
}
```

#### ❌ 500 - Falha na Criptografia
```json
{
  "message": "Erro interno do servidor: Erro na criptografia da senha",
  "timestamp": "2025-11-15T14:30:00",
  "status": 500,
  "path": "/clientes",
  "errors": null
}
```

### Mapeamento de Erros por Endpoint

#### GET /clientes
- **401**: Token ausente/inválido  
- **403**: Não é ADMIN
- **500**: Erro interno

#### GET /clientes/{id}  
- **401**: Token ausente/inválido
- **403**: USER tentando acessar dados de outro cliente
- **404**: Cliente não encontrado
- **500**: Erro interno

#### POST /clientes
- **400**: Validações falharam, JSON malformado
- **409**: CPF ou username já existem
- **500**: Erro interno

#### PUT /clientes/{id}
- **400**: Validações falharam, JSON malformado
- **401**: Token ausente/inválido
- **403**: USER tentando atualizar dados de outro cliente
- **404**: Cliente não encontrado
- **409**: Username já existe (se tentando alterar)
- **500**: Erro interno

#### GET /clientes/cpf/{cpf}
- **401**: Token ausente/inválido
- **403**: Não é ADMIN
- **404**: Cliente não encontrado
- **500**: Erro interno

#### GET /clientes/username/{username}
- **401**: Token ausente/inválido
- **403**: Não é ADMIN  
- **404**: Cliente não encontrado
- **500**: Erro interno

---