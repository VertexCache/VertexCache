plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.vertexcache.client"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":sdk"))
}

application {
    mainClass.set("com.vertexcache.client.VertexCacheClientKt")
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
