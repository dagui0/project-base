// example-project/build.gradle.kts
plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.lombok)
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":core-library"))
    annotationProcessor(project(":annotation-processors"))
    testAnnotationProcessor(project(":annotation-processors"))

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)
    implementation(libs.flyway.core)
    implementation(libs.mapstruct)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    runtimeOnly(libs.h2)

    // Lombok
    lombok(libs.lombok)

    // Spring boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.thymeleaf)
    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.commons.compress)
    testImplementation(libs.spring.boot.testcontainers)

    // junit
    testImplementation(libs.spring.boot.junit.jupiter)
    testRuntimeOnly(libs.spring.boot.junit.platform.launcher)
}

tasks.withType<JavaCompile> {
    options.release.set(libs.versions.java.get().toInt())
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.test {
    useJUnitPlatform()
}
