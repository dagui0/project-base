import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
}

ext {
    set("java.version", 23)
    set("lombok.version", "1.18.38")
    set("jetbrains.annotations.version", "26.0.2")
    set("guava.version", "33.4.8-jre")
    set("commons.lang3.version", "3.17.0")
    set("junit.version", "5.13.0")
    set("compile.testing.version", "0.21.0")
}

group = "com.yidigun"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {

    // dependencies
    implementation("org.jetbrains:annotations:${project.ext["jetbrains.annotations.version"]}")
    implementation("org.apache.commons:commons-lang3:${project.ext["commons.lang3.version"]}")

    // Lombok
    compileOnly("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    annotationProcessor("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    testImplementation("org.projectlombok:lombok:${project.ext["lombok.version"]}")
    testAnnotationProcessor("org.projectlombok:lombok:${project.ext["lombok.version"]}")

    // compile testing
    testImplementation("com.google.guava:guava:${project.ext["guava.version"]}")
    testImplementation("com.google.testing.compile:compile-testing:${project.ext["compile.testing.version"]}")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter:${project.ext["junit.version"]}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.release.set(project.ext["java.version"] as Int)
    options.compilerArgs.addAll(listOf("-Xlint:all"))
}

tasks.jar {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    options {
        if (this is StandardJavadocDocletOptions) {
            encoding = "UTF-8"
            charSet = "UTF-8"
            title = "${project.name} ${project.version} API Documentation"
            version = project.version.toString()
            memberLevel = JavadocMemberLevel.PRIVATE
            links = listOf(
                "https://docs.oracle.com/en/java/javase/${project.ext["java.version"]}/docs/api/",
                "https://javadoc.io/doc/org.projectlombok/lombok/${project.ext["lombok.version"]}/",
                "https://javadoc.io/doc/org.jetbrains/annotations/${project.ext["jetbrains.annotations.version"]}/",
                "https://javadoc.io/doc/com.google.guava/guava/${project.ext["guava.version"]}/",
                "https://javadoc.io/doc/org.apache.commons/commons-lang3/${project.ext["commons.lang3.version"]}/"
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Project Base Classes")
                description.set("Project base classes and utilities for common coding conventions")
                url.set("https://github.com/dagui0/project-base")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("dagui0")
                        name.set("Daekyu Lee")
                        email.set("dagui0@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/dagui0/project-base.git")
                    url.set("https://github.com/dagui0/project-base")
                }
            }
        }
    }
    repositories {
        maven {
            name = "nexus.yidigun.com"
            val releasesRepoUrl = "https://nexus.yidigun.com/repository/maven-releases/"
            val snapshotsRepoUrl = "https://nexus.yidigun.com/repository/maven-snapshots/"
            url = URI(if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.findProperty("nexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
                password = project.findProperty("nexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
