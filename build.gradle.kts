import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
}

ext {
    set("project.info.dir", "generated/sources/project-info/java/main")
    set("project.info.package", "com.yidigun.base")
}

group = "com.yidigun"
version = "0.0.1"

// ProjectInfo generation task
val generateProjectInfoDir = project.layout.buildDirectory.dir(project.ext["project.info.dir"] as String)
sourceSets.main.get().java.srcDir(generateProjectInfoDir)
sourceSets.test.get().java.srcDir(generateProjectInfoDir)
apply(from = "gradle/project-info.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // compile testing
    testImplementation(libs.guava)
    testImplementation(libs.compile.testing)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<JavaCompile> {
    options.release.set(libs.versions.java.get().toInt())
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
}

tasks.withType<Javadoc> {

    javadocTool.set(javaToolchains.javadocToolFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    })

    options {
        if (this is StandardJavadocDocletOptions) {
            encoding = "UTF-8"
            charSet = "UTF-8"
            title = "${project.name} ${project.version} API Documentation"
            version = project.version.toString()
            memberLevel = JavadocMemberLevel.PROTECTED
            links = listOf(
                "https://docs.oracle.com/en/java/javase/${libs.versions.java.get()}/docs/api/",
                "https://javadoc.io/doc/org.projectlombok/lombok/${libs.versions.lombok.get()}/",
                "https://javadoc.io/doc/org.jetbrains/annotations/${libs.versions.jetbrainsAnnotations.get()}/",
                "https://javadoc.io/doc/com.google.guava/guava/${libs.versions.guava.get()}/",
                "https://javadoc.io/doc/org.apache.commons/commons-lang3/${libs.versions.commonsLang3.get()}/",
                "https://javadoc.io/doc/org.slf4j/slf4j-api/${libs.versions.slf4j.get()}/",
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
