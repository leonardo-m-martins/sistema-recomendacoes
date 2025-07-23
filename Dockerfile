# Etapa 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Diretório de trabalho no container
WORKDIR /app

# Copia o código-fonte e o arquivo pom.xml
COPY pom.xml .
COPY src ./src

# Executa o build do projeto
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final, apenas com o jar gerado
FROM eclipse-temurin:21-jdk-alpine

# Diretório de trabalho no container
WORKDIR /app

# Copia o jar gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
