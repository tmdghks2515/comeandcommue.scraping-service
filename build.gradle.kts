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
	mavenLocal()
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/tmdghks2515/comeandcommue.lib.web-lib")
		credentials {
			// gradle.properties의 gpr.user / gpr.key 사용, 환경변수로 fallback
			 username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
			 password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
		}
	}
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/tmdghks2515/comeandcommue.lib.data-lib")
		credentials {
			username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
			password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
		}
	}
}

dependencies {
	implementation("io.comeandcommue.lib:web-lib:0.0.1-SNAPSHOT")
	implementation("io.comeandcommue.lib:data-lib:0.0.1-SNAPSHOT")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.jsoup:jsoup:1.17.2") // HTML 파싱 라이브러리
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
	// Selenium Java
	implementation("org.seleniumhq.selenium:selenium-java:4.21.0")
	implementation("com.auth0:java-jwt:4.4.0")
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
