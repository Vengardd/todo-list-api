plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Core Spring Framework
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.4.4")

    // Database & Persistence
    implementation("org.springframework.data:spring-data-jpa:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.4")
    runtimeOnly("org.postgresql:postgresql:42.7.5")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.16")

    // Utilities & Annotations
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.4")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.testcontainers:postgresql:1.20.6")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.5")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
}

application {
    mainClass.set("com.dominik.todolist.TodoListApplication")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}