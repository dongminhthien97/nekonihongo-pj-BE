// build.gradle.kts – Kotlin DSL cho NekoNihongo Backend
// Spring Boot 3.3.4 + Gradle 8.7 + Java 17

plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    java
    id("io.freefair.lombok") version "8.6" // Plugin Lombok cho Kotlin DSL
}

group = "com.nekonihongo"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // === Core Spring Boot ===
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator") // Thêm actuator

    // === Jackson Java Time ===
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // === Database ===
    runtimeOnly("com.mysql:mysql-connector-j")

    // === JWT ===
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // === OpenAPI / Swagger ===
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // === Lombok ===
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // === Dev ===
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // === Test ===
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// =========================
// TASKS CONFIGURATION
// =========================

// BootJar configuration - SỬA LỖI Ở ĐÂY
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    archiveFileName.set("nekonihongo-backend.jar")
    
    // Manifest configuration
    manifest {
        attributes(
            "Main-Class" to "org.springframework.boot.loader.JarLauncher",
            "Start-Class" to "com.nekonihongo.backend.BackendApplication",
            "Implementation-Version" to project.version,
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})",
            "Build-OS" to "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}"
        )
    }
    
    // Layered jar for better Docker support
    layered {
        enabled = true
        application {
            intoLayer("spring-boot-loader") {
                include("org/springframework/boot/loader/**")
            }
            intoLayer("application")
        }
        dependencies {
            intoLayer("application-dependencies") {
                include("*:*:*")
            }
        }
    }
}

// Disable plain jar
tasks.named<Jar>("jar") {
    enabled = false
    archiveClassifier.set("plain")
}

// =========================
// JAVA COMPILE OPTIONS
// =========================
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    
    // Java module fixes for Spring Boot 3
    options.compilerArgs.addAll(listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.io=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED"
    ))
}

// =========================
// TEST CONFIGURATION
// =========================
tasks.withType<Test> {
    useJUnitPlatform()
    
    // Enable test logging
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    // Skip tests on Railway build
    if (System.getenv("RAILWAY_ENVIRONMENT") != null) {
        enabled = false
    }
}

// =========================
// CUSTOM BUILD TASKS
// =========================

// Railway build task
tasks.register("railwayBuild") {
    group = "build"
    description = "Build for Railway deployment"
    dependsOn(tasks.clean, tasks.bootJar)
    
    doLast {
        val jarFile = tasks.bootJar.get().archiveFile.get().asFile
        println("✅ Railway build completed!")
        println("📦 JAR location: ${jarFile.absolutePath}")
        println("📏 File size: ${jarFile.length() / 1024 / 1024} MB")
        println("🔧 Ready for deployment on Railway!")
    }
}

// Production build task
tasks.register("productionBuild") {
    group = "build"
    description = "Build for production deployment"
    dependsOn(tasks.clean, tasks.bootJar)
    
    doLast {
        val jarFile = tasks.bootJar.get().archiveFile.get().asFile
        
        // Copy to deploy directory
        val deployDir = file("build/deploy")
        deployDir.mkdirs()
        
        val targetFile = File(deployDir, "nekonihongo-backend.jar")
        jarFile.copyTo(targetFile, overwrite = true)
        
        println("✅ Production build completed!")
        println("📦 JAR: ${targetFile.absolutePath}")
        println("📏 Size: ${targetFile.length() / 1024 / 1024} MB")
        println("🚀 Ready for deployment!")
    }
}

// =========================
// CLEAN TASK EXTENSION
// =========================
tasks.named("clean") {
    doLast {
        // Clean additional directories
        delete("build/deploy", "out", ".gradle")
        println("🧹 Clean completed!")
    }
}

// =========================
// GRADLE WRAPPER CONFIG
// =========================
tasks.named<Wrapper>("wrapper") {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}