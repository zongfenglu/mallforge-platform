# Multi-stage build for mall-payment
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

COPY pom.xml /build/pom.xml
COPY mall-common/ /build/mall-common/
COPY mall-framework/ /build/mall-framework/
COPY mall-payment/ /build/mall-payment/

RUN mvn -f /build/pom.xml install -pl mall-common,mall-framework -am -DskipTests -q &&     mvn -f /build/mall-payment/pom.xml package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall
USER mall

COPY --from=build /build/mall-payment/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java",   "-XX:+UseContainerSupport",   "-XX:MaxRAMPercentage=75.0",   "-Djava.security.egd=file:/dev/./urandom",   "-jar", "app.jar"]
