# 1단계: 빌드 (JDK 21)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 캐시 활용을 위해 wrapper/메타 먼저 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle* settings.gradle* gradle.properties* ./

# (선택) CRLF 방지: \r 제거 후 실행권한 부여
RUN set -eux; \
    sed -i 's/\r$//' gradlew; \
    chmod +x gradlew

# 의존성 캐시 워밍업 (변경 적은 단계)
RUN ./gradlew --no-daemon --version

# 소스 나중에 복사 → 변경 시 여기부터만 캐시 무효화
COPY src src

# 실제 빌드
RUN ./gradlew --no-daemon clean bootJar -x test

# 2단계: 런타임 (JRE 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
