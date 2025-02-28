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
}

application {
    mainClass.set("com.dominik.tasktracker.Main")
}