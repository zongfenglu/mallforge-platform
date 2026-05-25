FROM registry.cn-hangzhou.aliyuncs.com/library/maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

COPY pom.xml ./
COPY mall-common/     ./mall-common/
COPY mall-framework/  ./mall-framework/
COPY mall-gateway/    ./mall-gateway/
COPY mall-system/     ./mall-system/
COPY mall-product/    ./mall-product/
COPY mall-order/      ./mall-order/
COPY mall-marketing/  ./mall-marketing/
COPY mall-payment/    ./mall-payment/

RUN mvn install -pl mall-common,mall-framework -am -DskipTests -q &&     mvn package -pl mall-marketing -DskipTests -q

FROM registry.cn-hangzhou.aliyuncs.com/library/openjdk:17-jdk-alpine
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall
USER mall

COPY --from=build /build/mall-marketing/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java",   "-XX:+UseContainerSupport",   "-XX:MaxRAMPercentage=75.0",   "-Djava.security.egd=file:/dev/./urandom",   "-jar", "app.jar"]
