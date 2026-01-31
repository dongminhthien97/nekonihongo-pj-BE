// build.gradle.kts – Kotlin DSL cho NekoNihongo Backend (Spring Boot 3.x – tương thích tốt nhất hiện tại 2026)
// Nếu Spring Boot 4.0.0 chính thức ra mắt, chỉ cần update version plugin

plugins {
    id("org.springframework.boot") version "3.3.4"  // Version mới nhất Spring Boot 3.x (tính đến 2026)
    id("io.spring.dependency-management") version "1.1.6"
    java
    // Nếu dùng Kotlin cho entity/service (tùy chọn)
    // kotlin("jvm") version "1.9.24"
    // kotlin("plugin.spring") version "1.9.24"
}

group = "com.nekonihongo"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // === Core Spring Boot Starters ===
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    

    // === Jackson cho Java Time (LocalDateTime, etc.) ===
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // === Database ===
    runtimeOnly("com.mysql:mysql-connector-j")

    // === JWT ===
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // === OpenAPI / Swagger UI ===
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // === Lombok ===
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // === DevTools (chỉ dev) ===
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // === Test ===
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")

    // === Nếu dùng Thymeleaf cho email HTML template (tùy chọn) ===
    // implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    archiveFileName.set("nekonihongo-backend.jar")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Optional: Tối ưu build cache
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}