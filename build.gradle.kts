plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.comeandcommue"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.jsoup:jsoup:1.17.2") // HTML 파싱 라이브러리
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
	// Selenium Java
	implementation("org.seleniumhq.selenium:selenium-java:4.19.1")
	// (선택) WebDriverManager: 자동으로 ChromeDriver 등 설치/버전 관리해주는 라이브러리
	implementation("io.github.bonigarcia:webdrivermanager:5.2.0")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
