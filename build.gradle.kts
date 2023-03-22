plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("com.theokanning.openai-gpt3-java:service:0.11.1")
    implementation("com.theokanning.openai-gpt3-java:api:0.11.1")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.theokanning.openai-gpt3-java", "org.poiesis.mcpete.openai")
    }
}