plugins {
    id("java-library")
    alias(libs.plugins.lombok)
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":core-library"))
    testImplementation(project(":core-library"))

    // dependencies
    implementation(libs.jetbrains.annotations)
    implementation(libs.slf4j.api)

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

tasks.withType<JavaCompile> {
    options.release.set(libs.versions.java.get().toInt())
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.test {
    useJUnitPlatform()
}
