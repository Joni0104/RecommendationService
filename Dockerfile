FROM openjdk:17-jdk-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/recommendation-service-*.jar app.jar
COPY data/transaction.mv.db /app/data/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
