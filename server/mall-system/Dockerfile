# Multi-stage build for Spring Boot service
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 先复制 pom 利用 Docker 层缓存
COPY ../../pom.xml ./parent-pom.xml
COPY pom.xml ./pom.xml
COPY src ./src

RUN mvn clean package -DskipTests -f pom.xml

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall
USER mall

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
