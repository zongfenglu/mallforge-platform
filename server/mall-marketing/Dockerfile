FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# 复制父 pom 和全部子模块（父 pom 声明了所有模块，缺一不可）
COPY pom.xml ./
COPY mall-common/     ./mall-common/
COPY mall-framework/  ./mall-framework/
COPY mall-gateway/    ./mall-gateway/
COPY mall-system/     ./mall-system/
COPY mall-product/    ./mall-product/
COPY mall-order/      ./mall-order/
COPY mall-marketing/  ./mall-marketing/
COPY mall-payment/    ./mall-payment/

# 先安装公共模块，再只打包目标模块
RUN mvn install -pl mall-common,mall-framework -am -DskipTests -q &&     mvn package -pl mall-marketing -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall
USER mall

COPY --from=build /build/mall-marketing/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java",   "-XX:+UseContainerSupport",   "-XX:MaxRAMPercentage=75.0",   "-Djava.security.egd=file:/dev/./urandom",   "-jar", "app.jar"]
