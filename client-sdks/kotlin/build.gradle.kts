plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.vertexcache"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()

    // 👇 Enable printing of stdout/stderr
    testLogging {
        events("passed", "failed", "skipped", "standard_out", "standard_error")
        showStandardStreams = true
    }
}

sourceSets {
    main {
        java.srcDirs("sdk/main/kotlin")
    }
    test {
        java.srcDirs("tests/kotlin")
    }
}

// Optional: Publishing block
/*
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.vertexcache"
            artifactId = "vertexcache-sdk"
            version = "1.0.0"
        }
    }
}
*/