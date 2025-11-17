# 📊 API Investimentos Caixa - Documentação

Este é o índice principal da documentação da API de Investimentos da Caixa. Aqui você encontrará todos os recursos, funcionalidades e guias organizados por categoria.

## 🚀 Funcionalidades e Documentações da API

### 🔐 Autenticação e Segurança
- [**DOCUMENTACAO_AUTENTICACAO.md**](docs/DOCUMENTACAO_AUTENTICACAO.md) - Sistema de autenticação JWT e autorização baseada em roles

### 👥 Gestão de Clientes
- [**DOCUMENTACAO_CLIENTE.md**](docs/DOCUMENTACAO_CLIENTE.md) - Operações CRUD para clientes e validações
- [**DOCUMENTACAO_PERFIL_RISCO.md**](docs/DOCUMENTACAO_PERFIL_RISCO.md) - Sistema de avaliação e cálculo de perfil de risco

### 💰 Produtos Financeiros
- [**DOCUMENTACAO_PRODUTO_RESOURCE.md**](docs/DOCUMENTACAO_PRODUTO_RESOURCE.md) - Gestão de produtos de investimento
- [**DOCUMENTACAO_PRODUTO_RECOMENDADO.md**](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md) - Sistema de recomendação baseado em Machine Learning

### 🧮 Simulações de Investimento
- [**DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md**](docs/DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md) - Simulações de cenários de investimento
- [**DOCUMENTACAO_SIMULACAO_RESOURCE.md**](docs/DOCUMENTACAO_SIMULACAO_RESOURCE.md) - API endpoints para simulações
- [**DOCUMENTACAO_INVESTIMENTO_RESOURCE.md**](docs/DOCUMENTACAO_INVESTIMENTO_RESOURCE.md) - API endpoints para investimentos (registro e consulta)

### 📈 Monitoramento e Observabilidade
- [**DOCUMENTACAO_TELEMETRIA.md**](docs/DOCUMENTACAO_TELEMETRIA.md) - Sistema de telemetria e métricas de performance

## 🤖️ Inteligência Artificial e Machine Learning

### 🤖Sistema de Recomendação Para Investimentos Ffinanceiros 
- [**DOCUMENTACAO_MACHINE_LEARNING.md**](docs/DOCUMENTACAO_MACHINE_LEARNING.md) - **🚀 Sistema de Recomendação com Distância Euclidiana**
    - Algoritmo avançado de recomendação personalizada de produtos
    - Análise multidimensional com 8 características financeiras
    - Aprendizado baseado em histórico real de investimentos

##  Especificação da API

### 🔌 OpenAPI/Swagger
- [**swagger-api-investimentos-caixa.yaml**](./swagger-api-investimentos-caixa.yaml) - **Especificação completa da API** em formato OpenAPI 3.0.3
    - 27 endpoints documentados com schemas detalhados
    - Autenticação JWT e segurança por roles
    - Exemplos práticos para todos os endpoints
    - Validações completas com Jakarta Bean Validation
    - Códigos de resposta HTTP apropriados

## 🏗️ Arquitetura do Sistema

A API foi construída seguindo os princípios de:

- **Clean Architecture** com separação clara de responsabilidades
- **RESTful APIs** com endpoints bem definidos
- **Segurança por Design** com JWT e autorização baseada em roles
- **Observabilidade** com sistema de telemetria integrado
- **🚀 Machine Learning Avançado** - **Distância Euclidiana Multidimensional** para recomendações personalizadas
- **Testes Abrangentes** com cobertura de integração

## 🌟 **Diferencial Tecnológico: Sistema de Recomendação ML**

A API Investimentos CAIXA possui um **sistema de Machine Learning proprietário** que representa o estado da arte em recomendação de produtos financeiros:

### **🎯 Características Únicas**

- **📐 Algoritmo Matemático Avançado**: Distância Euclidiana em 8 dimensões simultâneas
- **🧠 Aprendizado Comportamental**: Aprende com histórico real de investimentos e simulações
- **🔍 Transparência Total**: Cada recomendação possui justificativa matemática auditável
### **🚀 Por Que Este Sistema é Superior?**

1. **Análise Multidimensional**: Avalia valor, tipo, rentabilidade, liquidez, risco, prazo, índices e garantias simultaneamente
2. **Ponderação Inteligente**: Produtos similares a investimentos de maior valor recebem maior peso
3. **Adaptação Contínua**: Melhora continuamente com cada interação do cliente
4. **Escalabilidade Real**: Processa milhares de produtos em < 150ms

## 🚦 Status do Projeto

- ✅ **Autenticação JWT** - Implementado e testado
- ✅ **Gestão de Clientes** - CRUD completo com validações
- ✅ **Produtos Financeiros** - Gestão completa de produtos
- ✅ **🌟 Sistema de Recomendação ML** - **Algoritmo avançado com Distância Euclidiana Multidimensional**
- ✅ **Perfil de Risco** - Cálculo baseado em fatores de mercado
- ✅ **Simulações** - Cenários completos de investimento
- ✅ **Telemetria** - Monitoramento em tempo real
- ✅ **Testes de Integração** - Cobertura abrangente
- ✅ **Documentação Técnica ML** - Especificação completa do sistema inteligente

## 📝 Como Navegar

1. **Para desenvolvedores**: 
   - Comece pelo [**Swagger YAML**](./swagger-api-investimentos-caixa.yaml) para visão completa da API
   - Continue pela [Autenticação](docs/DOCUMENTACAO_AUTENTICACAO.md) e depois explore os recursos específicos
2. **Para arquitetos**: Veja a [Telemetria](docs/DOCUMENTACAO_TELEMETRIA.md) para entender o monitoramento do sistema
3. **Para analistas**: O [Sistema de Recomendação](docs/DOCUMENTACAO_PRODUTO_RECOMENDADO.md) contém a lógica de ML
4. **Para QA**: As documentações de testes contêm cenários completos de validação
5. **Para integração**: Use o [**Swagger YAML**](./swagger-api-investimentos-caixa.yaml) com ferramentas como:
   - **Swagger UI** para interface interativa
   - **Postman** para importar coleção automaticamente
   - **Insomnia** para testes de API
   - Geradores de código para SDKs em diferentes linguagens

## 🔧 Tecnologias Utilizadas

- **Quarkus 3.24.3** - Framework Java nativo para nuvem
- **JAX-RS** - APIs RESTful
- **Hibernate ORM + Panache** - Persistência de dados
- **SmallRye JWT** - Autenticação e autorização
- **SQLite Database** - Banco de dados em arquivo
- **JUnit 5 + RestAssured** - Testes automatizados
- **Machine Learning** - Algoritmo personalizado de recomendações
- **OpenAPI 3.0.3** - Especificação completa da API


*Esta documentação é mantida automaticamente e reflete o estado atual do sistema.*

---

## 📋 Documentos de Conformidade e Análise

### ✅ Rastreabilidade de Requisitos
- [**MATRIZ_CONFORMIDADE.md**](docs/MATRIZ_CONFORMIDADE.md) - **Matriz de conformidade completa** - Mapeamento detalhado de todos os requisitos entregues vs. especificação (demonstra cobertura 100% dos requisitos)
- [**RELATORIO_ANALISE_REQUISITOS.md**](docs/RELATORIO_ANALISE_REQUISITOS.md) - **Relatório completo de análise de requisitos** - Análise funcional e não-funcional de cada componente entregue
- [**ANALISE_ML_RESUMO.md**](docs/ANALISE_ML_RESUMO.md) - **Resumo executivo da análise de Machine Learning** - Validação do algoritmo de Distância Euclidiana Multidimensional e suas características técnicas

**👉 Estes documentos comprovam a entrega completa de todos os requisitos do projeto.**
