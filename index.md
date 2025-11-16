# 📊 API Investimentos Caixa - Documentação

Este é o índice principal da documentação da API de Investimentos da Caixa. Aqui você encontrará todos os recursos, funcionalidades e guias organizados por categoria.

## 🚀 Funcionalidades Principais

### 🔐 Autenticação e Segurança
- [**DOCUMENTACAO_AUTENTICACAO.md**](./DOCUMENTACAO_AUTENTICACAO.md) - Sistema de autenticação JWT e autorização baseada em roles

### 👥 Gestão de Clientes
- [**DOCUMENTACAO_CLIENTE.md**](./DOCUMENTACAO_CLIENTE.md) - Operações CRUD para clientes e validações
- [**DOCUMENTACAO_PERFIL_RISCO.md**](./DOCUMENTACAO_PERFIL_RISCO.md) - Sistema de avaliação e cálculo de perfil de risco

### 💰 Produtos Financeiros
- [**DOCUMENTACAO_PRODUTO_RESOURCE.md**](./DOCUMENTACAO_PRODUTO_RESOURCE.md) - Gestão de produtos de investimento
- [**DOCUMENTACAO_PRODUTO_RECOMENDADO.md**](./DOCUMENTACAO_PRODUTO_RECOMENDADO.md) - Sistema de recomendação baseado em Machine Learning

### 🧮 Simulações
- [**DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md**](./DOCUMENTACAO_SIMULACAO_INVESTIMENTO.md) - Simulações de cenários de investimento
- [**DOCUMENTACAO_SIMULACAO_RESOURCE.md**](./DOCUMENTACAO_SIMULACAO_RESOURCE.md) - API endpoints para simulações

### 📈 Monitoramento e Observabilidade
- [**DOCUMENTACAO_TELEMETRIA.md**](./DOCUMENTACAO_TELEMETRIA.md) - Sistema de telemetria e métricas de performance

## 🧪 Documentação de Testes

### 📋 Testes de Integração
- [**DOCUMENTACAO_TESTES_PRODUTO_RECOMENDADO.md**](./DOCUMENTACAO_TESTES_PRODUTO_RECOMENDADO.md) - Testes para o sistema de recomendações
- [**DOCUMENTACAO_TESTES_TELEMETRIA.md**](./DOCUMENTACAO_TESTES_TELEMETRIA.md) - Testes para o sistema de telemetria

## 🏗️ Arquitetura do Sistema

A API foi construída seguindo os princípios de:

- **Clean Architecture** com separação clara de responsabilidades
- **RESTful APIs** com endpoints bem definidos
- **Segurança por Design** com JWT e autorização baseada em roles
- **Observabilidade** com sistema de telemetria integrado
- **Machine Learning** para recomendações personalizadas
- **Testes Abrangentes** com cobertura de integração

## 🚦 Status do Projeto

- ✅ **Autenticação JWT** - Implementado e testado
- ✅ **Gestão de Clientes** - CRUD completo com validações
- ✅ **Produtos Financeiros** - Gestão completa de produtos
- ✅ **Sistema de Recomendação ML** - Algoritmo inteligente baseado em histórico
- ✅ **Perfil de Risco** - Cálculo baseado em fatores de mercado
- ✅ **Simulações** - Cenários completos de investimento
- ✅ **Telemetria** - Monitoramento em tempo real
- ✅ **Testes de Integração** - Cobertura abrangente

## 📝 Como Navegar

1. **Para desenvolvedores**: Comece pela [Autenticação](./DOCUMENTACAO_AUTENTICACAO.md) e depois explore os recursos específicos
2. **Para arquitetos**: Veja a [Telemetria](./DOCUMENTACAO_TELEMETRIA.md) para entender o monitoramento do sistema
3. **Para analistas**: O [Sistema de Recomendação](./DOCUMENTACAO_PRODUTO_RECOMENDADO.md) contém a lógica de ML
4. **Para QA**: As documentações de testes contêm cenários completos de validação

## 🔧 Tecnologias Utilizadas

- **Quarkus 3.24.3** - Framework Java nativo para nuvem
- **JAX-RS** - APIs RESTful
- **Hibernate ORM + Panache** - Persistência de dados
- **SmallRye JWT** - Autenticação e autorização
- **SQLite Database** - Banco de dados em arquivo
- **JUnit 5 + RestAssured** - Testes automatizados
- **Machine Learning** - Algoritmo personalizado de recomendações

---

*Esta documentação é mantida automaticamente e reflete o estado atual do sistema.*