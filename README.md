#  API-INVESTIMENTOS-CAIXA - C159555

Projeto de API para o CXVERSO que analisa o comportamento financeiro do cliente e ajusta seu perfil de risco, sugerindo produtos de investimento com base em **MACHINE LEARNING AVANÇADO**.

## 🤖️ Machine Learning

### Sistema de Recomendação Para Investimentos Financeiros 
- [**MACHINE_LEARNING**](docs/DOCUMENTACAO_MACHINE_LEARNING.md) - **Recomendação com Distância Euclidiana com 8 dimensões**
    - Algoritmo avançado de recomendação personalizada de produtos
    - Análise multidimensional com 8 características financeiras
    - Aprendizado baseado em histórico real de investimentos ou simulações
	- Decay Temporal: Investimentos recentes têm mais relevância na recomendação
	- Ponderação Inteligente por Investimento
	- Peso Logarítmico: Evita dominância excessiva de investimentos de alto valor
	- Expansível para N dimensões
	
### Endpoints com Machine Learning
- [1. GET /perfil-risco/{clienteId}](docs/DOCUMENTACAO_PERFIL_RISCO.md#1-get-perfil-riscoclienteid) *Usa ML para definir o perfil de risco do cliente e mostra probabilidade de acerto.*
- [2. GET /produtos-recomendados/cliente/{clienteId}](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md#1-get-produtos-recomendadosclienteclienteid) *Recomenda produtos usando ML, com base no histórico de investimentos ou simulações.*
- [3. POST /simular-investimento](docs/DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md#1-post-simular-investimento) *Caso critério de busca retorne mais de 1 produto, usa ML para decidr qual melhor opção.*
	
##  Por Onde Começar

1. Use Docker Composer e execute o comando abaixo na pasta raiz do projeto:
   ```bash
   docker compose up --build
   ```

2. Crie um usuário:
   - [**Criar Usuário**](docs/DOCUMENTACAO_CLIENTE.md#3-post-clientes)

3. Autentique o usuário na API (Obter Token JWT):
   - [**Autenticação**](docs/DOCUMENTACAO_AUTENTICACAO.md#1-post-entrar)

4. Explore a documentação e os **36 ENDPOINTS** da API:
   - [**Documentação completa dos endpoints e funcionalidades da API**](#funcionalidades-e-documentações-da-api)
   - [**Collection do Postman**](#especificação-da-api)
   - [**OpenAPI/Swagger**](#especificação-da-api)

##  Evidências

   Confira as evidências de funcionamento, relatório de testes e análise do SONAR na pasta: [**EVIDENCIAS**](./evidencias)


##  Funcionalidades e Documentações da API

###  Autenticação e Segurança
- [**AUTENTICACAO**](docs/DOCUMENTACAO_AUTENTICACAO.md) - Documentação da API de autenticação JWT e autorização baseada em roles

###  Gestão de Clientes
- [**CLIENTE**](docs/DOCUMENTACAO_CLIENTE.md) - Documentação da API de operações para gestão do cadastro de clientes
- [**PERFIL_RISCO**](docs/DOCUMENTACAO_PERFIL_RISCO.md) - Documentação da API de avaliação e cálculo de perfil de risco 

###  Gestão de Produtos Financeiros
- [**PRODUTO**](docs/DOCUMENTACAO_PRODUTO_RESOURCE.md) - Documentação da API de operações para gestão do cadastro de produtos de investimento

###  Recomendações de Investimento
- [**PRODUTO_RECOMENDADO**](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md) - Documentação da API de recomendações de investimentos 

###  Simulações de Investimento
- [**SIMULACAO_INVESTIMENTO**](docs/DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md) - Documentação da API de simulações de cenários de investimento 
- [**CONSULTA_SIMULACAO**](docs/DOCUMENTACAO_SIMULACAO_RESOURCE.md) - Documentação da API de consulta de simulações de investimentos 

###  Investimentos
- [**INVESTIMENTO**](docs/DOCUMENTACAO_INVESTIMENTO_RESOURCE.md) - Documentação da API de realização de investimentos

###  Monitoramento e Observabilidade
 - [**TELEMETRIA**](docs/DOCUMENTACAO_TELEMETRIA.md) - Documentação da API de consulta de telemetria e métricas de performance
 - [**LOGS_ACESSO**](docs/DOCUMENTACAO_SISTEMA_LOGS_ACESSO.md) - Documentação da API de auditoria e registro detalhado de acessos à API

##  Especificação da API

###  OpenAPI/Swagger
- [**swagger-api-investimentos-caixa**](docs/swagger-api-investimentos-caixa.yaml) - **Especificação completa da API** em formato OpenAPI 3.0.3
    - Todos os endpoints documentados com schemas detalhados
    - Autenticação JWT e segurança por roles
    - Validações completas com Jakarta Bean Validation
    - Códigos de resposta HTTP apropriados

###  Postman Collection
- [**API-INVESTIMENTOS-CAIXA.postman_collection**](docs/API-INVESTIMENTOS-CAIXA.postman_collection.json) - **Collection completo da API** em formato V2.1
    - Exemplos práticos para todos os endpoints
	
## ️ Arquitetura do Sistema

A API foi construída seguindo os princípios de:

- **Clean Architecture** com separação clara de responsabilidades
- **RESTful APIs** com endpoints bem definidos
- **Segurança por Design** com JWT e autorização baseada em roles
- **Rate Limit** requisições por IP (default 30 por segundo)
- **Observabilidade** com sistema de telemetria integrado
- **Machine Learning Avançado** - **Distância Euclidiana Multidimensional** para recomendações personalizadas
- **Testes Abrangentes** com cobertura de integração
- **Cache** na Telemetria e Logs de Acesso para otimização de performance

##  **Diferencial Tecnológico: Sistema de Recomendação**


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
- ✅ **Gestão de Clientes** - Cadastro completo com validações
- ✅ **Produtos Financeiros** - Gestão completa de produtos
- ✅ **Sistema de Recomendação** - **Algoritmo avançado com Distância Euclidiana Multidimensional**
- ✅ **Perfil de Risco** - Cálculo baseado em fatores de mercado
- ✅ **Simulações** - Cenários completos de investimento
- ✅ **Telemetria** - Monitoramento em tempo real
- ✅ **Testes de Integração** - Cobertura abrangente
- ✅ **Documentação Técnica** - Especificação completa do sistema inteligente

##  Tecnologias Utilizadas

- **Quarkus 3.24.3** - Framework Java nativo para nuvem
- **JAX-RS** - APIs RESTful
- **Hibernate ORM + Panache** - Persistência de dados
- **SmallRye JWT** - Autenticação e autorização
- **SQLite Database** - Banco de dados em arquivo
- **JUnit 5 + RestAssured** - Testes automatizados
- **Machine Learning** - Algoritmo personalizado de recomendações
- **OpenAPI 3.0.3** - Especificação completa da API
- **Caffeine Cache** - Cache de alta performance
---

## Documentos de Conformidade e Análise

### ✅ Rastreabilidade de Requisitos
- [**MATRIZ_CONFORMIDADE**](docs/MATRIZ_CONFORMIDADE.md) - **Matriz de conformidade completa** - Mapeamento detalhado de todos os requisitos entregues vs. especificação (demonstra cobertura 100% dos requisitos)
- [**RELATORIO_ANALISE_REQUISITOS**](docs/RELATORIO_ANALISE_REQUISITOS.md) - **Relatório completo de análise de requisitos** - Análise funcional e não-funcional de cada componente entregue
- [**ANALISE_ML_RESUMO**](docs/ANALISE_ML_RESUMO.md) - **Resumo executivo da análise de Machine Learning** - Validação do algoritmo de Distância Euclidiana Multidimensional e suas características técnicas


TESTES AUTOMATIZADOS
-----------------------------
- TOTAL DE TESTES: **697**
- TAXA DE SUCESSO: **100%** (697/697)
- FALHAS: **0** | ERROS: **0** | IGNORADOS: **0**
- TEMPO TOTAL DE EXECUÇÃO: **41,605 s**
- TEMPO MÉDIO POR TESTE: **0,062 s**


- TESTES DE INTEGRAÇÃO: **167**
- TESTES UNITÁRIOS: **530**

Cobertura (IntelliJ Test Report)
----------------------
- COVERAGE (CLASS): **97%**
- COVERAGE (METHOD): **96%**
- COVERAGE (LINE): **91%**


Cobertura (SONARQube Cloud)
----------------------
- COVERAGE: **85%**

Quality Gate (SONARQube Cloud)
----------------------
- Passed
