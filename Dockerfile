## Multi-stage Dockerfile para API Investimentos CAIXA
## Otimizado para Quarkus com Java 21

# ============================================
# Stage 1: Build - Compilação e Empacotamento
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar arquivos de configuração Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copiar código-fonte
COPY src ./src

# Build do projeto (criar jar executável)
RUN mvn clean package -DskipTests \
    -Dquarkus.package.type=fast-jar \
    -Dquarkus.build.skip-tests=true

# ============================================
# Stage 2: Runtime - Execução da Aplicação
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Metadados da imagem
LABEL maintainer="API Investimentos CAIXA"
LABEL description="API de Simulação de Investimentos - CAIXA"
LABEL version="1.0"

WORKDIR /app

# Copiar artefatos do build (otimizado - apenas necessário)
COPY --from=builder /build/target/quarkus-app/lib/ ./lib/
COPY --from=builder /build/target/quarkus-app/*.jar ./
COPY --from=builder /build/target/quarkus-app/app/ ./

# Criar diretório para banco de dados
RUN mkdir -p /app/data

# Copiar chaves JWT (será sobrescrito pelo Docker Compose volume)
# Elas serão compartilhadas via volumes para persistência
COPY public-key.pem /app/public-key.pem 2>/dev/null || true
COPY private-key.pem /app/private-key.pem 2>/dev/null || true

# Copiar script SQL de inicialização (opcional)
COPY others/CRIACAO_BANCO_DE_DADOS.sql /app/init.sql 2>/dev/null || true

# Definir permissões
RUN chmod +x /app/quarkus-run.jar || true

# Expor a porta da aplicação
EXPOSE 9090

# Variáveis de ambiente padrão
ENV JAVA_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV QUARKUS_HTTP_PORT=9090
ENV QUARKUS_DATASOURCE_JDBC_URL="jdbc:sqlite:./banco_de_dados.db"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9090/health || exit 1

# Comando para iniciar a aplicação
CMD ["java", \
     "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", \
     "-jar", "quarkus-run.jar"]

