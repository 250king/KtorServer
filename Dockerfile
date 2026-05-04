FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew && ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

ENV TZ=Asia/Shanghai

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
