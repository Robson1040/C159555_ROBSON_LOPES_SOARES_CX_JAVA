#  API-INVESTIMENTOS-CAIXA - C159555



##  Por Onde Começar

1. Execute o comando na pasta raiz do projeto:
   ```bash
   docker compose up --build
   ```

2. Crie um cliente para começar:
   - [**Criar Cliente**](docs/DOCUMENTACAO_CLIENTE.md#3-post-clientes)

3. Autentique-se na API:
   - [**Autenticação**](docs/DOCUMENTACAO_AUTENTICACAO.md#1-post-entrar)

4. Explore nossa documentação abaixo ou explore a API pelo Postman:
   - [**Collection do Postman**](./API-INVESTIMENTOS-CAIXA.postman_collection.json)

##  Evidências

   Confira as evidências de funcionamento, relatório de testes e análise do SONAR na pasta: [**EVIDENCIAS**](./evidencias)


##  Funcionalidades e Documentações da API

###  Autenticação e Segurança
- [**AUTENTICACAO**](docs/DOCUMENTACAO_AUTENTICACAO.md) - API de autenticação JWT e autorização baseada em roles

###  Gestão de Clientes
- [**CLIENTE**](docs/DOCUMENTACAO_CLIENTE.md) - API de operações CRUD para clientes
- [**PERFIL_RISCO**](docs/DOCUMENTACAO_PERFIL_RISCO.md) - API de avaliação e cálculo de perfil de risco 

###  Gestão de Produtos Financeiros
- [**PRODUTO**](docs/DOCUMENTACAO_PRODUTO_RESOURCE.md) - API de operações CRUD para produtos de investimento

###  Recomendações de Investimento
- [**PRODUTO_RECOMENDADO**](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md) - API de recomendações de investimentos 

###  Simulações de Investimento
- [**INVESTIMENTO**](docs/DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md) - API de simulações de cenários de investimento 
- [**SIMULACAO_INVESTIMENTO**](docs/DOCUMENTACAO_SIMULACAO_RESOURCE.md) - API de consulta de simulações de investimentos 

###  Registro de Investimento
- [**INVESTIMENTO_RESOURCE**](docs/DOCUMENTACAO_INVESTIMENTO_RESOURCE.md) - API de realização de investimentos

###  Monitoramento e Observabilidade
 - [**TELEMETRIA**](docs/DOCUMENTACAO_TELEMETRIA.md) - API de consulta de telemetria e métricas de performance
 - [**LOGS_ACESSO**](docs/DOCUMENTACAO_SISTEMA_LOGS_ACESSO.md) - API de auditoria e registro detalhado de acessos à API

## 🤖️ Machine Learning

### Sistema de Recomendação Para Investimentos Financeiros 
- [**MACHINE_LEARNING**](docs/DOCUMENTACAO_MACHINE_LEARNING.md) - **Recomendação com Distância Euclidiana com 8 dimensões**
    - Algoritmo avançado de recomendação personalizada de produtos
    - Análise multidimensional com 8 características financeiras
    - Aprendizado baseado em histórico real de investimentos ou simulações
	
### Endpoints com Machine Learning
- [1. GET /perfil-risco/{clienteId}](docs/DOCUMENTACAO_PERFIL_RISCO.md#1-get-perfil-riscoclienteid)
- [2. GET /produtos-recomendados/cliente/{clienteId}](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md#1-get-produtos-recomendadosclienteclienteid)
- [3. POST /simular-investimento](docs/DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md#1-post-simular-investimento) *Quando não informado PRODUTO_ID*.
	
##  Especificação da API

###  OpenAPI/Swagger
- [**swagger-api-investimentos-caixa.yaml**](./swagger-api-investimentos-caixa.yaml) - **Especificação completa da API** em formato OpenAPI 3.0.3
    - Todos os endpoints documentados com schemas detalhados
    - Autenticação JWT e segurança por roles
    - Exemplos práticos para todos os endpoints
    - Validações completas com Jakarta Bean Validation
    - Códigos de resposta HTTP apropriados

###  Postman Collection
- [**API-INVESTIMENTOS-CAIXA.postman_collection**](./API-INVESTIMENTOS-CAIXA.postman_collection.json) - **Teste completo da API** em formato Collection V2.1

## ️ Arquitetura do Sistema

A API foi construída seguindo os princípios de:

- **Clean Architecture** com separação clara de responsabilidades
- **RESTful APIs** com endpoints bem definidos
- **Segurança por Design** com JWT e autorização baseada em roles
- **Rate Limit** requisições por IP (default 30 por segundo)
- **Observabilidade** com sistema de telemetria integrado
- **Machine Learning Avançado** - **Distância Euclidiana Multidimensional** para recomendações personalizadas
- **Testes Abrangentes** com cobertura de integração

##  **Diferencial Tecnológico: Sistema de Recomendação ML**


###  Características Únicas

- **Algoritmo Matemático Avançado**: Distância Euclidiana em 8 dimensões simultâneas
- **Aprendizado Comportamental**: Aprende com histórico real de investimentos e simulações
- **Transparência Total**: Cada recomendação possui justificativa matemática auditável

###  Por Que Este Sistema é Superior?

1. **Análise Multidimensional**: Avalia valor, tipo, rentabilidade, liquidez, risco, prazo, índices simultaneamente
2. **Ponderação Inteligente**: Produtos similares a investimentos de maior valor recebem maior peso
3. **Adaptação Contínua**: Melhora continuamente com cada investimento ou simulação do cliente

##  Status do Projeto

- ✅ **Autenticação JWT** - Implementado e testado
- ✅ **Gestão de Clientes** - CRUD completo com validações
- ✅ **Produtos Financeiros** - Gestão completa de produtos
- ✅ **Sistema de Recomendação ML** - **Algoritmo avançado com Distância Euclidiana Multidimensional**
- ✅ **Perfil de Risco** - Cálculo baseado em fatores de mercado
- ✅ **Simulações** - Cenários completos de investimento
- ✅ **Telemetria** - Monitoramento em tempo real
- ✅ **Testes de Integração** - Cobertura abrangente
- ✅ **Documentação Técnica ML** - Especificação completa do sistema inteligente

##  Tecnologias Utilizadas

- **Quarkus 3.24.3** - Framework Java nativo para nuvem
- **JAX-RS** - APIs RESTful
- **Hibernate ORM + Panache** - Persistência de dados
- **SmallRye JWT** - Autenticação e autorização
- **SQLite Database** - Banco de dados em arquivo
- **JUnit 5 + RestAssured** - Testes automatizados
- **Machine Learning** - Algoritmo personalizado de recomendações
- **OpenAPI 3.0.3** - Especificação completa da API

---

## Documentos de Conformidade e Análise

### ✅ Rastreabilidade de Requisitos
- [**MATRIZ_CONFORMIDADE**](docs/MATRIZ_CONFORMIDADE.md) - **Matriz de conformidade completa** - Mapeamento detalhado de todos os requisitos entregues vs. especificação (demonstra cobertura 100% dos requisitos)
- [**RELATORIO_ANALISE_REQUISITOS**](docs/RELATORIO_ANALISE_REQUISITOS.md) - **Relatório completo de análise de requisitos** - Análise funcional e não-funcional de cada componente entregue
- [**ANALISE_ML_RESUMO**](docs/ANALISE_ML_RESUMO.md) - **Resumo executivo da análise de Machine Learning** - Validação do algoritmo de Distância Euclidiana Multidimensional e suas características técnicas


TESTES AUTOMATIZADOS
-----------------------------
- TOTAL DE TESTES: **678**
- TAXA DE SUCESSO: **100%** (678/678)
- FALHAS: **0** | ERROS: **0** | IGNORADOS: **0**
- TEMPO TOTAL DE EXECUÇÃO: **41,605 s**
- TEMPO MÉDIO POR TESTE: **0,062 s**


- TESTES DE INTEGRAÇÃO: **167**
- TESTES UNITÁRIOS: **511**

Cobertura
----------------------
- COVERAGE (CLASS): **98%** (97 / 95)
- COVERAGE (METHOD): **95%** (557 / 583)
- COVERAGE (LINE): **91%** (1940 / 2125)


