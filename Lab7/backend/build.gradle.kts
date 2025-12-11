plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Spring Boot Core ---
    implementation("org.springframework.boot:spring-boot-starter-web")

    // --- JPA/Hibernate ---
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // --- JSON support for LocalDate / LocalDateTime ---
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // --- Database driver ---
    runtimeOnly("org.postgresql:postgresql")

    // --- Development tools (hot reload) ---
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // --- TEST ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
