# ============================================
# Stage 1 — Build (optional se você builda fora)
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /project

# Copia o código e compila (caso queira build interno)
# Se você preferir buildar no IntelliJ, pode ignorar esse stage
COPY . .
RUN ./mvnw clean package -DskipTests

# ============================================
# Stage 2 — Runtime
# ============================================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia a aplicação compilada
COPY --from=build /project/target/quarkus-app /app/quarkus-app

# Copia as chaves PEM para o container (importante!)
#COPY public-key.pem /app/public-key.pem
#COPY private-key.pem /app/private-key.pem

# Copia o banco SQLite para dentro do container (vai ser montado via volume também)
COPY banco_de_dados.db /app/banco_de_dados.db
COPY banco_de_dados_teste.db /app/banco_de_dados_teste.db

EXPOSE 9090

ENV QUARKUS_HTTP_PORT=9090

CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]