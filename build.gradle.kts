plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.jetbrains:annotations:26.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")

    testImplementation("org.junit.platform:junit-platform-launcher:1.12.0")
    testImplementation("org.junit.platform:junit-platform-engine:1.12.0")
    implementation("org.junit.platform:junit-platform-commons:1.12.0")

    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("org.testcontainers:postgresql:1.20.5")

    implementation("org.springframework:spring-context:6.2.3")
    implementation("org.springframework:spring-tx:6.2.3")
    implementation("org.springframework.data:spring-data-jpa:3.4.3")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.springframework:spring-web:6.2.3")
}

application {
    mainClass.set("com.dominik.tasktracker.Main")
}

tasks.test {
    useJUnitPlatform()
}