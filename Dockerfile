# 1단계: 빌드 (JDK 21)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Gradle 캐시를 최대한 재사용하기 위한 최소 복사
COPY gradlew gradle/ ./
COPY settings.gradle* build.gradle* gradle.properties* ./
RUN chmod +x gradlew
# 의존성만 먼저 받아 캐시층 생성
RUN ./gradlew --no-daemon dependencies || true

# 이후 소스 복사 후 실제 빌드
COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test

# 2단계: 실행 (JRE 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
