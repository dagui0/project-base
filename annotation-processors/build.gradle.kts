// annotation-processors/build.gradle.kts
plugins {
    id("java-library")
    alias(libs.plugins.lombok)
}

dependencies {

    implementation(project(":core-api"))

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.slf4j.api)
    implementation(libs.commons.csv)
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)

    // Lombok
    lombok(libs.lombok)

    // compile testing
    testImplementation(libs.lombok)
    testImplementation(libs.guava)
    testImplementation(libs.compile.testing)

    // junit
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
