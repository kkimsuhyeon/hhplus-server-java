plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
    return providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.retry:spring-retry:2.0.12")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("user.timezone", "UTC")
    
    // Mockito를 Java Agent로 명시적으로 로드 (정석 방법)
    val mockitoAgent = classpath.elements.map { files ->
        files.single { it.asFile.name.startsWith("mockito-core-") }
    }
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:${mockitoAgent.get().asFile}")
    })
}
