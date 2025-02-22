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
}

application {
    mainClass.set("com.tracker.Main")
}