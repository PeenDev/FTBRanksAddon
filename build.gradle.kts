plugins {
    id("java")
}

group = "com.peendev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.erdbeerbaerlp.de/repository/maven-public/") }
}

dependencies {
    compileOnly("de.erdbeerbaerlp:dcintegration.common:3.0.7")
    compileOnly("org.apache.logging.log4j:log4j-api:2.17.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}