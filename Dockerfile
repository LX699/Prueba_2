FROM maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /build
COPY . .
RUN mvn clean package

FROM openjdk:17-slim
EXPOSE 3001

WORKDIR /app

COPY --from=builder /build/target/Prueba_2-0.0.1-SNAPSHOT.jar ./prueba.jar
COPY src/main/resources/Dataset/dataset2.csv ./src/main/resources/Dataset/dataset2.csv

CMD ["java", "-jar", "prueba.jar"]