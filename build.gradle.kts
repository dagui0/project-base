// $projectRoot/build.gradle.kts
import java.net.URI

plugins {
    id("java")
    alias(libs.plugins.lombok)
}

group = "com.yidigun"
version = "0.0.1"

ext {
    set("java.version", libs.versions.java.get())
    set("project.info.dir", "generated/sources/project-info/java/main")
    set("project.info.package", "com.yidigun.base")
    set("publish.name", "Project Base Classes")
    set("publish.description", "Project base classes and utilities for common coding conventions")
    set("publish.url", "https://github.com/dagui0/project-base")
    set("publish.license", "The Apache License, Version 2.0")
    set("publish.license.url", "https://www.apache.org/licenses/LICENSE-2.0.txt")
    set("publish.developer.id", "dagui0")
    set("publish.developer.name", "Daekyu Lee")
    set("publish.developer.email", "dagui0@gmail.com")
    set("publish.scm.connection", "scm:git:https://github.com/dagui0/project-base.git")
    set("publish.scm.url", "https://github.com/dagui0/project-base")
    set("publish.repo.name", "nexus.yidigun.com")
    set("publish.repo.release.url", "https://nexus.yidigun.com/repository/maven-releases/")
    set("publish.repo.snapshot.url", "https://nexus.yidigun.com/repository/maven-snapshots/")
    set("private-repo.username", System.getenv("NEXUS_USERNAME") ?: "")
    set("private-repo.password", System.getenv("NEXUS_PASSWORD") ?: "")
}

dependencies {

    implementation(project(":core-api"))
    implementation(project(":core-library"))
    implementation(project(":annotation-processors"))
    annotationProcessor(project(":annotation-processors"))
    testAnnotationProcessor(project(":annotation-processors"))

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)

    // Lombok
    lombok(libs.lombok)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

val javadocSources = objects.fileCollection()
val javadocClasspath = objects.fileCollection()

tasks.register<Task>("prepareAllSources") {
    dependsOn(project(":core-api").tasks.named("prepareJavadoc"))
    dependsOn(project(":core-library").tasks.named("prepareJavadoc"))
}

tasks.javadoc {
    group = "documentation"
    description = "Generates aggregated Javadoc documentation for all modules."

    dependsOn("prepareAllSources")

    source.removeAll { true }
    classpath.removeAll { true }
    source(javadocSources)
    classpath += javadocClasspath

    javadocTool.set(javaToolchains.javadocToolFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javadoc.get().toInt()))
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

subprojects {
    group = rootProject.group
    version = rootProject.version

    tasks.withType<Javadoc> {
        enabled = false
    }

    tasks.register<Task>("prepareJavadoc") {
        if (project.name.startsWith("core-")) {
            javadocSources.from(project.sourceSets.main.get().allJava)
            javadocClasspath.from(project.sourceSets.main.get().compileClasspath)
        }
    }
}

allprojects {

    repositories {
        mavenCentral()
//        maven {
//            url = URI(rootProject.ext["publish.repo.release.url"].toString())
//            credentials {
//                username = rootProject.findProperty("nexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
//                password = rootProject.findProperty("nexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
//            }
//        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(libs.versions.java.get().toInt())
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<Jar> {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
    }
}
