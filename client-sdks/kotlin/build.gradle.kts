plugins {
    kotlin("jvm") version "1.9.23"
    `maven-publish`
    signing
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

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.vertexcache"
            artifactId = "vertexcache-sdk-kotlin"
            version = project.version.toString()

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("VertexCache Kotlin SDK")
                description.set("Kotlin client library for VertexCache")
                url.set("https://github.com/VertexCache/VertexCache/tree/main/client-sdks/kotlin")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.html")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/VertexCache/VertexCache.git")
                    developerConnection.set("scm:git:ssh://git@github.com:VertexCache/VertexCache.git")
                    url.set("https://github.com/VertexCache/VertexCache")
                }

                developers {
                    developer {
                        id.set("jasonlam604")
                        name.set("Jason Lam")
                        email.set("contact@vertexcache.com")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            val usernameProp = findProperty("ossrhUsername")?.toString() ?: throw GradleException("ossrhUsername not set")
            val passwordProp = findProperty("ossrhPassword")?.toString() ?: throw GradleException("ossrhPassword not set")

            println("DEBUG - OSSRH Username Resolved: $usernameProp")
            println("DEBUG - OSSRH Password Length: ${passwordProp.length}")

            credentials {
                username = usernameProp
                password = passwordProp
            }
        }
    }
}

signing {
    useGpgCmd()
    isRequired = gradle.taskGraph.hasTask("publish")
    sign(publishing.publications["mavenJava"])
}
