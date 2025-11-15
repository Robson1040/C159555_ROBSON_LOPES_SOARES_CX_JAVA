# Melhorias na Validação JWT - Refatoração

## Resumo das Melhorias Implementadas

A validação de acesso JWT foi refatorada para melhorar a reutilização, tratamento de erros e organização do código.

### ✅ Implementações Realizadas

#### 1. **Exceção Customizada (AccessDeniedException)**
- **Local**: `src/main/java/br/gov/caixa/api/investimentos/exception/auth/AccessDeniedException.java`
- **Propósito**: Exceção específica para casos de acesso negado
- **Vantagem**: Melhor semântica e tratamento especializado

#### 2. **Classe Utilitária (JwtAuthorizationHelper)**
- **Local**: `src/main/java/br/gov/caixa/api/investimentos/helper/auth/JwtAuthorizationHelper.java`
- **Propósito**: Centralizar lógicas de autorização JWT
- **Vantagem**: Reutilização em múltiplos resources

#### 3. **Tratamento de Erro (BusinessExceptionHandler)**
- **Atualização**: Adicionado tratamento específico para `AccessDeniedException`
- **Status Code**: Retorna `403 Forbidden` em vez de `500 Internal Server Error`
- **Vantagem**: Status HTTP mais apropriado

#### 4. **Refatoração do Resource**
- **Remoção**: Método `validarAcessoAoCliente` local removido
- **Adoção**: Uso da classe utilitária `JwtAuthorizationHelper`
- **Vantagem**: Código mais limpo e reutilizável

## Comparação: Antes vs Depois

### ❌ **ANTES (Implementação Anterior)**

```java
// Resource com método duplicado
private void validarAcessoAoCliente(Long clienteId) {
    if (jwt.getGroups().contains("ADMIN")) {
        return;
    }
    if (jwt.getGroups().contains("USER")) {
        Long userIdJwt = jwt.getClaim("userId");
        if (userIdJwt == null || !userIdJwt.equals(clienteId)) {
            throw new RuntimeException("Acesso negado...");  // ❌ RuntimeException genérica
        }
    }
    throw new RuntimeException("Acesso negado...");  // ❌ Status 500
}
```

**Problemas:**
- Duplicação de código em cada Resource
- `RuntimeException` genérica sem semântica
- Status HTTP 500 (erro de servidor) para negação de acesso
- Não reutilizável

### ✅ **DEPOIS (Nova Implementação)**

```java
// Resource limpo usando helper
@Inject
JwtAuthorizationHelper authHelper;

public Response buscarHistoricoSimulacoes(@PathParam("clienteId") Long clienteId) {
    authHelper.validarAcessoAoCliente(jwt, clienteId);  // ✅ Reutilizável
    // ... resto do código
}
```

```java
// Helper reutilizável
public void validarAcessoAoCliente(JsonWebToken jwt, Long clienteId) {
    if (jwt == null) {
        throw new AccessDeniedException("Token JWT não encontrado");  // ✅ Exceção específica
    }
    // ... lógica de validação
    throw new AccessDeniedException("Acesso negado...");  // ✅ Status 403
}
```

**Melhorias:**
- ✅ Código reutilizável em qualquer Resource
- ✅ `AccessDeniedException` com semântica clara
- ✅ Status HTTP 403 (Forbidden) adequado
- ✅ Métodos helper adicionais (`isAdmin()`, `isUser()`, `getUserId()`)

## Como Usar em Novos Resources

### Exemplo 1: Resource de Investimentos
```java
@Path("/investimentos")
public class InvestimentoResource {
    
    @Inject JsonWebToken jwt;
    @Inject JwtAuthorizationHelper authHelper;
    
    @GET
    @Path("/cliente/{clienteId}")
    public Response buscarInvestimentosCliente(@PathParam("clienteId") Long clienteId) {
        // Validação automática com exceção apropriada
        authHelper.validarAcessoAoCliente(jwt, clienteId);
        
        // Lógica do negócio...
        return Response.ok(investimentos).build();
    }
}
```

### Exemplo 2: Validações Condicionais
```java
@PUT
@Path("/cliente/{clienteId}")
public Response atualizarCliente(@PathParam("clienteId") Long clienteId, ClienteRequest request) {
    // Para operações de escrita, só o próprio usuário pode atualizar (nem ADMIN)
    Long userId = authHelper.getUserId(jwt);
    if (userId == null || !userId.equals(clienteId)) {
        throw new AccessDeniedException("Usuário só pode atualizar seus próprios dados");
    }
    
    // Lógica de atualização...
}
```

### Exemplo 3: Verificações de Role
```java
@DELETE
@Path("/{id}")
public Response removerSimulacao(@PathParam("id") Long id) {
    // Só ADMIN pode remover qualquer simulação
    if (!authHelper.isAdmin(jwt)) {
        throw new AccessDeniedException("Operação permitida apenas para administradores");
    }
    
    // Lógica de remoção...
}
```

## Respostas de Erro Padronizadas

### Status 403 - Acesso Negado (USER tentando acessar dados de outro)
```json
{
    "message": "Acesso negado: usuário só pode acessar seus próprios dados",
    "timestamp": "2025-11-15T10:30:00",
    "status": 403,
    "path": "/simular-investimento/historico/5",
    "errors": null
}
```

### Status 403 - Role Não Autorizada
```json
{
    "message": "Acesso negado: role não autorizada",
    "timestamp": "2025-11-15T10:30:00",
    "status": 403,
    "path": "/simular-investimento/historico/5",
    "errors": null
}
```

### Status 403 - Token Ausente
```json
{
    "message": "Token JWT não encontrado",
    "timestamp": "2025-11-15T10:30:00",
    "status": 403,
    "path": "/simular-investimento/historico/5",
    "errors": null
}
```

## Benefícios da Refatoração

### 🔄 **Reutilização**
- Helper pode ser usado em qualquer Resource
- Métodos utilitários (`isAdmin()`, `getUserId()`) simplificam validações

### 🎯 **Semântica Clara**
- `AccessDeniedException` deixa intenção explícita
- Status HTTP 403 é mais apropriado que 500

### 🧹 **Código Limpo**
- Resources ficam focados na lógica de negócio
- Validação JWT centralizada em um local

### 🧪 **Testabilidade**
- Helper pode ser mockado facilmente
- Testes unitários mais simples

### 📈 **Escalabilidade**
- Fácil adição de novos tipos de validação
- Consistent em toda a aplicação

## Próximas Aplicações

Esta estrutura pode ser aplicada em:
- **ClienteResource** - validação de acesso a dados pessoais
- **InvestimentoResource** - controle de acesso a investimentos
- **ProdutoResource** - recursos administrativos (apenas ADMIN)
- **RelatorioResource** - relatórios por cliente