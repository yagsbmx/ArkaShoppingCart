FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle clean bootJar --no-daemon -x test

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8081 \
    SPRING_DATASOURCE_URL=jdbc:postgresql://shoppingcart-db:5432/shoppingcartdb \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=0921 \
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ \
    SECURITY_JWT_SECRET=short_secret_key_32_chars_len!!! \
    MAIL_ENABLED=false \
    SPRING_MAIL_HOST=smtp.gmail.com \
    SPRING_MAIL_PORT=587 \
    SPRING_MAIL_USERNAME=arkaproyect0921@gmail.com \
    SPRING_MAIL_PASSWORD=TU_APP_PASSWORD

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
