plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "org.poiesis"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
    implementation("com.theokanning.openai-gpt3-java:api:0.18.2")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.theokanning.openai-gpt3-java", "org.poiesis.mcpete.openai")
    }
    runServer {
        minecraftVersion("1.20.4")
    }
}